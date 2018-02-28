/*************************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Red Hat, Inc. - Initial implementation.
 ************************************************************************************/
package com.jboss.devstudio.core.cheatsheets;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.cheatsheets.OpenCheatSheetAction;
import org.osgi.service.prefs.BackingStoreException;

public class CheatsheetsStartup implements org.eclipse.ui.IStartup {
	
	public static final String PLUGIN_ID = "com.jboss.devstudio.core.cheatsheets";
	private static final String RELATIVE_ROOT_PATH = (Platform.getOS().equals(Platform.OS_MACOSX) ? "../../../" //$NON-NLS-1$
			: ""); //$NON-NLS-1$
	private static final String CHEATSEET_PATH = RELATIVE_ROOT_PATH
			+ "../../cheatsheets/guided-development.xml"; //$NON-NLS-1$

	@Override
	public void earlyStartup() {
		File pathToXml;
		try {
			pathToXml = CheatsheetsStartup.getRelativeDirectory(CheatsheetsStartup.CHEATSEET_PATH);
			if (CheatsheetsStartup.isFirstStart() && pathToXml.canRead()) {
				Debug.trace("File '" + pathToXml + "' exists, running async exec to open it.");
				Display.getDefault().asyncExec(() -> {
					try {
						CheatsheetsStartup.setFirstStart(false);
						new OpenCheatSheetAction("guided.development", "Guided Development", pathToXml.toURI().toURL())
								.run();
						Debug.trace("File '" + pathToXml + "' opened.");
					} catch (MalformedURLException e) {
						logError(e.getMessage(), e);
					}
				});
			} else {
				Debug.trace("Skip opening of guided development cheatsheet");
			}
		} catch (IOException ex) {
			logError("Configuration folder location cannot be resolved", ex);
		}
	}

	private static String getConfiguration() throws IOException {
		Location configLocation = Platform.getConfigurationLocation();
		URL configURL = configLocation.getURL();
		return FileLocator.resolve(configURL).getPath();
	}

	private static File getRelativeDirectory(String dir) throws IOException {
		String configuration = getConfiguration();
		return new File(configuration, dir);
	}
	
	public static void logError(String message, Throwable exception) {
		Platform.getLog(Platform.getBundle(PLUGIN_ID)).log(new Status(Status.ERROR,
				PLUGIN_ID, message, exception));
	}

	public static boolean isFirstStart() {
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(CheatsheetsStartup.PLUGIN_ID);
		return prefs.getBoolean("FIRST_START", true);
	}

	public static void setFirstStart(boolean value) {
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(CheatsheetsStartup.PLUGIN_ID);
		prefs.putBoolean("FIRST_START", value);
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			CheatsheetsStartup.logError("Failure to flush preferences storage", e);
		}
	}
}
