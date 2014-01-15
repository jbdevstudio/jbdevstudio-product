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

package com.jboss.devstudio.core.installer;

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.jboss.devstudio.core.installer.bean.RuntimePath;
import com.jboss.devstudio.core.installer.bean.ServerListBean;

/**
 * @author eskimo
 *
 */
public class ServerListBeanTest extends TestCase {
	
	
	List<RuntimePath> data = ServerListBeanTestData.getTestList();
	/**
	 * Test method for {@link com.jboss.devstudio.core.installer.bean.ServerListBean#getServers()} 
	 * and {@link com.jboss.devstudio.core.installer.bean.ServerListBean#setServers(java.util.List)}.
	 */
	public void testGetSetServers() {
		ServerListBean bean = new ServerListBean();
		assertNotNull(bean.getServers());
		assertEquals(0, bean.getServers().size());
		List<RuntimePath> newServers = new ArrayList<RuntimePath>(data);
		PropertyChangeListenerTest listenerTest = new PropertyChangeListenerTest("servers",newServers);
		bean.addPropertyChangeListener(listenerTest);
		bean.setServers(newServers);
		assertNotNull(bean.getServers());
		assertEquals(3,bean.getServers().size());
		listenerTest.assertPassed();
	}

	/**
	 * Test method for {@link com.jboss.devstudio.core.installer.bean.ServerListBean#getServers(int)}.
	 */
	public void testGetServersInt() {
		ServerListBean bean = new ServerListBean();
		Exception ex = null;
		try {
			assertNull(bean.getServers(0));
		} catch (IndexOutOfBoundsException ioobe) {
			ex = ioobe;
		}
		assertNotNull(ex);
		bean.setServers(data);
		RuntimePath server = bean.getServers(0);
		assertNotNull(server);
		assertEquals(data.get(0).getLocation(),server.getLocation());
		server = bean.getServers(1);
		assertNotNull(server);
		assertEquals(data.get(1).getLocation(),server.getLocation());
		server = bean.getServers(2);
		assertNotNull(server);
		assertEquals(data.get(2).getLocation(),server.getLocation());
	}

	/**
	 * Test method for {@link com.jboss.devstudio.core.installer.bean.ServerListBean#setServers(int, com.jboss.devstudio.core.installer.bean.RuntimePath)}.
	 */
	public void testSetServersIntServerBean() {
		ServerListBean bean = new ServerListBean();
		List<RuntimePath> newServers = new ArrayList<RuntimePath>(data);
		bean.setServers(newServers);
		RuntimePath newServerBean = new RuntimePath("/home/user/server4",true);
		IndexedPropertyChangeListenerTest listenerTest = new IndexedPropertyChangeListenerTest("servers",newServerBean);
		bean.addPropertyChangeListener(listenerTest);
		bean.setServers(1,newServerBean);
		listenerTest.assertPassed();
		assertEquals(bean.getServers().get(1).getLocation(),newServerBean.getLocation());
	}

	/**
	 * Test method for {@link com.jboss.devstudio.core.installer.bean.ServerListBean#addPropertyChangeListener(java.beans.PropertyChangeListener)}.
	 */
	public void testAddRemovePropertyChangeListener() {
		ServerListBean bean = new ServerListBean();
		List<RuntimePath> newServers = new ArrayList<RuntimePath>(data);
		PropertyChangeListenerTest listenerTest = new PropertyChangeListenerTest("servers",newServers);
		bean.addPropertyChangeListener(listenerTest);
		bean.setServers(newServers);
		RuntimePath newServerBean = new RuntimePath("/home/user/server4",true);
		IndexedPropertyChangeListenerTest indexedListenerTest = new IndexedPropertyChangeListenerTest("servers",newServerBean);
		bean.addPropertyChangeListener(indexedListenerTest);
		bean.setServers(1,newServerBean);
		listenerTest.assertPassed();
		indexedListenerTest.assertPassed();
		
		listenerTest.reset();
		indexedListenerTest.reset();
		newServers = bean.getServers();
		bean.setServers(new ArrayList<RuntimePath>());
		bean.removePropertyChangeListener(listenerTest);
		bean.removePropertyChangeListener(indexedListenerTest);
		bean.setServers(newServers);
		bean.setServers(1,newServerBean);
		listenerTest.assertNotPassed();
		indexedListenerTest.assertNotPassed();
		
	}

	/**
	 * Test method for {@link com.jboss.devstudio.core.installer.bean.ServerListBean#remove(int)}.
	 */
	public void testRemove() {
		ServerListBean bean = new ServerListBean();
		List<RuntimePath> newServers = new ArrayList<RuntimePath>(data);
		List<RuntimePath> newServers1Removed = new ArrayList<RuntimePath>(data);
		newServers1Removed.remove(1);
		RuntimePath removedBean = newServers.get(1);
		bean.setServers(newServers);
		RuntimePath newServerBean = new RuntimePath("/home/user/server4",true);
		PropertyChangeListenerTest listenerTest = new PropertyChangeListenerTest("servers",newServers1Removed);
		bean.addPropertyChangeListener(listenerTest);
		bean.remove(1);
		listenerTest.assertPassed();
		assertFalse("Removed ServerBean is still in a list", bean.getServers().contains(removedBean));
	}

	class PropertyChangeListenerTest implements PropertyChangeListener {
		
		String propertyName;
		Object newValue;
		boolean passed = false;

		public PropertyChangeListenerTest(String propertyName, Object newValue) {
			this.propertyName = propertyName;
			this.newValue = newValue;
		}

		public void propertyChange(PropertyChangeEvent evt) {
			if(!passed && evt.getPropertyName().equals(this.propertyName) 
					&& this.newValue.equals(evt.getNewValue())) {
				passed = true;
			}
		}
		
		public void assertPassed() {
			assertTrue("Event abougt chnging property '"+ this.propertyName +"' wasn't recieved", passed);
		}
		
		public void assertNotPassed() {
			assertFalse("Event abougt changing property '"+ this.propertyName +"' was recieved", passed);
		}
		
		public void reset() {
			passed = false;
		}
	}
	
	class IndexedPropertyChangeListenerTest implements PropertyChangeListener {
		
		String propertyName;
		Object newValue;
		boolean passed = false;
		
		public IndexedPropertyChangeListenerTest(String propertyName, Object newValue) {
			this.propertyName = propertyName;
			this.newValue = newValue;
		}

		public void propertyChange(PropertyChangeEvent evt) {
			if(evt instanceof IndexedPropertyChangeEvent) {
				if(!passed && evt.getPropertyName().equals(this.propertyName) 
						&& this.newValue.equals(evt.getNewValue())) {
					passed = true;
				}
			}
		}

		public void assertPassed() {
			assertTrue("Event abougt changing indexed property '"+ this.propertyName +"' wasn't recieved", passed);
		}

		public void assertNotPassed() {
			assertFalse("Event abougt changing indexed property '"+ this.propertyName +"' was recieved", passed);
		}

		public void reset() {
			passed = false;
		}
	}
}
