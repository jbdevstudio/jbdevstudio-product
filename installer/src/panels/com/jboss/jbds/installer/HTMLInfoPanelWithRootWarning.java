package com.jboss.jbds.installer;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.izforge.izpack.installer.InstallData;
import com.izforge.izpack.installer.InstallerFrame;
import com.izforge.izpack.panels.HTMLInfoPanel;

public class HTMLInfoPanelWithRootWarning extends HTMLInfoPanel {

	private static final String WARNING_TITLE_TXT = "HTMLInfoPanelWithRootWarning.warning";
	private static final String ROOT_WARNING_TXT = "HTMLInfoPanelWithRootWarning.rootUserWarning";

	public HTMLInfoPanelWithRootWarning(InstallerFrame parent, InstallData idata) {
		super(parent, idata);
	}

	public HTMLInfoPanelWithRootWarning(InstallerFrame parent,
			InstallData idata, String resPrefixStr, boolean showInfoLabelFlag) {
		super(parent, idata, resPrefixStr, showInfoLabelFlag);
	}

	public void panelActivate() {
		super.panelActivate();
		if(System.getProperty("os.name","").toLowerCase().indexOf("win") == -1) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					if("root".equals(System.getProperty("user.name", ""))) {
						JOptionPane.showMessageDialog(
								HTMLInfoPanelWithRootWarning.this, 
								HTMLInfoPanelWithRootWarning.this.parent.langpack.getString(ROOT_WARNING_TXT),
								HTMLInfoPanelWithRootWarning.this.parent.langpack.getString(WARNING_TITLE_TXT),
								JOptionPane.WARNING_MESSAGE);
					}
				}
			});
		}
		parent.doLayout();
	}
}
