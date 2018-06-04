package com.jboss.devstudio.core.installer;


import com.izforge.izpack.installer.InstallData;
import com.izforge.izpack.installer.InstallerFrame;
import com.izforge.izpack.installer.IzPanel;

public class UpdatePacksPanel extends IzPanel{
	
	UpdatePacksPanelConsoleHelper helper = new UpdatePacksPanelConsoleHelper();

	private static final long serialVersionUID = 1256443616359329170L;

	// FIXME: Calculate real size by artifacts.xml to have exact number
	// JBDS-3319 use a larger default size of 660M instead of 420M (JBDS-2010)
	static final long STUDIO_SIZE = 747*1024L*1024;
	
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
		StringBuffer buffer = new StringBuffer();
		buffer.append("Red Hat Developer Studio "+version+"<br>");
		
		// Summarize any additional features and runtimes.
		String additionalSummaryInfo = "";
		String installIUs = idata.getVariable("INSTALL_IUS");
		if (installIUs != null) {
			if (installIUs.contains("devstudio.fuse"))
				additionalSummaryInfo = additionalSummaryInfo.concat("Red Hat Red Hat Fuse Tooling<br>");
			if (installIUs.contains("integration-stack.bpr"))
				additionalSummaryInfo = additionalSummaryInfo.concat("Red Hat JBoss Business Process and Rules Development<br>");
			if (installIUs.contains("integration-stack.ds"))
				additionalSummaryInfo = additionalSummaryInfo.concat("Red Hat JBoss Data Virtualization<br>");
			if (installIUs.contains("integration-stack.soa"))
				additionalSummaryInfo = additionalSummaryInfo.concat("Red Hat JBoss Integration and SOA Development<br>");
		}

		String installRTLocs = idata.getVariable("INSTALL_RT_LOCATIONS");
		if (installRTLocs != null) {
	    	String[] rtl = installRTLocs.split(",");
	    	for (int i = 0; i < rtl.length; i++) {		
	    		String[] rtlComponents = rtl[i].split("/");
	    		additionalSummaryInfo = additionalSummaryInfo.concat(rtlComponents[rtlComponents.length-1] + "<br>");
	    	}
		}

		if (additionalSummaryInfo.length() > 0)
			buffer.append(additionalSummaryInfo);

        return buffer.toString();
    }
}
