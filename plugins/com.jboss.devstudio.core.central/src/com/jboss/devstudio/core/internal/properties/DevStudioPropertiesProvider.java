/*************************************************************************************
 * Copyright (c) 2014 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     JBoss by Red Hat - Initial implementation.
 ************************************************************************************/
package com.jboss.devstudio.core.internal.properties;

import org.jboss.tools.foundation.core.properties.internal.VersionPropertiesProvider;

/**
 * Properties Provider for Red Hat Developer Studio
 *
 * @author Fred Bricon
 * @since 8.0.0
 */
public class DevStudioPropertiesProvider extends VersionPropertiesProvider {

  public DevStudioPropertiesProvider() {
    super();
  }


  @Override
  protected String getVersionBundleName() {
    return "com.jboss.devstudio.core.internal.properties.currentversion";
  }
}
