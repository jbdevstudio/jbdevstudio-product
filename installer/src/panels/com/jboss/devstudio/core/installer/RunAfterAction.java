package com.jboss.devstudio.core.installer;

import com.izforge.izpack.installer.InstallData;
import com.izforge.izpack.panels.SimpleFinishPanel;

/*
 * JBDS-2038
 */
public class RunAfterAction implements com.izforge.izpack.util.CleanupClient {

	private boolean runAfter = true;
	private SimpleFinishPanel panel;
	private InstallData installData;

	public RunAfterAction(SimpleFinishPanel simpleFinishPanel, InstallData installData) {
		this.panel = simpleFinishPanel;
		this.installData = installData;
	}

	@Override
    public void cleanUp() {
    	if (this.runAfter && this.installData.installSuccess) {
    		this.panel.runAfter();
    	}
    }

    public void setRunAfter(boolean runAfter) {
    	this.runAfter = runAfter;
    }
}