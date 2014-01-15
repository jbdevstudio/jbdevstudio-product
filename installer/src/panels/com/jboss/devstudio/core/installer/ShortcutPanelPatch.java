package com.jboss.devstudio.core.installer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.izforge.izpack.installer.InstallData;
import com.izforge.izpack.installer.InstallerFrame;
import com.izforge.izpack.installer.IzPanel;
import com.izforge.izpack.installer.UninstallData;

@SuppressWarnings("serial")
public class ShortcutPanelPatch extends IzPanel {

	/* JBDS-2458: Files which should be uninstalled. 
	 * Assume we want to retain studio/jbdevstudio.ini and studio/configuration/, just in case user modified them post-install
	 * Should we also keep runtimes/ folder? If so will need a way to REMOVE files in uninstallData as it currently only supports add()
	 */
	protected String[] additionalUninstallableFiles = {
		/* files/folders in studio folder */
		"studio" + System.getProperty("file.separator") + "readme",
		"studio" + System.getProperty("file.separator") + "features",
		"studio" + System.getProperty("file.separator") + "plugins",
		"studio" + System.getProperty("file.separator") + "p2",
		"studio" + System.getProperty("file.separator") + "artifacts.xml",
		"studio" + System.getProperty("file.separator") + ".eclipseproduct",
		"studio" + System.getProperty("file.separator") + "epl-v10.html",
		"studio" + System.getProperty("file.separator") + "icon.xpm",
		"studio" + System.getProperty("file.separator") + "jbdevstudio",
		"studio" + System.getProperty("file.separator") + "notice.html",
		"studio" + System.getProperty("file.separator") + "runtime_locations.properties",
		"studio" + System.getProperty("file.separator") + "48-jbds_icon.png",
		"studio" + System.getProperty("file.separator") + "48-jbds_uninstall_icon.png",
		"studio" + System.getProperty("file.separator") + "jbds.ico",
		"studio" + System.getProperty("file.separator") + "jbds_uninstall.ico",

		/* files/folders in root folder */
		"Uninstaller",
		".installationinformation",
		"InstallConfigRecord.xml",
		"jbdevstudio",
		"jbdevstudio-unity",
		"JBoss-EULA.html",
		"readme.txt"
	};

	@Override
	public void panelActivate() {
		//System.out.println(idata.getVariables().toString().replaceAll(", ", ", \n"));
//		if(isUnixLikeSystem() && !OsVersion.IS_OSX) {
//			String name = idata.getVariable("DESKTOP_SHORTCUT_NAME");
//			StringBuffer cmd = new StringBuffer();
//
//			// if we have a shortcut in the install dir, make it executable
//			cmd.append("find \"" + idata.getVariable("INSTALL_PATH") + "\" \"" + getConfiguredDesktopLocation() + "\" -maxdepth 1 -mindepth 1 -type f -name \"" + name + "*.desktop\" -exec chmod +x {} \\;");
//			if (!cmd.toString().equals(""))
//			{
//				try {
//					System.out.println("Running script: `" + cmd + "` ...");
//					ShellScript.execute(cmd, File.createTempFile("jbds-desktop-chmod-", ".sh").getAbsolutePath());
//				} catch (IOException e) {
//					e.printStackTrace();
//				}		
//			}
//		}
		
		File file = new File(idata.getVariable("INSTALL_PATH"), "InstallConfigRecord.xml" );
        FileOutputStream out = null;
        BufferedOutputStream outBuff = null;
		try {
			out = new FileOutputStream(file);
			outBuff = new BufferedOutputStream(out, 5120);
			parent.writeXMLTree(this.idata.xmlData, outBuff);
			outBuff.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(outBuff!=null) {
					outBuff.close();
				}
				if(out!=null) {
					out.close();
				}
			} catch (IOException ignored) {
			}
		}
		addToUninstaller();

		parent.skipPanel();
	}

	/* JBDS-2458: make sure we're uninstalling everything that should be (except configuration data that might have changed post-install) */
	private void addToUninstaller()
	{
		for (int i = 0; i < additionalUninstallableFiles.length; i++)
		{
			addFilesToUninstaller(new File(idata.getVariable("INSTALL_PATH"), additionalUninstallableFiles[i]));
		}
	}

	public void addFilesToUninstaller(File root) {
		UninstallData uninstallData = UninstallData.getInstance();
		if (root.isDirectory())
		{
			File[] list = root.listFiles();
			for (File f : list) {
				if (f.isDirectory()) {
					//System.out.println("+ Dir: " + f.getAbsoluteFile());
					addFilesToUninstaller( f.getAbsoluteFile() );
				}
				else
				{
					//System.out.println( "+ File:" + f.getAbsoluteFile() );
					uninstallData.addFile(f.getAbsoluteFile().getAbsolutePath(), true);
				}
			}
		}
		else
		{
			//System.out.println( "+ File:" + root.getAbsoluteFile() );
			uninstallData.addFile(root.getAbsoluteFile().getAbsolutePath(), true);
		}
	}

	public ShortcutPanelPatch(InstallerFrame arg0, InstallData arg1) {
		super(arg0, arg1);
		setHidden(true);
	}

	/*
	 * If Unix like system return true
	 * 
	 * @return
	 */
//	private boolean isUnixLikeSystem() {
//		return System.getProperty("os.name","").toLowerCase().indexOf("win") == -1;
//	}
//	
//	public static final String X_CONFIG_FILE_PATH = "$HOME/.config/user-dirs.dirs";	
//	public static final String DEFAULT_DESKTOP_PATH = "$HOME/Desktop";
//	
//	public static String getConfiguredDesktopLocation() {
//		Properties configParams = new Properties();
//		String result = "";
//		InputStream configIo = null;
//		try {
//			configIo = new FileInputStream(resolveVariables(X_CONFIG_FILE_PATH));
//			configParams.load(configIo);
//			result = configParams.getProperty("XDG_DESKTOP_DIR");
//		} catch (FileNotFoundException e) {
//			// Empty string is returned
//		} catch (IOException e) {
//			// Empty string is returned
//		} finally {
//			try {
//				if(configIo!=null) {
//					configIo.close();
//				}
//			} catch (IOException e1) {
//				// Ignore. Nothing we could do about that
//			}
//			
//		}
//		return result==null || result == "" ? DEFAULT_DESKTOP_PATH : result;
//	}
//	
//	public static String resolveVariables(String desktopLocation) {
//		String result = "";
//		if(desktopLocation.contains("$")) {
//			StringBuffer script = new StringBuffer();
//			script.append("echo " + desktopLocation + ";");
//			try {
//				result = ShellScript.execAndDelete(script, File.createTempFile("jbds-desktop-location-", ".sh").getAbsolutePath());
//			} catch (IOException e) {
//				// ignore and return empty string
//			}
//		} else {
//			result = "";
//		}
//		return result.trim();
//	}
//	
//    /**
//     * Asks to make the XML panel data.
//     *
//     * @param panelRoot The tree to put the data in.
//     */
//    public void makeXMLData(IXMLElement panelRoot)
//    {
////        IXMLElement shortcutSpec = panelRoot.getFirstChildNamed("com.izforge.izpack.panels.ShortcutPanel");
////        IXMLElement group = shortcutSpec.getFirstChildNamed("programGroup");
//        
//    }
}
