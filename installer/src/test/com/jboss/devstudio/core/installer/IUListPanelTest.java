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

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;

import junit.framework.TestCase;

import com.jboss.devstudio.core.installer.bean.P2IU;

/**
 * @author eskimo
 *
 */
public class IUListPanelTest extends TestCase{

	public static void main(String[] args) {
		new IUListPanelTest().testP2IUListPanel();
	}
	/**
	 * @param args
	 * @throws Exception 
	 * @throws FileNotFoundException 
	 */
	public void testP2IUListPanel(){
		JFrame frame = new JFrame();

		List<P2IU> defaultIUs = Arrays.asList(new P2IU[]{
				new P2IU(Boolean.TRUE, "id1","Label1","Description1","jbds"),
				new P2IU(Boolean.TRUE, "id2","Label2","Description2","jbds"),});
		
		List<P2IU> additionalIus = Arrays.asList(new P2IU[]{
				new P2IU("id3","Label3","Description3","jbds-is"),
				new P2IU("id4","Label4","Description4","jbds-is"),
				new P2IU("id5","Label5","Description5","jbds-is")});
		
		P2IUListPanel panel = new P2IUListPanel(CommonTestData.langpack,defaultIUs,additionalIus);
		
		frame.getContentPane().add(panel,BorderLayout.CENTER);
		
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		assertEquals("id1,id2",panel.getIUListBean().getCommaSeparatedIUStringList());
		assertEquals("jbds",panel.getIUListBean().getCommaSeparatedLocationStringList());
		
		panel.selectIU(0);
		assertEquals("id1,id2,id3",panel.getIUListBean().getCommaSeparatedIUStringList());
		assertEquals("jbds,jbds-is",panel.getIUListBean().getCommaSeparatedLocationStringList());
		
		panel.selectAll();
		assertEquals("id1,id2,id3,id4,id5",panel.getIUListBean().getCommaSeparatedIUStringList());
		assertEquals("jbds,jbds-is",panel.getIUListBean().getCommaSeparatedLocationStringList());
		
		panel.deselectAll();
		assertEquals("id1,id2",panel.getIUListBean().getCommaSeparatedIUStringList());
		assertEquals("jbds",panel.getIUListBean().getCommaSeparatedLocationStringList());		
	}

}
