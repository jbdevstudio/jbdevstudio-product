package com.jboss.devstudio.core.installer;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Properties;

import com.izforge.izpack.Pack;
import com.izforge.izpack.installer.AutomatedInstallData;
import com.izforge.izpack.installer.PanelConsole;

public class UpdatePacksPanelConsoleHelper implements PanelConsole{

	public boolean runGeneratePropertiesFile(AutomatedInstallData installData, PrintWriter printWriter) {
		return true;
	}

	public boolean runConsoleFromPropertiesFile(AutomatedInstallData installData, Properties p) {
		return true;
	}

	public boolean runConsole(AutomatedInstallData idata) {
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
            	p.nbytes = UpdatePacksPanel.STUDIO_SIZE;
            }
        }
		return true;
	}
}
