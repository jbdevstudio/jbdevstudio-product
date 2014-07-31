/******************************************************************************* 
 * Copyright (c) 2007-2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 

package com.jboss.devstudio.core.installer;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.jboss.devstudio.core.installer.bean.RuntimePathTest;

/**
 * @author eskimo
 *
 */
public class ServerListPanelAllTests {
	public static Test suite ()
	{
		TestSuite suite = new TestSuite("Server List Panel Tests");
		
		suite.addTestSuite(ServerListTableModelTest.class);
		suite.addTestSuite(ServerListBeanTest.class);
		suite.addTestSuite(ServerListTableModelTest.class);
		suite.addTestSuite(RuntimePathTest.class);
		return suite;
	}
}
