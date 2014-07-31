/*******************************************************************************
 * Copyright (c) 2011-2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 

package com.jboss.devstudio.core;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.jboss.devstudio.core.messages"; //$NON-NLS-1$

	public static String INTRO_PERSPECTIVE_NOT_FOUND;

	private Messages() {
	}

	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}
