package com.jboss.devstudio.core.installer;


import java.util.Iterator;

import com.izforge.izpack.Pack;
import com.izforge.izpack.installer.InstallData;
import com.izforge.izpack.installer.InstallerFrame;
import com.izforge.izpack.installer.IzPanel;

public class UpdatePacksPanel extends IzPanel{

	private static final long serialVersionUID = 1256443616359329170L;
	   // fix for
	   //       JBDS-2010 jbdevstudio-product-universal-5.0.0.v201202062217N-H926-Beta1 - during install, 
	   //                 incorrect disk space requirement displayed
	   // FIXME: Calculate real size by artifacts.xml is required to have exact number
	   static final long STUDIO_SIZE = 420*1024L*1024;

	
	public UpdatePacksPanel(InstallerFrame parent, InstallData idata) {
		super(parent, idata);
		setHidden(true);
	}
	
	public void panelActivate() {
		String group = idata.getVariable("INSTALL_GROUP");
		
		idata.selectedPacks.clear();
        idata.selectedPacks.addAll(idata.availablePacks);
        Iterator iter = idata.selectedPacks.iterator();
        while( iter.hasNext() )
        {
            Pack p = (Pack) iter.next();

            p.revDependencies = null;
            if(!p.installGroups.contains(group))
            {
                iter.remove();
            }
            if("jbds.update".equals(p.id)) {
            	p.nbytes = STUDIO_SIZE;
            }
        }
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
