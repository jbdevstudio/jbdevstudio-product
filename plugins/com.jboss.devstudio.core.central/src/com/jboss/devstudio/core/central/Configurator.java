/*************************************************************************************
 * Copyright (c) 2011-2014 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat - Initial implementation.
 ************************************************************************************/
package com.jboss.devstudio.core.central;

import org.jboss.tools.central.internal.DefaultJBossCentralConfigurator;

/**
 * 
 * @author snjeza
 *
 */
public class Configurator extends DefaultJBossCentralConfigurator {

    private static final String DOCUMENTATION_URL = "https://access.redhat.com/documentation/en/red-hat-jboss-developer-studio/"; //$NON-NLS-1$

	@Override
	public String[] getMainToolbarCommandIds() {
		return new String[] {"com.jboss.devstudio.core.central.openDevstudioHome", 
				"org.jboss.tools.central.preferences"};
	}

        @Override
    public String getDocumentationUrl() {
        return DOCUMENTATION_URL;
    }
	
}
