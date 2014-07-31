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

package com.jboss.devstudio.core.installer.bean;

import java.io.File;

public class RuntimePath {
	
	public static final String EMPTY = "";
	
	public RuntimePath() {
		
	}
	
	public RuntimePath(String location, Boolean scan) {
		super();
		this.location = location;
		this.setScannedOnStartup(scan);
	}
	
	public RuntimePath(RuntimePath bean) {
		this(bean.getLocation(),bean.isScannedOnStartup());
	}

	private String location=EMPTY;
	
	private boolean scannedOnStartup = false;
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public String toString() {
		return location + "," + isScannedOnStartup();
	}

	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		} else if(this == obj) {
			return true;
		} else {
			return this.toString().equals(obj.toString());
		}
	}

	public boolean isScannedOnStartup() {
		return scannedOnStartup;
	}

	public void setScannedOnStartup(boolean scannedOnStartup) {
		this.scannedOnStartup = scannedOnStartup;
	}
}