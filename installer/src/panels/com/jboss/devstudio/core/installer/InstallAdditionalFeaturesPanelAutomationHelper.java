/**
 * 
 */
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
public class InstallAdditionalFeaturesPanelAutomationHelper implements PanelAutomation {
	
	public static final String LOCATIONS_NODE_NAME = "locations";
	public static final String IUS_NODE_NAME = "ius";

	@Override
	public void makeXMLData(AutomatedInstallData installData, IXMLElement panelRoot) {
		IXMLElement ius = new XMLElementImpl(IUS_NODE_NAME,panelRoot);
		IXMLElement locations = new XMLElementImpl(LOCATIONS_NODE_NAME,panelRoot);
		ius.setContent(installData.getVariable(Unpacker.INSTALL_IUS_VAR));
		locations.setContent(installData.getVariable(Unpacker.INSTALL_P2_LOCATIONS_VAR));
		panelRoot.addChild(ius);
		panelRoot.addChild(locations);
	}

	@Override
	public void runAutomated(AutomatedInstallData idata, IXMLElement panelRoot) throws InstallerException {
		IXMLElement ius = panelRoot.getFirstChildNamed(IUS_NODE_NAME);
		IXMLElement locations = panelRoot.getFirstChildNamed(LOCATIONS_NODE_NAME);
		if(ius != null ) {
			idata.setVariable(Unpacker.INSTALL_IUS_VAR, ius.getContent());
		}
		if(locations != null) {
			idata.setVariable(Unpacker.INSTALL_P2_LOCATIONS_VAR, locations.getContent());
		}
	}

}
