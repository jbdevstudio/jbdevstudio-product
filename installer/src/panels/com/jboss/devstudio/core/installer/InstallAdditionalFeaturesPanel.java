/**
 * 
 */
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
import com.jboss.devstudio.core.installer.bean.P2IU;
import com.jboss.devstudio.core.installer.bean.P2IUListBean;

/**
 * It should be skipped if there are no additional IU's to install
 * how to define IU's:
 * additional-ius.json
 * [
 * 	{"id":"id1", "label":"Fuse", "description":"Description text if we need one"},
 * 	{"id":"id2", "label":"Something else 1", "description":"Description text if we need one"},
 * 	{"id":"id3", "label":Something else 2", "description":"Description text if we need one"}
 * ]
 * 
 * How to make sure there are all required dependencies included that is not inside
 * devstudio installer
 * 
 * @author eskimo
 *
 */
public class InstallAdditionalFeaturesPanel extends IzPanel {
	
	private P2IUListPanel iuList;
	private List<P2IU> defaultIUs = loadIUs("DevstudioFeaturesSpec.json");
	private List<P2IU> additionalIUs = loadIUs("AdditionalFeaturesSpec.json");
	public InstallAdditionalFeaturesPanel(InstallerFrame parent, InstallData idata) {
		super(parent, idata,new IzPanelLayout());
		JLabel label = new JLabel(parent.langpack.getString("JBossAsSelectPanel.statement1"));
		add(label, NEXT_LINE);
		iuList = new P2IUListPanel(parent.langpack,defaultIUs,additionalIUs);
		add(iuList,NEXT_LINE);
		setHidden(additionalIUs.isEmpty());
	}

	public List<P2IU> loadIUs(String resourceName) {
		List<P2IU> ius = new ArrayList<P2IU>();
		try {
			InputStream features = this.parent.getResource(resourceName);
			JsonParser parser = new JsonParser();
			JsonArray array = parser.parse(new InputStreamReader(features)).getAsJsonArray();
			Gson gson = new Gson();
			for (JsonElement jsonElement : array) {
				P2IU iu = gson.fromJson(jsonElement, P2IU.class);
				ius.add(iu);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ius;
	}
	
	@Override
	public void panelActivate() {
		if(additionalIUs.isEmpty()) {
			parent.skipPanel();
		}
	}
	
	@Override
	public void panelDeactivate() {
		P2IUListBean iuLB = iuList.getIUListBean();
		
		idata.setVariable("INSTALL_IUS", iuLB.getCommaSeparatedIUStringList());
		idata.setVariable("INSTALL_P2_LOCATIONS", iuLB.getCommaSeparatedLocationStringList());	
		idata.setVariable("INSTALL_ADDITIONAL_SIZES", iuLB.getCommaSeparatedSizeStringList());
	}
	
	@Override
	public void makeXMLData(IXMLElement panelRoot) {
		PanelAutomation helper = new InstallAdditionalFeaturesPanelAutomationHelper();
		helper.makeXMLData(this.idata, panelRoot);
	}

}
