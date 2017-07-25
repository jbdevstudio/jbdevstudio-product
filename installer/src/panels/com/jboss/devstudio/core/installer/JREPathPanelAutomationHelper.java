package com.jboss.devstudio.core.installer;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.Properties;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.izforge.izpack.adaptator.IXMLElement;
import com.izforge.izpack.installer.AutomatedInstallData;
import com.izforge.izpack.installer.InstallData;
import com.izforge.izpack.installer.InstallerException;
import com.izforge.izpack.installer.PanelAutomation;
import com.izforge.izpack.util.OsVersion;
import com.izforge.izpack.util.Debug;
import com.jboss.devstudio.core.installer.JREPathValidator.ValidationCode;

public class JREPathPanelAutomationHelper implements PanelAutomation {

	JREPathPanelMessages messages;
	JREPathValidator validator;
	PrintStream out;
	
	// this constructor executed during automated execution
	public JREPathPanelAutomationHelper() {
		this(new JREPathPanelMessages(),new JREPathValidator(),System.out);
	}

	public JREPathPanelAutomationHelper(JREPathPanelMessages jrePathPanelMessages, JREPathValidator validator) {
		this(jrePathPanelMessages, validator, System.out);
	}
	
	public JREPathPanelAutomationHelper(JREPathPanelMessages messages, JREPathValidator validator, PrintStream out) {
		this.messages = messages;
		this.validator = validator;
		this.out = out;
	}

	public void makeXMLData(AutomatedInstallData arg0, IXMLElement arg1) {
		// installer saves JVM location in new CreateLinkPanelAutomationHelper class
	}

	public void runAutomated(AutomatedInstallData installData, IXMLElement xmlData) throws InstallerException {
		// initialization is required in automation mode 
		messages.setLocaleDatabase(installData.langpack);
		NodeList panelNodes = xmlData.getElement().getParentNode().getChildNodes();
		for(int i=0;i<panelNodes.getLength();i++) {
			Node panelNode = panelNodes.item(i);
			if(panelNode.getNodeName().equals(CreateLinkPanel.class.getName())) {
				NodeList childNodes = panelNode.getChildNodes();
				for(int j=0; j<childNodes.getLength();j++) {
					Node childNode = childNodes.item(j);
					if("jrelocation".equals(childNode.getNodeName())) {
						if(childNode.getTextContent().trim().equals("")) {
							throw new InstallerException("[ERROR] jrelocation node value cannot be empty");
						}
						String jreLocation = childNode.getTextContent();
						// jrelocation tag stores full path to java 
						// ${java.home}/bin/java
						// Validate java executable location
						if(!isValidJavaExecutable(jreLocation)) {
							// Fallback to resolving cannonical path in case of link uder linux
							try {
								jreLocation = new File(jreLocation).getCanonicalPath();
								if (!isValidJavaExecutable(jreLocation)) {
									// link doesn't point to right location
									throw new InstallerException(MessageFormat.format("[ERROR] configured java executable ''{0}'' doesn't match regular location inside home directory",jreLocation));
								}
							} catch (IOException e) {
								// link destination is unresolvable 
								throw new InstallerException(MessageFormat.format("[ERROR] cannot get canonical path to configured java executable ''{0}''",jreLocation), e);
							}
						}
						Debug.trace("[DEBUG] runAutomated() jreLocation = " + jreLocation);
						String jreHome = new File(jreLocation)
										.getParentFile() // bin
										.getParentFile() // ${java.home}
										.getAbsolutePath();
						Debug.trace("[DEBUG] (1) runAutomated() JREPathPanel.VAR_SELECTED_JAVA_PATH = " + JREPathPanel.VAR_SELECTED_JAVA_PATH);
						Debug.trace("[DEBUG] (1) runAutomated() jreHome = " + jreHome);
						installData.setVariable(JREPathPanel.VAR_SELECTED_JAVA_PATH, jreHome);
						validateSelectedJavaPath(installData);
						return;
					}
				}
			}
		}
		// No jrelocation node found, current jvm should be used to match GUI installer behavior
		updateJavaHomeVariable(installData);
		Debug.trace("[DEBUG] (2) runAutomated() JREPathPanel.VAR_SELECTED_JAVA_PATH = " + JREPathPanel.VAR_SELECTED_JAVA_PATH);
		Debug.trace("[DEBUG] (2) runAutomated() installData.getVariable(JREPathPanel.VAR_JAVA_HOME) = " + installData.getVariable(JREPathPanel.VAR_JAVA_HOME));
		installData.setVariable(JREPathPanel.VAR_SELECTED_JAVA_PATH, installData.getVariable(JREPathPanel.VAR_JAVA_HOME));
		validateSelectedJavaPath(installData);
	}
	
