/**
 * 
 */
package com.jboss.devstudio.core.installer.bean;

/**
 * @author eskimo
 *
 */
public class P2IU {

	private String id;
	private String label;
	private String description;
	private Boolean selected;
	private String path;
	private String size;  // estimated size - needed only in summary panel
	
	public P2IU(Boolean selected, String id, String label, String description, String path, String size) {
		this.id = id;
		this.label = label;
		this.description = description;
		this.selected = selected;
		this.path = path;
		this.size = size;
	}
	public P2IU(String id, String label, String description, String path) {
		this(Boolean.FALSE,id,label,description, path, "0");
	}
	
	public P2IU(String id, String label, String description, String path, String size) {
		this(Boolean.FALSE,id,label,description, path, size);
	}
	
	public String getLabel() {
		return label;
	}

	public String getDescription() {
		return description;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
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
				|| (o instanceof P2IU) 
				&& this.id.equals(((P2IU)o).getId()) 
				&& this.label.equals(((P2IU)o).getLabel())
				&& this.description.equals(((P2IU)o).getDescription())
				&& this.selected.equals(((P2IU)o).isSelected())
				&& this.path.equals(((P2IU)o).getPath())
				&& this.size.equals(((P2IU)o).getSize());
	}
}
