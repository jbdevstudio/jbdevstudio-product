/**
 * 
 */
package com.jboss.devstudio.core.installer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Properties;

import com.izforge.izpack.util.Debug;
import com.izforge.izpack.util.FileExecutor;
import com.izforge.izpack.util.OsVersion;
import com.jboss.devstudio.core.installer.bean.Java;

/**
 * @author eskimo
 *
 */
public class JREPathValidator {

	public enum ValidationCode {
		ERR_PATH_DOES_NOT_EXIST (-7),
		ERR_PATH_IS_NOT_JVM_LOCATION(-8),
		ERR_PATH_IS_APPLET_PLUGIN_JVM (-6),
		ERR_GNU_JVM (-1),
		ERR_JVM_VERSION_NOT_FOUND (-5),
		ERR_JVM_VERSION_NOT_PARSED (-4),
		ERR_JVM_VERSION_LESS_THAN_MINIMAL (-3),
		WRN_JVM_VERSION_NOT_TESTED(2),
		WRN_NO_VPE_SUPPORT_64BIT(1),
		WRN_JRE_SELECTED(3),
		OK(0);
		
		int code;

		ValidationCode(int code) {
			this.code = code;
		}
		
		public boolean isError() {
			return code < 0;
		}
	}

	private static final String EMPTY_STRING = "";
	private static final String DEFAULT_JVM_LOCATION = EMPTY_STRING;
	private static final String[] EMPTY_OUTPUT = new String[] {EMPTY_STRING,EMPTY_STRING};
	private static final int MIN_VERSION = 8;
	private static final int MAX_VERSION = 8;
	private static final String EXE_EXT = ".exe";

	static final String JAVA_APPLET_PLUGIN = "JavaAppletPlugin.plugin";
	static final String GNU_VERSION = "gij ";
	
	private static final String VPE_NOT_SUPPORTED_ARCH = "64";

	public JREPathValidator() {
		fileExecutor = new FileExecutor();
	}

	public JREPathValidator(FileExecutor fileExecutor) {
		this.fileExecutor = fileExecutor;
	}

	public ValidationCode verifyJavaDistributionType(String jvmLocation) {
		File javacLocation = new File(jvmLocation, "bin/javac" + (OsVersion.IS_WINDOWS ? EXE_EXT : EMPTY_STRING));
		File javaLocation = new File(jvmLocation, "bin/java" + (OsVersion.IS_WINDOWS ? EXE_EXT : EMPTY_STRING));
		// check for java is required to avoid jre warnings for none java home locations
		if (javaLocation.canRead() && !javacLocation.canRead()) {
			return ValidationCode.WRN_JRE_SELECTED;
		}
		return ValidationCode.OK;
	}

	public ValidationCode runAndVerifyVersion(String jvmLocation) {
		return runAndVerifyVersion(jvmLocation, new Properties(), EMPTY_OUTPUT);
	}

	public ValidationCode runAndVerifyVersion(String jvmLocation, Properties props) {
		return runAndVerifyVersion(jvmLocation, props, EMPTY_OUTPUT);
	}
	
	public ValidationCode runAndVerifyVersion(String jvmLocation, Properties props, String[] output) {
		ValidationCode result = verifyPath(jvmLocation);
		if(result == ValidationCode.OK) {
			props.putAll(runJavaAndGetPlatformProperties(jvmLocation,output));
			result = verifyVersion(jvmLocation,props, output);
		}
		return result;
	}
	
	public ValidationCode verifyPath(String jvmLocation) {
		if(!DEFAULT_JVM_LOCATION.equals(jvmLocation) && !new File(jvmLocation).canRead()) {
			return ValidationCode.ERR_PATH_DOES_NOT_EXIST;
		} else if (!isPathValid(jvmLocation)) {
			return ValidationCode.ERR_PATH_IS_NOT_JVM_LOCATION;
		} else if (jvmLocation.contains(JREPathValidator.JAVA_APPLET_PLUGIN)) {
				// Prevent running JBDS with JavaAppletPlugin.plugin JVM
				return ValidationCode.ERR_PATH_IS_APPLET_PLUGIN_JVM;
			}
		return ValidationCode.OK;
	}

