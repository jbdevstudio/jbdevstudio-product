/*******************************************************************************
 * Copyright (c) 2007-2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 

package com.jboss.devstudio.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;

public class Activator extends AbstractUIPlugin {
	public static final String PLUGIN_ID = "com.jboss.devstudio.core"; //$NON-NLS-1$
	static Activator INSTANCE;

	public Activator() {
		INSTANCE = this;
	}

	public static Activator getDefault() {
		if(INSTANCE == null) {
			Platform.getBundle(PLUGIN_ID);
		}
		return INSTANCE;
	}

	public static void logError(String pluginId, Throwable t) {
		try {
			IStatus status = new Status(IStatus.INFO, pluginId,
					0, t.getMessage() , t);
			Bundle bundle = Platform.getBundle(pluginId);
			Platform.getLog(bundle).log(status);
		} catch (Throwable x) {
			// Ignore
		}
	}
	
	public static void logError(String pluginId, String error) {
		try {
			IStatus status = new Status(IStatus.INFO, pluginId,
					0, error , null);
			Bundle bundle = Platform.getBundle(pluginId);
			Platform.getLog(bundle).log(status);
		} catch (Throwable x) {
			// Ignore
		}
	}
}