	private void validateSelectedJavaPath(AutomatedInstallData installData) throws InstallerException {
		Properties props = new Properties();
		String jreLocation = installData.getVariable(JREPathPanel.VAR_SELECTED_JAVA_PATH);
		ValidationCode code = validator.runAndVerifyVersion(jreLocation,props);

		messages.printJvmInfo(out, jreLocation, props);
		
		String errorMessage = stripHtml(messages.getValidationMessageText(code));
		if(!code.isError()) {
			if (code != ValidationCode.OK) {
				this.out.println("[WARNING] " + errorMessage);
			}
			ValidationCode dcode = validator.verifyJavaDistributionType(jreLocation);

			if (dcode != ValidationCode.OK) {
				this.out.println("[WARNING] " + stripHtml(messages.getJavaDistributionValidationMessage(dcode)));
			}
		} else {
			throw new InstallerException("[ERROR] " + errorMessage);
		}
	}
	
	public void updateJavaHomeVariable(AutomatedInstallData idata) {
		File javaHome = validator.getDefaultJavaLocation(idata.getVariable(JREPathPanel.VAR_JAVA_HOME));
		Debug.trace("[DEBUG] updateJavaHomeVariable() " + javaHome.toString());
		idata.setVariable(JREPathPanel.VAR_JAVA_HOME, javaHome.getAbsolutePath());
		// This case is for starting installer with jdk/bin/java under any
		// platform
		Properties props = validator.runJavaAndGetPlatformProperties(javaHome.getAbsolutePath(), new String[2]);
		if ("jre".equals(javaHome.getName())) {
			File parentFolder = javaHome.getParentFile();
			File bin = new File(parentFolder, "bin");
			String ext = OsVersion.IS_WINDOWS ? ".exe" : "";
			File java = new File(bin, "java" + ext);
			File javac = new File(bin, "javac" + ext);
			if (javac.canRead() && java.canRead()) {
				idata.setVariable(JREPathPanel.VAR_JAVA_HOME, parentFolder.getAbsolutePath());
			}
		} else if (javaHome.getName().matches("jre\\d")) {
			// try to discover windows jdk
			// c:\Program Files\Java\jdk${java_version}
			String javaVersion = (String) props.get(JREPathPanel.SYSPN_JAVA_VERSION);
			if (javaVersion != null) {
				File parentFolder = javaHome.getParentFile();
				File jdkLocation = new File(parentFolder, "jdk" + javaVersion);
				File bin = new File(jdkLocation, "bin");
				File java = new File(bin, "java.exe");
				File javac = new File(bin, "javac.exe");
				if (java.canRead() && javac.canRead()) {
					idata.setVariable(JREPathPanel.VAR_JAVA_HOME, jdkLocation.getAbsolutePath());
				}
			}
		}
	}

	private String stripHtml(String text) {
		return text
			.replaceAll("<html><p>","")
			.replaceAll("</p></html>", "")
			.replaceAll("<br>", "");
	}
	
	private boolean isValidJavaExecutable(String path) {
		// Usual location for java inside home folder is 
		// For Linux and mac:
		//    bin/java
		//    link to java in java home directory
		// For Windows
		//    bin/java.exe
		//    bin/javaw.exe

		File javaExec = new File(path);
		if (javaExec.canRead()) {
			File javaBin = javaExec.getParentFile();
			if (javaBin.getName().equals("bin")) {
				// this is probably right location inside java home director
				String javaExecName = javaExec.getName();
				if( javaExecName.equals("java") 
					|| (OsVersion.IS_WINDOWS 
						&& javaExecName.equals("javaw.exe")
						|| javaExecName.equals("java.exe"))) {
					return true;
				}
			}
		}
		return false;
	}
}
