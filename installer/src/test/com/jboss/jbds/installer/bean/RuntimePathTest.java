package com.jboss.jbds.installer.bean;

import junit.framework.TestCase;

public class RuntimePathTest extends TestCase {

	private static final String LOCATION = "/location";
	private static final String NAME = "name";

	
	public void testServerBeanStringStringServerTypeString() {
		RuntimePath bean = new RuntimePath(LOCATION,true);
		assertEquals(LOCATION, bean.getLocation());
		assertEquals(true, bean.isScannedOnStartup());
	}

	public void testServerBeanServerBean() {
		RuntimePath bean1 = new RuntimePath(LOCATION,true);
		RuntimePath bean2 = new RuntimePath(bean1);
		assertEquals(bean1, bean2);
	}

	public void testSetLocation() {
		RuntimePath bean = new RuntimePath();
		bean.setLocation(LOCATION);
		assertEquals(LOCATION, bean.getLocation());
	}

	public void testSetType() {
		RuntimePath bean = new RuntimePath();
		bean.setScannedOnStartup(true);
		assertEquals(true, bean.isScannedOnStartup());
	}

	public void testEqualsObject() {
		RuntimePath bean1 = new RuntimePath(LOCATION,true);
		RuntimePath bean2 = new RuntimePath(LOCATION,true);
		assertFalse(bean1.equals(null));
		assertTrue(bean1.equals(bean1));
		assertTrue(bean1.equals(bean2));
	}

}
