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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.izforge.izpack.adaptator.IXMLElement;
import com.izforge.izpack.gui.IzPanelLayout;
import com.izforge.izpack.installer.InstallData;
import com.izforge.izpack.installer.InstallerFrame;
import com.izforge.izpack.installer.IzPanel;
import com.izforge.izpack.installer.PanelAutomation;
import com.jboss.devstudio.core.installer.bean.RuntimeServer;

/**
 * This panel will be skipped if there are no additional runtime servers to install.
 *
 * How to define additional runtimes (i.e.):
 * AdditionalRuntimesSpec.json
 * [
 * 	{
 *          "label"       : "Fuse Runtime", 
 *          "description" : "Fuse Runtime 6.3.0",
 *          "path"        : "devstudio-is/runtime/jboss-fuse-6.3.0.redhat-077",
 *          "selected"    : "true"
 *      }
 * ]
 *
 */
public class InstallAdditionalRuntimesPanel extends IzPanel {
	
	private RuntimeServerListPanel RTList;
	private List<RuntimeServer> defaultRTs = loadRTs("DevstudioRuntimesSpec.json");
	private List<RuntimeServer> additionalRTs = loadRTs("AdditionalRuntimesSpec.json");
	
	/**
	 * Install supplemental runtime servers panel definition.
	 * @param parent
	 * @param idata
	 */
	public InstallAdditionalRuntimesPanel(InstallerFrame parent, InstallData idata) {
		super(parent, idata, new IzPanelLayout());
		JLabel label = new JLabel(parent.langpack.getString("RuntimesSelectPanel.statement1"));
		add(label, NEXT_LINE);
		RTList = new RuntimeServerListPanel(parent.langpack, defaultRTs, additionalRTs);
		add(RTList,NEXT_LINE);
		setHidden(additionalRTs.isEmpty());
	}

	/**
	 * Load the supplemental runtime servers defined by the JSON file.
	 * @param resourceName
	 * @return
	 */
	public List<RuntimeServer> loadRTs(String resourceName) {
		List<RuntimeServer> rts = new ArrayList<RuntimeServer>();
		try {
			InputStream runtimes = this.parent.getResource(resourceName);
			JsonParser parser = new JsonParser();
			JsonArray array = parser.parse(new InputStreamReader(runtimes)).getAsJsonArray();
			Gson gson = new Gson();
			for (JsonElement jsonElement : array) {
				RuntimeServer rt = gson.fromJson(jsonElement, RuntimeServer.class);
				rts.add(rt);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rts;
	}
	
	@Override
	public void panelActivate() {
		if (additionalRTs.isEmpty())
			parent.skipPanel();
	}
	
	@Override
	public void panelDeactivate() {
		idata.setVariable("INSTALL_RT_LOCATIONS", RTList.getRTListBean().getCommaSeparatedLocationStringList());
		idata.setVariable("INSTALL_RT_SIZES", RTList.getRTListBean().getCommaSeparatedSizeStringList());
	}
	
	@Override
	public void makeXMLData(IXMLElement panelRoot) {
		PanelAutomation helper = new InstallAdditionalRuntimesPanelAutomationHelper();
		helper.makeXMLData(this.idata, panelRoot);
	}

}