	public ValidationCode verifyVersion(String jvmLocation, Properties props, String[] output) {
		String detectedVersion = props.getProperty(Java.SYSPN_JAVA_VERSION);

		// Eclipse doesn't work with Gnu JVM implementation
		if (isGnuVersion(output)) {
			return ValidationCode.ERR_GNU_JVM;
		}
		// Java versions cold be 1.1..1.9 considering early access for java 9
		// So we accept the pattern for java version 1\.[1-9] and
		// check 3d char for >6 and return new error code -3 for none supported
		// java version
		if (detectedVersion == null) {
			return ValidationCode.ERR_JVM_VERSION_NOT_FOUND;
		}
		// Check detected version format
		if (!detectedVersion.matches("1\\.[1-9]\\.[0-9].*")) {
			// Unknown version
			return ValidationCode.ERR_JVM_VERSION_NOT_PARSED; 
		}
		int versionNumber = Integer.parseInt(detectedVersion.substring(2, 3));

		// check selected JVM version range
		if (versionNumber < MIN_VERSION) {
			// Version is less that minimum version - do not let to continue
			// installation
			return ValidationCode.ERR_JVM_VERSION_LESS_THAN_MINIMAL;
		}

		if (versionNumber > MAX_VERSION) {
			// Version is more maximum version tested - warn and let to continue
			// installation
			return ValidationCode.WRN_JVM_VERSION_NOT_TESTED;
		}

		if ((OsVersion.IS_WINDOWS || OsVersion.IS_OSX) && VPE_NOT_SUPPORTED_ARCH
						.equals(props.getProperty(Java.SYSPN_SUN_ARCH_DATA_MODEL))) {
			return ValidationCode.WRN_NO_VPE_SUPPORT_64BIT;
		}
		return ValidationCode.OK;
	}

	static public class StringInputStream extends InputStream {
		StringReader reader;
		
		public StringInputStream(String source) {
			super();
			reader = new StringReader(source);
		}
		@Override
		public int read() throws IOException {
			return reader.read();
		}	
	}

	public Properties runJavaAndGetPlatformProperties(String location) {
		return runJavaAndGetPlatformProperties(location, new String[2]);
	}
	
	public Properties runJavaAndGetPlatformProperties(String location, String[] output) {
		String jarPath = P2DirectorStarterListener.findPathJar(JREPathPanel.class);
		Debug.trace(jarPath);

		String[] params = { 
			DEFAULT_JVM_LOCATION.equals(location) ? "java" : location + File.separator + "bin" + File.separator + "java",
			"-Djava.awt.headless=true",
			"-showversion", 
			"-classpath",
			jarPath + File.pathSeparator + System.getProperty("java.class.path"),
			JREPathValidatorMain.class.getName() 
		};

		Debug.trace(params[0] + " " + params[1]);
		fileExecutor.executeCommand(params, output);
		Debug.trace(output[0]);
		Properties jvmInfo = new Properties();
		try {
			jvmInfo.load(new JREPathValidator.StringInputStream(output[0]));
		} catch (IOException e) {
			jvmInfo = new Properties();
		}
		return jvmInfo;
	}

	public boolean isGnuVersion(String[] output) {
		// "My" VM writes the version on stderr :-(
		String vs = (output[0].length() > 0) ? output[0] : output[1];
		return vs.indexOf(JREPathValidator.GNU_VERSION) >= 0;
	}

	public File getDefaultJavaLocation(String izPackDefaultJVM) {
		if (izPackDefaultJVM.contains(JAVA_APPLET_PLUGIN)) {
			String[] params = { "/usr/libexec/java_home", "-v",
					"1." + MIN_VERSION };
			String[] output = new String[2];
			Debug.trace(params[0] + " " + params[1]);
			fileExecutor.executeCommand(params, output);
			Debug.trace(output[0]);
			File location = new File(output[0].trim());
			if (location.canRead()) {
				return location;
			}
		}

		return new File(izPackDefaultJVM);
	}

	private static final String TEST_FILES[] = {
		"bin" + File.separator + "java" + (OsVersion.IS_WINDOWS ? EXE_EXT : EMPTY_STRING),
		"jre" + File.separator + "bin" + File.separator + "java" +(OsVersion.IS_WINDOWS ? EXE_EXT : EMPTY_STRING) 
	};
	private final FileExecutor fileExecutor;

	protected boolean isPathValid(String jreLocation) {
		if(DEFAULT_JVM_LOCATION.equals(jreLocation)) {
			return true;
		} 
	
		for (int i = 0; i < TEST_FILES.length; ++i) {
			File path = new File(jreLocation, TEST_FILES[i]).getAbsoluteFile();
			if (path.exists())
				return true;
		}
		return false;
	}
}