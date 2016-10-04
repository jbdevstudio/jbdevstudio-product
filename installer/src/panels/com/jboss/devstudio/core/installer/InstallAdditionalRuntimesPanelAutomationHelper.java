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

import com.izforge.izpack.adaptator.IXMLElement;
import com.izforge.izpack.adaptator.impl.XMLElementImpl;
import com.izforge.izpack.installer.AutomatedInstallData;
import com.izforge.izpack.installer.InstallerException;
import com.izforge.izpack.installer.PanelAutomation;
import com.izforge.izpack.installer.Unpacker;

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

	public void runAutomated(AutomatedInstallData idata, IXMLElement panelRoot) throws InstallerException {
		IXMLElement locations = panelRoot.getFirstChildNamed(LOCATIONS_NODE_NAME);
		if (locations != null && locations.getContent() != null) {
			idata.setVariable(Unpacker.INSTALL_RT_LOCATIONS_VAR, locations.getContent());
		}
	}

}
