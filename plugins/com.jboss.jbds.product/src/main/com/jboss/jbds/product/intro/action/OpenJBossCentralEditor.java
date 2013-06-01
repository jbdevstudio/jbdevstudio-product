/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package com.jboss.jbds.product.intro.action;

import java.util.Properties;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.intro.IIntroManager;
import org.eclipse.ui.intro.IIntroPart;
import org.eclipse.ui.intro.IIntroSite;
import org.jboss.tools.central.JBossCentralActivator;

/**
 * URL Action to open JBoss Central Editor
 * 
 * Params:
 * 	perspectiveId - ID of perspective to be switched to. Default value is "org.jboss.tools.common.ui.JBossPerspective"
 * 
 * @author Victor V. Rubezhny
 *
 */
public class OpenJBossCentralEditor extends OpenPerspectiveIntroURLAction {
	private static final String JBOSS_PERSPECTIVE_ID = "org.jboss.tools.common.ui.JBossPerspective";
	
	public void run(IIntroSite site, Properties params) {
		if (params == null || params.getProperty("perspectiveId", null) == null) {//$NON-NLS-1$
			params.setProperty("perspectiveId", JBOSS_PERSPECTIVE_ID);
			return;
		}
        
    	// This is to close Welcome Screen right before JBoss Central will be opened 
        IWorkbenchPage activePage = getWorkbench().getActiveWorkbenchWindow()
				.getActivePage();
        if (activePage != null) {
			IIntroManager intro = activePage.getWorkbenchWindow().getWorkbench()
					.getIntroManager();
			IIntroPart part = intro.getIntro();
			if (part != null) {
				intro.closeIntro(part);
			}
        }
        
		super.run(site, params);
		JBossCentralActivator.getJBossCentralEditor(true);
		
	}

}
