package com.jboss.devstudio.core.installer;

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
import com.izforge.izpack.util.Debug;
import com.izforge.izpack.util.OsVersion;
import com.izforge.izpack.util.os.unix.ShellScript;

public class CreateLinkPanel extends IzPanel {

	private static final long serialVersionUID = 1256443616359329171L;
	private static final String winTestFiles[] = new String[] { "bin" + File.separator + "javaw.exe",
			"jre" + File.separator + "bin" + File.separator + "javaw.exe" };
	private static final String linTestFiles[] = new String[] { "bin" + File.separator + "java",
		"jre" + File.separator + "bin" + File.separator + "java" };
	private String installPath = "";

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
		installPath = idata.getVariable("INSTALL_PATH");
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
			String launcherName = OsVersion.IS_OSX ? "devstudio.app" : "devstudio";
			 
			cmd.append("cd \"").append(installPath).append("\"\n")
				.append("ln -s \"." + File.separator + "studio" + File.separator)
				.append(launcherName + "\"")
				.append(" \"" + launcherName + "\"");
			try {
				ShellScript.execute(cmd, File.createTempFile("devstudio-launcher-link-", ".sh").getAbsolutePath());
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

		File folder = new File(installPath, P2DirectorStarterListener.DEVSTUDIO_LOCATION);

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
				e.printStackTrace();
			} finally {
				if (stream != null) {
					try {
						stream.close();
					} catch (IOException e1) {
						Debug.trace(e1);
					}
				}
			}
		}
	}

	public void createLink(String fileName, String folderName) {
		String path = installPath + File.separator + "eclipse" + File.separator
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
		} catch (IOException ex) {
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
		File pathToIni = new File(installPath + File.separator + P2DirectorStarterListener.DEVSTUDIO_LOCATION + File.separator
				+ "devstudio.ini");
		if(pathToIni.exists()) {
			addJVM(execPath, pathToIni);
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
					Debug.trace(e);  
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
					Debug.trace(e);
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
