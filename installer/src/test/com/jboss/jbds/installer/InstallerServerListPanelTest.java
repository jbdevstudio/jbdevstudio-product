/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 

package com.jboss.jbds.installer;

import java.awt.BorderLayout;
import java.io.FileNotFoundException;

import javax.swing.JFrame;

/**
 * @author eskimo
 *
 */
public class InstallerServerListPanelTest {

	
	/**
	 * @param args
	 * @throws Exception 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args){
		JFrame frame = new JFrame();

		ServerListPanel panel = new ServerListPanel(CommonTestData.langpack);
		panel.getServerList().addAll(ServerListBeanTestData.getTestList());
		frame.getContentPane().add(panel,BorderLayout.CENTER);
		
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		panel.setSelection(new int [] {0,1,2});
		//System.exit(0);
	}

}
