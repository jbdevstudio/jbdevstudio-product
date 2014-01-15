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

import java.util.Properties;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ide.IIDEHelpContextIds;
import org.eclipse.ui.intro.IIntroSite;
import org.eclipse.ui.intro.config.IIntroAction;

import com.jboss.devstudio.core.Activator;

public class RunWizardIntroURLAction implements IIntroAction {

    private static final int WIZARD_WIDTH = 500;
    private static final int WIZARD_HEIGHT = 500;

	public void run(IIntroSite site, Properties params) {
		if (params == null)
			return;

		String wizardPluginId = params.getProperty("wizardPluginId", null); //$NON-NLS-1$
        String wizardId = params.getProperty("wizardId", null); //$NON-NLS-1$

		if (wizardPluginId == null || wizardId == null)
			return;
		
		INewWizard wizard = findNewWizardsItem(
				wizardPluginId,
				wizardId
			);
		
		if (wizard == null) 
			return;
		
		wizard.init(PlatformUI.getWorkbench(), null);
		WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
		dialog.create();
        dialog.getShell().setSize(
                Math.max(WIZARD_WIDTH, dialog.getShell().getSize().x),
                WIZARD_HEIGHT);
        PlatformUI.getWorkbench().getHelpSystem().setHelp(dialog.getShell(),
                IIDEHelpContextIds.NEW_PROJECT_WIZARD);

		dialog.open();  
	}
	
	private INewWizard findNewWizardsItem(String pluginId, String wizardId) {
		Platform.getBundle(pluginId);
		try	{
			return (INewWizard)findClassByElementId("org.eclipse.ui.newWizards", wizardId); //$NON-NLS-1$
		} catch (Exception ex) {
			Activator.logError(Activator.PLUGIN_ID, ex);
		}
		return null;
	}
	
	private Object findClassByElementId(String pointId, String id) throws Exception {
		IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(pointId);
		IConfigurationElement element = getElementById(point, id);
		if(element == null)
		  throw new RuntimeException("Configuration element with id=" + id + " is not found");
		String className = element.getAttribute("class"); //$NON-NLS-1$
		if(className == null || className.length() == 0)
		  throw new RuntimeException("Configuration element with id=" + id + " does not define 'class' attribute");
		return element.createExecutableExtension("class"); //$NON-NLS-1$
	}
	
	private IConfigurationElement getElementById(IExtensionPoint point, String id) {
		IExtension[] extensions = point.getExtensions();
		for (int i = 0; i < extensions.length; i++) {
			IConfigurationElement element = getElementById(extensions[i].getConfigurationElements(), id);
			if(element != null) return element;
		}
		return null;
	}
	
	private IConfigurationElement getElementById(IConfigurationElement[] elements, String id) {
		for (int i = 0; i < elements.length; i++) 
			if(id.equals(elements[i].getAttribute("id"))) return elements[i]; //$NON-NLS-1$
		for (int i = 0; i < elements.length; i++) {
			IConfigurationElement element = getElementById(elements[i].getChildren(), id);
			if(element != null) return element;
		}
		return null;			
	}
}
