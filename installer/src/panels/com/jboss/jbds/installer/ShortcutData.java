package com.jboss.jbds.installer;

public class ShortcutData implements Cloneable {
	public String name;
	public String description;
	public String target;
	public String commandLine;
	public int type;
	public int userType;
	public boolean addToGroup;
	public String subgroup;
	public String iconFile;
	public int iconIndex;
	public int initialState;
	public String workingDirectory;
	public String deskTopEntryLinux_MimeType;
	public String deskTopEntryLinux_Terminal;
	public String deskTopEntryLinux_TerminalOptions;
	public String deskTopEntryLinux_Type;
	public String deskTopEntryLinux_URL;
	public String deskTopEntryLinux_Encoding;
	public String deskTopEntryLinux_X_KDE_SubstituteUID;
	public String deskTopEntryLinux_X_KDE_UserName;
	public String Categories;
	public String TryExec;
	public Boolean createForAll;

	public ShortcutData() {
		this.addToGroup = false;
	}

	public Object clone() throws OutOfMemoryError {
		ShortcutData result = new ShortcutData();

		result.type = this.type;
		result.userType = this.userType;
		result.iconIndex = this.iconIndex;
		result.initialState = this.initialState;
		result.addToGroup = this.addToGroup;

		result.name = cloneString(this.name);
		result.description = cloneString(this.description);
		result.target = cloneString(this.target);
		result.commandLine = cloneString(this.commandLine);
		result.subgroup = cloneString(this.subgroup);
		result.iconFile = cloneString(this.iconFile);
		result.workingDirectory = cloneString(this.workingDirectory);
		result.deskTopEntryLinux_MimeType = cloneString(this.deskTopEntryLinux_MimeType);
		result.deskTopEntryLinux_Terminal = cloneString(this.deskTopEntryLinux_Terminal);
		result.deskTopEntryLinux_TerminalOptions = cloneString(this.deskTopEntryLinux_TerminalOptions);
		result.deskTopEntryLinux_Type = cloneString(this.deskTopEntryLinux_Type);
		result.deskTopEntryLinux_URL = cloneString(this.deskTopEntryLinux_URL);
		result.deskTopEntryLinux_Encoding = cloneString(this.deskTopEntryLinux_Encoding);
		result.deskTopEntryLinux_X_KDE_SubstituteUID = cloneString(this.deskTopEntryLinux_X_KDE_SubstituteUID);
		result.deskTopEntryLinux_X_KDE_UserName = cloneString(this.deskTopEntryLinux_X_KDE_UserName);

		result.Categories = cloneString(this.Categories);
		result.TryExec = cloneString(this.TryExec);

		result.createForAll = Boolean.valueOf(this.createForAll.booleanValue());
		return result;
	}

	private String cloneString(String original) {
		if (original == null) {
			return "";
		}

		return original;
	}
}
