package com.jboss.devstudio.core.installer.bean;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class P2IUListBean {

	List<P2IU> defaultIUs = new ArrayList<P2IU>();
	List<P2IU> additionalUs = new ArrayList<P2IU>();
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	public P2IUListBean(List<P2IU> defaultIUs, List<P2IU> additionalUs) {
		this.defaultIUs = defaultIUs;
		this.additionalUs = additionalUs;
	}

	public List<P2IU> getDefaultIUs() {
		return defaultIUs;
	}

	public void setDefaultIUs(List<P2IU> newValue) {
		this.defaultIUs = newValue;
		this.pcs.firePropertyChange("defaultIUs", null, newValue);
	}

	public List<P2IU> getAdditionalIUs() {
		return additionalUs;
	}

	public void setAdditionalIUs(List<P2IU> newValue) {
		this.additionalUs = newValue;
		this.pcs.firePropertyChange("additionalUs", null, newValue);
	}
    
    public void addPropertyChangeListener( PropertyChangeListener listener ) {
        this.pcs.addPropertyChangeListener( listener );
    }

    public void removePropertyChangeListener( PropertyChangeListener listener ) {
        this.pcs.removePropertyChangeListener( listener );
    }

	public ArrayList<P2IU> getAllIUs() {
		ArrayList<P2IU> allIUs = new ArrayList<P2IU>();
		allIUs.addAll(defaultIUs);
		allIUs.addAll(additionalUs);
		return allIUs;
	}
    
    public String getCommaSeparatedIUStringList() {
		ArrayList<P2IU> allIUs = getAllIUs();
		ArrayList<P2IU> allSelIUs = new ArrayList<P2IU>();
		for (int i = 0; i < allIUs.size(); i++) {
			P2IU p2iu = allIUs.get(i);
			if(p2iu.isSelected()) {
				allSelIUs.add(p2iu);
			}
		}
		StringBuilder allIUsList = new StringBuilder();
		for (int i = 0; i < allSelIUs.size(); i++) {
			P2IU p2iu = allSelIUs.get(i);
			allIUsList.append(p2iu.getId());
			if(i!=allSelIUs.size()-1) {
				allIUsList.append(",");
			}
		}
    	return allIUsList.toString();
    }
    
    public String getCommaSeparatedLocationStringList() {
		ArrayList<P2IU> allIUs = getAllIUs();
		ArrayList<String> uniqueLocations = new ArrayList<String>();
		for (int i = 0; i < allIUs.size(); i++) {
			if(allIUs.get(i).isSelected()) {
				if(!uniqueLocations.contains(allIUs.get(i).getPath())) {
					uniqueLocations.add(allIUs.get(i).getPath());
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
}
