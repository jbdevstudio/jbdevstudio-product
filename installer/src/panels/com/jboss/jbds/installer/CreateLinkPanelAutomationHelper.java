package com.jboss.jbds.installer;

import java.io.File;

import com.izforge.izpack.adaptator.IXMLElement;
import com.izforge.izpack.adaptator.impl.XMLElementImpl;
import com.izforge.izpack.installer.AutomatedInstallData;
import com.izforge.izpack.installer.InstallerException;
import com.izforge.izpack.installer.PanelAutomation;

public class CreateLinkPanelAutomationHelper implements PanelAutomation {

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

	public void runAutomated(AutomatedInstallData idata, IXMLElement panelRoot)
		throws InstallerException {
		CreateLinkPanel.createSoftLink(idata.getInstallPath());
		IXMLElement ipath = panelRoot.getFirstChildNamed("jrelocation");
		if(ipath!=null) {
			String path = ipath.getContent();
			CreateLinkPanel.addJREPath(idata.getVariable("INSTALL_PATH"), path);
		}
	}

}
