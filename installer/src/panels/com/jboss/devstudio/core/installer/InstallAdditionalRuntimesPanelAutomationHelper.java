/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

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

/**
 * @author eskimo
 *
 */
public class InstallAdditionalRuntimesPanelAutomationHelper implements PanelAutomation {
	
	public static final String LOCATIONS_NODE_NAME = "locations";

	public void makeXMLData(AutomatedInstallData installData, IXMLElement panelRoot) {
		IXMLElement locations = new XMLElementImpl(LOCATIONS_NODE_NAME,panelRoot);
		locations.setContent(installData.getVariable(Unpacker.INSTALL_RT_LOCATIONS_VAR));
		panelRoot.addChild(locations);
	}

	/**
	 * Establish the runtime_locations.properties file based on the JBoss Application Server location
     * (if that panel is active) or minimally just the 'runtimes' directory.  This file is read at startup
     * and affects the runtime UI preferences.  Also set the install data runtime locations variable.
	 */
	public void runAutomated(AutomatedInstallData idata, IXMLElement panelRoot) throws InstallerException {
		IXMLElement locations = panelRoot.getFirstChildNamed(LOCATIONS_NODE_NAME);
		if (locations != null && locations.getContent() != null) {
			idata.setVariable(Unpacker.INSTALL_RT_LOCATIONS_VAR, locations.getContent());
		}
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

		File file = new File(folder, "runtime_locations.properties");
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
}
