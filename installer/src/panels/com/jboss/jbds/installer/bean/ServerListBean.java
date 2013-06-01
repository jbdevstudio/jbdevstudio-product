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

package com.jboss.jbds.installer.bean;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

/**
 * @author eskimo
 *
 */
public class ServerListBean {

	List<RuntimePath> servers = new ArrayList<RuntimePath>();
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	
	public List<RuntimePath> getServers() {
		return servers;
	}

	public void setServers(List<RuntimePath> newValue) {
		List<RuntimePath> oldValue = this.servers;
		this.servers = newValue;
		this.pcs.firePropertyChange("servers", null, newValue);
	}
	
	public RuntimePath getServers(int index) {
		return servers.get(index);
	}
	
    public void setServers( int index, RuntimePath newValue ) {
        RuntimePath oldValue = servers.remove(index);
        servers.add(index, newValue);
        pcs.fireIndexedPropertyChange("servers", index, oldValue, newValue);
    }
    
    public void addPropertyChangeListener( PropertyChangeListener listener ) {
        this.pcs.addPropertyChangeListener( listener );
    }

    public void removePropertyChangeListener( PropertyChangeListener listener ) {
        this.pcs.removePropertyChangeListener( listener );
    }

	public void remove(int index) {
		this.servers.remove(index);
		this.pcs.firePropertyChange("servers", null, this.servers);
	}
}
