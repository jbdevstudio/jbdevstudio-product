package com.jboss.devstudio.core.installer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import com.izforge.izpack.adaptator.IXMLElement;
import com.izforge.izpack.adaptator.impl.XMLElementImpl;
import com.izforge.izpack.installer.AutomatedInstallData;
import com.izforge.izpack.installer.InstallerException;
import com.izforge.izpack.installer.PanelAutomation;
import com.izforge.izpack.installer.Unpacker;
import com.izforge.izpack.util.Debug;
import com.jboss.devstudio.core.installer.bean.RuntimePath;

public class CreateLinkPanelAutomationHelper implements PanelAutomation {
	
	public static final String LOCATIONS_NODE_NAME = "locations";
	
	public static void writeProperty(AutomatedInstallData idata, String fileName) {
	   	String installPath = idata.getVariable("INSTALL_PATH");
		File folder = new File(installPath, P2DirectorStarterListener.DEVSTUDIO_LOCATION);

		Properties servers = (Properties) idata.getAttribute("AS_SERVERS");
		if (servers == null || servers.isEmpty()) {
 			servers = new Properties();
 			File runtimesFolder = new File (installPath, "runtimes");
 			RuntimePath runtimesPath = new RuntimePath(runtimesFolder.getAbsolutePath(), true);
 			servers.put("server1", runtimesPath.toString());
 		}

		if (!folder.exists())
			folder.mkdirs();

		File file = new File(folder, fileName);
		FileOutputStream stream = null;
		try {
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

	public void makeXMLData(AutomatedInstallData iData, IXMLElement panelRoot) {
		IXMLElement jreLocation = new XMLElementImpl("jrelocation", panelRoot);
		// check this writes even if value is the default,
		// because without the constructor, default does not get set.
		String jdkPath = iData.getVariable("JREPath");
		String index = CreateLinkPanel.validatePath(jdkPath);

		if (index.length()>0) {
			jreLocation.setContent(jdkPath + File.separator + index);
			panelRoot.addChild(jreLocation);
		}
	}
	
	/**
	 * Establish the runtime_locations.properties file containing minimally the 'runtimes' directory.  
	 * This file is read at startup and affects the runtime UI preferences.  Also set the install data
	 * runtime locations variable and update the installtion path to contain the JRE path.
	 */
	public void runAutomated(AutomatedInstallData idata, IXMLElement panelRoot)
		throws InstallerException {
		IXMLElement locations = panelRoot.getFirstChildNamed(LOCATIONS_NODE_NAME);
		if (locations != null && locations.getContent() != null) {
			idata.setVariable(Unpacker.INSTALL_RT_LOCATIONS_VAR, locations.getContent());
		}
		writeProperty(idata, "runtime_locations.properties");
		CreateLinkPanel.createSoftLink(idata.getInstallPath());
		IXMLElement ipath = panelRoot.getFirstChildNamed("jrelocation");
		if(ipath!=null) {
			String path = ipath.getContent();
			CreateLinkPanel.addJREPath(idata.getVariable("INSTALL_PATH"), path);
		}
	}

}
