/*************************************************************************************
 * Copyright (c) 2011 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     JBoss by Red Hat - Initial implementation.
 ************************************************************************************/
package com.jboss.jbds.central;

import org.jboss.tools.project.examples.configurators.DefaultJBossCentralConfigurator;

/**
 * 
 * @author snjeza
 *
 */
public class JbdsConfigurator extends DefaultJBossCentralConfigurator {

    private static final String DOCUMENTATION_URL = "https://access.redhat.com/site/documentation/Red_Hat_JBoss_Developer_Studio/"; //$NON-NLS-1$

	@Override
	public String[] getMainToolbarCommandIds() {
		return new String[] {"com.jboss.jbds.central.openDevstudioHome", 
				"org.jboss.tools.central.preferences"};
	}

        @Override
    public String getDocumentationUrl() {
        return DOCUMENTATION_URL;
    }
	
}
