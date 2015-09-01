package com.jboss.devstudio.core.installer.bean;

import java.util.Properties;

public class Java {

	public static final String UNKNOWN = "Unknown";
	public static final String UNKNOWN_VENDOR = UNKNOWN;
	public static final String UNKNOWN_VERSION = UNKNOWN;
	public static final String UNKNOWN_ARCH_DATA_MODEL = UNKNOWN;
	public static final String SYSPN_JAVA_VENDOR = "java.vendor";
	public static final String SYSPN_JAVA_VERSION = "java.version";
	public static final String SYSPN_JAVA_HOME = "java.home";
	public static final String SYSPN_SUN_ARCH_DATA_MODEL = "sun.arch.data.model";

	private Properties properties;

	public Java() {
		this(new Properties());
	}
	
	public Java(Properties properties) {
		this.properties = properties;
	}

	public String getVendor() {
		return properties.getProperty(SYSPN_JAVA_VENDOR, UNKNOWN_VENDOR);
	}

	public String getVersion()  {
		return properties.getProperty(SYSPN_JAVA_VERSION, UNKNOWN_VERSION);
	}

	public String getArchitecture() {
		String arch = properties.getProperty(SYSPN_SUN_ARCH_DATA_MODEL);
		return arch !=null ? arch + "-bit" : UNKNOWN_ARCH_DATA_MODEL;
	}
}
