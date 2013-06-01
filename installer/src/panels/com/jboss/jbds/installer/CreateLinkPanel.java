package com.jboss.jbds.installer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import com.izforge.izpack.adaptator.IXMLElement;
import com.izforge.izpack.installer.AutomatedInstallData;
import com.izforge.izpack.installer.InstallData;
import com.izforge.izpack.installer.InstallerFrame;
import com.izforge.izpack.installer.IzPanel;
import com.izforge.izpack.installer.PanelAutomation;
import com.izforge.izpack.util.OsVersion;
import com.izforge.izpack.util.os.unix.ShellScript;

public class CreateLinkPanel extends IzPanel {

	private static final long serialVersionUID = 1256443616359329171L;
	private static final String winTestFiles[] = new String[] { "bin" + File.separator + "javaw.exe",
			"jre" + File.separator + "bin" + File.separator + "javaw.exe" };
	private static final String linTestFiles[] = new String[] { "bin" + File.separator + "java",
		"jre" + File.separator + "bin" + File.separator + "java" };

	public CreateLinkPanel(InstallerFrame parent, InstallData idata) {
		super(parent, idata);
		setHidden(true);
	}

	static String validatePath(String jdkPath) {
		String testFiles[] = new String[0];
		if (OsVersion.IS_WINDOWS) {
			testFiles = winTestFiles;
		} else if (OsVersion.IS_LINUX || OsVersion.IS_MAC) {
			testFiles = linTestFiles;
		}
		
		if (jdkPath != null && !"".equals(jdkPath)) {
			for (int i = 0; i < testFiles.length; ++i) {
				File path = new File(jdkPath, testFiles[i]).getAbsoluteFile();
				if (path.exists()) {
					return testFiles[i];
				}
			}
		}
		return "";
	}

	public void panelActivate() {
		File install = new File(idata.getVariable("INSTALL_PATH"));
		File studio = new File(install , "studio");
		File app = new File(studio,"jbdevstudio.app");
		if(app.exists()) {
			app.renameTo(new File(studio,"JBoss Developer Studio.app"));
		}
		createSoftLink();
		writeProperty("runtime_locations.properties");
		addJREPath();

		parent.skipPanel();
		parent.lockPrevButton();
	}

	private void createSoftLink() {
		createSoftLink(idata.getVariable("INSTALL_PATH"));
	}

	public static void createSoftLink(String installPath) {
		if(isUnixLikeSystem()) {

			StringBuffer cmd = new StringBuffer();
			String launcherLocation = installPath+File.separator+"studio";
			String launcherName = "jbdevstudio";
			if(OsVersion.IS_OSX) {
				File oldLauncher = new File(launcherLocation,"JBoss Developer Studio.app");
				if(oldLauncher.exists()) {
					 launcherName = "JBoss Developer Studio.app";
				} else {
					launcherName = "jbdevstudio.app";
				}
			} 
			cmd.append("cd \"").append(installPath).append("\"\n")
				.append("ln -s \"." + File.separator + "studio" + File.separator)
				.append(launcherName + "\"")
				.append(" \"" + launcherName + "\"");
			try {
				ShellScript.execute(cmd, File.createTempFile("jbds-launcher-link-", ".sh").getAbsolutePath());
			} catch (IOException e) {
				e.printStackTrace();
			}		
		}
	}
	/*
	 * If Unix like system return true
	 * 
	 * @return
	 */
	public static boolean isUnixLikeSystem() {
		return System.getProperty("os.name","").toLowerCase().indexOf("win") == -1;
	}
	
	public void writeProperty(String fileName) {
		String installPath = idata.getVariable("INSTALL_PATH");

		File folder = new File(installPath, "studio");

		Properties servers = (Properties) idata.getAttribute("AS_SERVERS");
		if (!servers.isEmpty()) {
			if (!folder.exists()) {
				folder.mkdirs();
			}
			File file = new File(folder, fileName);
			FileOutputStream stream = null;
			try {
				file.createNewFile();
				stream = new FileOutputStream(file);
				servers.store(stream, null);
				stream.flush();
				stream.close();
			} catch (IOException e) {
				if (stream != null) {
					try {
						stream.close();
					} catch (IOException e1) {
					}
				}
				e.printStackTrace();
			}
		}
	}

	public void createLink(String fileName, String folderName) {
		String installPath = idata.getVariable("INSTALL_PATH");
		String path;
		path = installPath + File.separator + "eclipse" + File.separator
				+ "links";

		File folder = new File(path);

		try {
			if (!folder.exists())
				folder.mkdir();
			path += File.separator + fileName;
			File file = new File(path);
			String str = "path=" + installPath.replace("\\", "/") + folderName;
			FileOutputStream stream = new FileOutputStream(file);
			stream.write(str.getBytes());
			stream.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void addJREPath() {
		String installPath = idata.getVariable("INSTALL_PATH");
		String jdkPath = idata.getVariable("JREPath");
		String index = validatePath(jdkPath!=null && !"".equals(jdkPath)? jdkPath : idata.getVariable("JAVA_HOME"));

		if (index.length()>0) {
			addJREPath(installPath, jdkPath + File.separator + index);
		}
	}

	public static void addJREPath(String installPath, String execPath) {
		File pathOld = new File(installPath + File.separator + "eclipse" + File.separator
				+ "eclipse.ini");
		File pathNew1 = new File(installPath + File.separator + "studio" + File.separator
				+ "jbdevstudio.ini");
		File pathNew2 = new File(installPath + File.separator + "studio" + File.separator  
				+ "JBoss Developer Studio.app" + File.separator + "Contents" + File.separator +"MacOS" + File.separator
				+ "JBoss Developer Studio.ini");
		File pathNew3 = new File(installPath + File.separator + "studio" + File.separator  
				+ "jbdevstudio.app" + File.separator + "Contents" + File.separator +"MacOS" + File.separator
				+ "jbdevstudio.ini");
		File pathNew4 = new File(installPath + File.separator + "studio" + File.separator  
				+ "JBoss Developer Studio.app" + File.separator + "Contents" + File.separator +"MacOS" + File.separator
				+ "jbdevstudio.ini");
		if(pathOld.exists() ) {
			addJVM(execPath, pathOld);
		} else if(pathNew1.exists()) {
			addJVM(execPath, pathNew1);
		} else if(pathNew2.exists()) {
			addJVM(execPath, pathNew2);
			if(pathNew3.exists()) {
				addJVM(execPath, pathNew3);
			}
		} else if(pathNew3.exists()) {
			addJVM(execPath, pathNew3);
		} else if(pathNew4.exists()) {
			addJVM(execPath, pathNew4);
		} 
		
	}

	public static void addJVM(String execPath, File path) {
		FileInputStream is = null;
		FileOutputStream stream = null;
		byte[] b = new byte[0];
		try {
			is = new FileInputStream(path);
			b = new byte[is.available()];
			is.read(b);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if(is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		if(b.length != 0) {
			try {
				String str = new String(b);
				str = "-vm\n" + execPath + "\n"
						+ str;
				stream = new FileOutputStream(path);
				stream.write(str.getBytes());
			} catch (IOException ex) {
				ex.printStackTrace();
			} finally {
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Asks to make the XML panel data.
	 * 
	 * @param panelRoot
	 *            The tree to put the data in.
	 */
	public void makeXMLData(IXMLElement panelRoot) {
		PanelAutomation helper = new CreateLinkPanelAutomationHelper();
		helper.makeXMLData(this.idata, panelRoot);
	}
}
