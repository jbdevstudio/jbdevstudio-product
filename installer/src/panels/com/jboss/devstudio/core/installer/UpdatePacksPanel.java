package com.jboss.devstudio.core.installer;


import com.izforge.izpack.installer.InstallData;
import com.izforge.izpack.installer.InstallerFrame;
import com.izforge.izpack.installer.IzPanel;

public class UpdatePacksPanel extends IzPanel{
	
	UpdatePacksPanelConsoleHelper helper = new UpdatePacksPanelConsoleHelper();

	private static final long serialVersionUID = 1256443616359329170L;

	// FIXME: Calculate real size by artifacts.xml to have exact number
	// JBDS-3319 use a larger default size of 660M instead of 420M (JBDS-2010)
	static final long STUDIO_SIZE = 660*1024L*1024;
	
	public UpdatePacksPanel(InstallerFrame parent, InstallData idata) {
		super(parent, idata);
		setHidden(true);
	}
	
	public void panelActivate() {
		helper.runConsole(idata);
		parent.skipPanel();
	}
	
	public String getSummaryBody()
    {
		String group = idata.getVariable("INSTALL_GROUP");
		String version = idata.getVariable("VERSION");
		String eapVersion = idata.getVariable("EAP_VERSION");		
		StringBuffer buffer = new StringBuffer();
		buffer.append("Red Hat JBoss Developer Studio "+version+"<br>");
		if("jbosseap".equals(group))
			buffer.append("Red Hat JBoss Enterprise Application Platform " + eapVersion+ "<br>");
		
        return buffer.toString();
    }
}
