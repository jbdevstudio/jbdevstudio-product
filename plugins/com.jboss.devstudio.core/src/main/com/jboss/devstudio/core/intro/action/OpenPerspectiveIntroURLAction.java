/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package com.jboss.devstudio.core.intro.action;

import java.text.MessageFormat;
import java.util.Properties;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.internal.IPreferenceConstants;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.util.PrefUtil;
import org.eclipse.ui.intro.IIntroManager;
import org.eclipse.ui.intro.IIntroPart;
import org.eclipse.ui.intro.IIntroSite;
import org.eclipse.ui.intro.config.IIntroAction;

import com.jboss.devstudio.core.Activator;
import com.jboss.devstudio.core.Messages;

public class OpenPerspectiveIntroURLAction implements IIntroAction {
    private IWorkbenchWindow workbenchWindow;

	public void run(IIntroSite site, Properties params) {
		if (params == null || params.getProperty("perspectiveId", null) == null) //$NON-NLS-1$
			return;
        String perspectiveId = params.getProperty("perspectiveId", null); //$NON-NLS-1$
		
		workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (workbenchWindow == null) {
            return;
        }

        if(perspectiveId != null && perspectiveId.trim().length() > 0) {
            if (openNewWindow()) {
                try {
                    IWorkbench workbench = getWorkbench();
                    IAdaptable input = ((Workbench) workbench)
                            .getDefaultPageInput();
                    workbench.openWorkbenchWindow(perspectiveId, input);
                } catch (WorkbenchException e) {
                    Activator.logError(Activator.PLUGIN_ID, e);
                }
            } else {
                IWorkbenchPage activePage = getWorkbench().getActiveWorkbenchWindow()
                								.getActivePage();
                if (activePage != null) {
            	    IPerspectiveRegistry reg = PlatformUI.getWorkbench().getPerspectiveRegistry();
            	    IPerspectiveDescriptor perspectiveDescr = reg == null ? null : reg.findPerspectiveWithId(perspectiveId);
            	    IPerspectiveDescriptor activePerspectiveDescr = activePage.getPerspective();

            	    if (perspectiveDescr != null && perspectiveDescr != activePerspectiveDescr) {
            	    	activePage.setPerspective(perspectiveDescr);
            	    } else {
            	    	if (perspectiveDescr == null) {
	            	    	// Log error in case the target perspective is absent
	            	    	String errorText = MessageFormat.format(Messages.INTRO_PERSPECTIVE_NOT_FOUND, perspectiveId);
	            	    	Activator.logError(Activator.PLUGIN_ID, errorText);
            	    	}

            	    	// If perspective descriptor is null or there is already selected the perspective with id == perspectiveId
            	    	// then just close Welcome Screen (set the standby mode for the Welcome)
            			IIntroManager intro = activePage.getWorkbenchWindow().getWorkbench()
            					.getIntroManager();
            			IIntroPart part = intro.getIntro();
            			if (part == null) {
            				return;
            			}
            			if (!intro.isIntroStandby(part)) {
            				intro.setIntroStandby(part, true);
            			}
            	    }
                } else {
                    try {
                        IWorkbench workbench = getWorkbench();
                        IAdaptable input = ((Workbench) workbench)
                                .getDefaultPageInput();
                        getWorkbench().getActiveWorkbenchWindow().openPage(perspectiveId, input);
                    } catch (WorkbenchException e) {
                        Activator.logError(Activator.PLUGIN_ID, e);
                    }
                }
            }
        }
	}
	
	boolean openNewWindow() {
		try {
	        IPreferenceStore store = PrefUtil.getInternalPreferenceStore();
	        IWorkbenchPage activePage = getWorkbench().getActiveWorkbenchWindow().getActivePage();

	        return (null != activePage &&
	        		null != activePage.getPerspective() &&
	        		IPreferenceConstants.OPM_NEW_WINDOW == 
	        			store.getInt(IPreferenceConstants.OPEN_PERSP_MODE) );
		} catch (Throwable x) {
			return false;
		}
	}

	IWorkbench getWorkbench() {
		return PlatformUI.getWorkbench();
	}
}
