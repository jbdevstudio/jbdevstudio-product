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

import org.eclipse.core.runtime.Platform;

public class Debug {
	public static final String DEBUG_OPTION = CheatsheetsStartup.PLUGIN_ID + "/debug";
	
	public static final boolean ON = "true".equalsIgnoreCase(Platform.getDebugOption(DEBUG_OPTION));
	
	public static final void trace(String message) {
		if(ON) {
			System.out.println(message);
		}
	}
}
