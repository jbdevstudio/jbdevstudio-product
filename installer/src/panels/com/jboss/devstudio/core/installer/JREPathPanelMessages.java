package com.jboss.devstudio.core.installer;

import java.io.PrintStream;
import java.util.Properties;

import com.izforge.izpack.LocaleDatabase;
import com.jboss.devstudio.core.installer.JREPathValidator.ValidationCode;
import com.jboss.devstudio.core.installer.bean.Java;

public class JREPathPanelMessages {
	
	// messages keys for CustomLangpack_eng.xml entries
	private static final String VPE_DOES_NOT_SUPPORTED = "JREPathPanel.VPEdoesNotSupportJava64.title";
	private static final String PATH_CONTAINS_JAVA_APPLET_PLUGIN_JVM = "PathInputPanel.badVersion4";
	private static final String PATH_IS_NOT_JVM_HOME_LOCATION = "PathInputPanel.notValid";
	private static final String PATH_DOESNT_EXIST = "JREPathPanel.wrongPath.title";
	private static final String NOT_TESTED_JVM_VERSION = "PathInputPanel.notTestedVesion";
	private static final String JBDS_DOESNT_WORK_WITH_GNU_JVM = "PathInputPanel.badVersion2";
	private static final String JBDS_DOESNT_WORK_WITH_SELECTED_VERSION = "PathInputPanel.badVersion3";
	private static final String JRE_WARNING = "PathInputPanel.jreWarning";
	
	private LocaleDatabase langpack;
	
	public JREPathPanelMessages() {
	}

	public JREPathPanelMessages(LocaleDatabase langpack) {
			this.langpack = langpack;
	}

	public String getJavaDistributionValidationMessage(ValidationCode jvmDistCode) {
		String message = "";
		switch(jvmDistCode) {
			case WRN_JRE_SELECTED:
				message = getString(JRE_WARNING);
			default:
		}
		return message;
	}

	public String getValidationMessageText(ValidationCode jvmValidationCode) {
		String message = "";
		switch(jvmValidationCode) {
			case ERR_JVM_VERSION_NOT_FOUND:
			case ERR_JVM_VERSION_NOT_PARSED:
			case ERR_JVM_VERSION_LESS_THAN_MINIMAL:
				message = getString(JBDS_DOESNT_WORK_WITH_SELECTED_VERSION);
				break;
			case ERR_GNU_JVM:
				message = getString(JBDS_DOESNT_WORK_WITH_GNU_JVM);
				break;
			case WRN_JVM_VERSION_NOT_TESTED:
				message = getString(NOT_TESTED_JVM_VERSION);
				break;
			case ERR_PATH_DOES_NOT_EXIST:
				message = getString(PATH_DOESNT_EXIST);
				break;
			case ERR_PATH_IS_NOT_JVM_LOCATION:
				message = getString(PATH_IS_NOT_JVM_HOME_LOCATION);
				break;
			case ERR_PATH_IS_APPLET_PLUGIN_JVM:
				message = getString(PATH_CONTAINS_JAVA_APPLET_PLUGIN_JVM);
				break;
			case WRN_NO_VPE_SUPPORT_64BIT:
				message = getString(VPE_DOES_NOT_SUPPORTED);
				break;
			case OK:
			default:
				break;
		}
		return message;
	}

	private String getString(String key) {
		return this.langpack == null ? key : this.langpack.getString(key);
	}

	void printJvmInfo(String jreLocation, Properties props) {
		printJvmInfo(System.out, jreLocation, props);
	}
	
	void printJvmInfo(PrintStream out, String jreLocation, Properties props) {
		String vendor = props.getProperty(Java.SYSPN_JAVA_VENDOR,
						Java.UNKNOWN_VENDOR);
		String version = props.getProperty(Java.SYSPN_JAVA_VERSION,
						Java.UNKNOWN_VERSION);
		String arch = props.getProperty(Java.SYSPN_SUN_ARCH_DATA_MODEL) == null
						? Java.UNKNOWN_ARCH_DATA_MODEL
						: props.getProperty(Java.SYSPN_SUN_ARCH_DATA_MODEL) + "-bit";
	
		out.println("[INFO]  Selected JVM Information");
		out.println("[INFO]  Location : " + jreLocation );
		out.println("[INFO]  Vendor   : " + vendor );
		out.println("[INFO]  Version  : " + version );
		out.println("[INFO]  Arch     : " + arch );
	}

	public LocaleDatabase getLocaleDatabase() {
		return langpack;
	}

	public void setLocaleDatabase(LocaleDatabase langpack) {
		this.langpack = langpack;
	}
}
