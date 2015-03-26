package com.jboss.devstudio.core.installer.bean;

import java.util.Arrays;

import com.jboss.devstudio.core.installer.IUListPanelTest;

import junit.framework.TestCase;

public class P2IUListBeanTest extends TestCase {

	public P2IUListBean createTestBean() {
		P2IUListBean pl = new P2IUListBean(
				Arrays.asList(new P2IU[]{
					new P2IU("id1","label1","Description 1", "jbds1"),
					new P2IU("id2","label2","Description 2", "jbds2"),
					new P2IU("id3","label3","Description 3", "jbds1"),
					new P2IU("id4","label4","Description 4", "jbds2")
				}),
				Arrays.asList(new P2IU[]{
					new P2IU("id5","label5","Description 5", "jbds1"),
					new P2IU("id6","label6","Description 6", "jbds2"),
					new P2IU("id7","label7","Description 7", "jbds1"),
					new P2IU("id8","label8","Description 8", "jbds2")
				})
			);
		return pl;
	}
	
	public void testGetCommaSeparatedIUStringList() {
		P2IUListBean pl = createTestBean();
		assertEquals("id1,id2,id3,id4,id5,id6,id7,id8", pl.getCommaSeparatedIUStringList());
	}

	public void testGetCommaSeparatedLocationStringList() {
		P2IUListBean pl = createTestBean();
		assertEquals("jbds1,jbds2", pl.getCommaSeparatedLocationStringList());
	}

}
