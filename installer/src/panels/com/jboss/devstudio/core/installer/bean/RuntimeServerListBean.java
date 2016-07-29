/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package com.jboss.devstudio.core.installer.bean;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

/**
 * Methods to manipulate lists of runtime server beans.
 *
 */
public class RuntimeServerListBean {

	List<RuntimeServer> defaultRTs = new ArrayList<RuntimeServer>();
	List<RuntimeServer> additionalRTs = new ArrayList<RuntimeServer>();
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	public RuntimeServerListBean(List<RuntimeServer> defaultRTs, List<RuntimeServer> additionalRTs) {
		this.defaultRTs = defaultRTs;
		this.additionalRTs = additionalRTs;
	}

	public List<RuntimeServer> getDefaultRTs() {
		return defaultRTs;
	}

	public void setDefaultRTs(List<RuntimeServer> newValue) {
		this.defaultRTs = newValue;
		this.pcs.firePropertyChange("defaultRTs", null, newValue);
	}

	public List<RuntimeServer> getAdditionalRTs() {
		return additionalRTs;
	}

	public void setAdditionalRTs(List<RuntimeServer> newValue) {
		this.additionalRTs = newValue;
		this.pcs.firePropertyChange("additionalRTs", null, newValue);
	}
    
    public void addPropertyChangeListener( PropertyChangeListener listener ) {
        this.pcs.addPropertyChangeListener( listener );
    }

    public void removePropertyChangeListener( PropertyChangeListener listener ) {
        this.pcs.removePropertyChangeListener( listener );
    }

	public ArrayList<RuntimeServer> getAllRTs() {
		ArrayList<RuntimeServer> allRTs = new ArrayList<RuntimeServer>();
		allRTs.addAll(defaultRTs);
		allRTs.addAll(additionalRTs);
		return allRTs;
	}
    
    public String getCommaSeparatedRTStringList() {
		ArrayList<RuntimeServer> allRTs = getAllRTs();
		ArrayList<RuntimeServer> allSelRTs = new ArrayList<RuntimeServer>();
		for (int i = 0; i < allRTs.size(); i++) {
			RuntimeServer rt = allRTs.get(i);
			if (rt.isSelected()) {
				allSelRTs.add(rt);
			}
		}
		StringBuilder allRTsList = new StringBuilder();
		for (int i = 0; i < allSelRTs.size(); i++) {
			allSelRTs.get(i);
			if (i!=allSelRTs.size()-1) {
				allRTsList.append(",");
			}
		}
    	return allRTsList.toString();
    }
    
    public String getCommaSeparatedLocationStringList() {
		ArrayList<RuntimeServer> allRTs = getAllRTs();
		ArrayList<String> uniqueLocations = new ArrayList<String>();
		for (int i = 0; i < allRTs.size(); i++) {
			if (allRTs.get(i).isSelected()) {
				if (!uniqueLocations.contains(allRTs.get(i).getPath())) {
					uniqueLocations.add(allRTs.get(i).getPath());
				}
			}
		}
		StringBuilder allLocationsList = new StringBuilder();
		for (int i = 0; i < uniqueLocations.size(); i++) {
			allLocationsList.append(uniqueLocations.get(i));
			if(i!=uniqueLocations.size()-1) {
				allLocationsList.append(",");
			}
		}
		return allLocationsList.toString();
    }
    
    public String getCommaSeparatedSizeStringList() {
		ArrayList<RuntimeServer> allRTs = getAllRTs();
		ArrayList<String> uniqueSizes = new ArrayList<String>();
		for (int i = 0; i < allRTs.size(); i++) {
			if (allRTs.get(i).isSelected()) {
				uniqueSizes.add(allRTs.get(i).getSize());
			}
		}
		StringBuilder allSizesList = new StringBuilder();
		for (int i = 0; i < uniqueSizes.size(); i++) {
			allSizesList.append(uniqueSizes.get(i));
			if (i != uniqueSizes.size()-1) {
				allSizesList.append(",");
			}
		}
		return allSizesList.toString();
    }
}
