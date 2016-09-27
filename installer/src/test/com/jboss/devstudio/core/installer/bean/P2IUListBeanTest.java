package com.jboss.devstudio.core.installer.bean;

import java.util.Arrays;

import com.jboss.devstudio.core.installer.IUListPanelTest;

import junit.framework.TestCase;

public class P2IUListBeanTest extends TestCase {

	public P2IUListBean createTestBean() {
		P2IUListBean pl = new P2IUListBean(
				Arrays.asList(new P2IU[]{
					new P2IU(true,"id1","label1","Description 1", "devstudio1", "0"),
					new P2IU(true,"id2","label2","Description 2", "devstudio2", "10"),
					new P2IU(true,"id3","label3","Description 3", "devstudio1", "100"),
					new P2IU(true,"id4","label4","Description 4", "devstudio2", "1000")
				}),
				Arrays.asList(new P2IU[]{
					new P2IU(true,"id5","label5","Description 5", "devstudio1", "0"),
					new P2IU(true,"id6","label6","Description 6", "devstudio2", "10"),
					new P2IU(true,"id7","label7","Description 7", "devstudio1", "100"),
					new P2IU(true,"id8","label8","Description 8", "devstudio2", "1000")
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
		assertEquals("devstudio1,devstudio2", pl.getCommaSeparatedLocationStringList());
	}

   public void testGetCommaSeparatedSizeStringList() {
		P2IUListBean pl = createTestBean();
		assertEquals("0,10,100,1000", pl.getCommaSeparatedSizeStringList());
	}

}
