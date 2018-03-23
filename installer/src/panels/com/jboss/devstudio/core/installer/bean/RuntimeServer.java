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

/**
 * A runtime server bean is a set of methods for describing and manipulating supplemental runtime
 * servers from json files (e.g.):
 	{
		"label":"Red Hat Fuse Server",
		"description":"Red Hat Fuse Server/ Runtime",
		"selected":"true",
		"path":"devstudio-is/runtime/jboss-fuse-full-6.3.0.redhat-077.zip"
		"size":"804978918"
	}
 *
 */
public class RuntimeServer {

	private String label;
	private String description;
	private String path;
	private Boolean selected;
	private String size;
    
	public RuntimeServer(Boolean selected, String label, String description, String path, String size) {
		this.label = label;
		this.description = description;
		this.path = path;
		this.selected = selected;
		this.size = size;
	}
	public RuntimeServer(String label, String description, String path, String size) {
		this(Boolean.FALSE, label, description, path, size);
	}
	
	public String getLabel() {
		return label;
	}

	public String getDescription() {
		return description;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean isSelected() {
		return selected;
	}
	
	public void setSelected(Boolean set) {
		this.selected = set;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}	
	
	public boolean equals(Object o) {
		return !super.equals(o) 
				|| (o instanceof RuntimeServer) 
				&& this.label.equals(((RuntimeServer)o).getLabel())
				&& this.description.equals(((RuntimeServer)o).getDescription())
				&& this.selected.equals(((RuntimeServer)o).isSelected())
				&& this.path.equals(((RuntimeServer)o).getPath())
				&& this.size == ((RuntimeServer)o).getSize();
	}
}
