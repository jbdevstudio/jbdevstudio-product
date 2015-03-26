package com.jboss.devstudio.core.installer;

import com.izforge.izpack.Pack;
import com.izforge.izpack.adaptator.IXMLElement;
import com.izforge.izpack.adaptator.impl.XMLElementImpl;
import com.izforge.izpack.installer.AutomatedInstallData;
import com.izforge.izpack.installer.InstallerException;
import com.izforge.izpack.installer.PanelAutomation;

public class JBossAsSelectPanelAutomationHelper implements PanelAutomation {

	public void makeXMLData(AutomatedInstallData idata, IXMLElement panelRoot) {
		IXMLElement group = new XMLElementImpl("installgroup", panelRoot);
		// check this writes even if value is the default,
		// because without the constructor, default does not get set.
		group.setContent(idata.getVariable("INSTALL_GROUP"));
		panelRoot.addChild(group);
	}

	public void runAutomated(AutomatedInstallData idata, IXMLElement panelRoot) throws InstallerException {
		IXMLElement ipath = panelRoot.getFirstChildNamed("installgroup");
		// Allow for variable substitution of the installpath value
		String group = ipath.getContent();
		idata.selectedPacks.clear();
		for (Pack pack : idata.availablePacks) {
			if (pack.installGroups.contains(group) && !idata.selectedPacks.contains(pack)) {
				idata.selectedPacks.add(pack);
			}
		}
	}
}
