package com.jboss.devstudio.core.installer;

import static org.mockito.Mockito.doAnswer;

import java.io.File;
import java.util.Properties;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.izforge.izpack.util.FileExecutor;
import com.izforge.izpack.util.OsVersion;
import com.izforge.izpack.util.Debug;
import com.jboss.devstudio.core.installer.JREPathValidator.ValidationCode;
import com.jboss.devstudio.core.installer.bean.Java;

import junit.framework.TestCase;
import jvm.TestJvm;

public class JREPathValidatorTest extends TestCase {

	public final static String[] EMPTY_OUT = new String[2];
	public void testRunDefaultJvm() {
		// verify default JVM execution is supported
		
		ValidationCode result = createValidator().runAndVerifyVersion("");
		assertTrue("Cannot run default jvm", result != ValidationCode.ERR_PATH_IS_NOT_JVM_LOCATION
						&& result != ValidationCode.ERR_PATH_DOES_NOT_EXIST
						&& result != ValidationCode.ERR_PATH_IS_APPLET_PLUGIN_JVM);
	}

	public void testRunSpecificJvm() {
		// verify default JVM execution is supported
		
		ValidationCode result = createValidator().runAndVerifyVersion(System.getProperty(Java.SYSPN_JAVA_HOME));
		System.out.println("[DEBUG] testRunSpecificJvm() Java.SYSPN_JAVA_HOME = " + System.getProperty(Java.SYSPN_JAVA_HOME));
		Debug.trace("[DEBUG] testRunSpecificJvm() Java.SYSPN_JAVA_HOME = " + System.getProperty(Java.SYSPN_JAVA_HOME));
		assertEquals(result,ValidationCode.OK);
	}

	public void testRunAndVerifyVersionJavaAppletPlugin() {
		String location = CommonTestData.findClassLocation(TestJvm.class);
		ValidationCode result = createValidator().verifyPath(location  + JREPathValidator.JAVA_APPLET_PLUGIN);
		assertEquals("JavaAppletPlugin.plugin was not detected " + location  + JREPathValidator.JAVA_APPLET_PLUGIN, ValidationCode.ERR_PATH_IS_APPLET_PLUGIN_JVM,result);
	}

	public void testRunAndVerifyVersion() {
		ValidationCode result = createValidator().runAndVerifyVersion(RANDOM_LOCATION, new Properties());
		assertTrue(result== ValidationCode.ERR_PATH_DOES_NOT_EXIST);
	}

	public static final String RANDOM_LOCATION = "/random/location";

	public void testVerifyPathDoesNotExists() {
		
		ValidationCode result = createValidator().verifyPath(RANDOM_LOCATION);
		assertTrue("ValidationCode.ERR_PATH_DOES_NOT_EXIST error expected", result == ValidationCode.ERR_PATH_DOES_NOT_EXIST);
	}

	public static final String SYSPN_TEMP_DIR = "java.io.tmpdir";

	public void testVerifyPathIsNotJvmHome() {
		
		ValidationCode result = createValidator().verifyPath(System.getProperty(SYSPN_TEMP_DIR));
		assertTrue("ValidationCode.ERR_PATH_DOES_NOT_EXIST error expected", result == ValidationCode.ERR_PATH_IS_NOT_JVM_LOCATION);
	}

	public void testVerifyVersionCannotDetect() {
		String[] normalOutput = new String[] {"JVM Information output", EMPTY_OUT[1]};
		ValidationCode result = createValidator().verifyVersion("", new Properties(), normalOutput );
		assertTrue(result == ValidationCode.ERR_JVM_VERSION_NOT_FOUND);
	}

	public void testVerifyVersionCannotParse() {
		ValidationCode result = createValidatorWithVersionOutput("7.0").runAndVerifyVersion("");
		assertTrue(result == ValidationCode.ERR_JVM_VERSION_NOT_PARSED);
		result = createValidatorWithVersionOutput("1.7_49").runAndVerifyVersion("");
		assertTrue(result == ValidationCode.ERR_JVM_VERSION_NOT_PARSED);
	}

	public void testVerifyVersionLessThanMinimal() {
		ValidationCode result = createValidatorWithVersionOutput("1.7.0_64").runAndVerifyVersion("");
		assertTrue(result == ValidationCode.ERR_JVM_VERSION_LESS_THAN_MINIMAL);
		result = createValidatorWithVersionOutput("1.6.0_64").runAndVerifyVersion("");
		assertTrue(result == ValidationCode.ERR_JVM_VERSION_LESS_THAN_MINIMAL);
	}

	public void testVerifyVersionJvmNotTested() {
		ValidationCode result = createValidatorWithVersionOutput("9-ea").runAndVerifyVersion("");
		assertTrue(result == ValidationCode.WRN_JVM_VERSION_NOT_TESTED);
	}

	public void testVerifyVersionJvmIsOk() {
		ValidationCode result = createValidatorWithVersionOutput("1.8.0_64").runAndVerifyVersion("");
		assertTrue(result == ValidationCode.OK);
	}

	public void testVerifyVersionWarnVpeNotSupportedOn64WinMacosx() {
		if(	OsVersion.IS_WINDOWS || OsVersion.IS_OSX) {
			ValidationCode result = createValidatorWithVersionOutput("1.8.0_64","64").runAndVerifyVersion("");
			assertTrue(result == ValidationCode.WRN_NO_VPE_SUPPORT_64BIT);
			}
	}
	
	public void testVerifyVersionNoWarnVpeNotSupportedOn32WinMacosx() {
		if(	OsVersion.IS_WINDOWS || OsVersion.IS_OSX) {
			ValidationCode result = createValidatorWithVersionOutput("1.8.0_64","32").runAndVerifyVersion("");
			assertTrue(result == ValidationCode.OK);
		}
	}

	public void testVerifyDistributionTypeIsJDK() {
		String location = CommonTestData.findClassLocation(TestJvm.class);
		ValidationCode result = createValidator().verifyJavaDistributionType(location + "jdk");
		assertEquals("JDK was not detected" + location + "jdk", ValidationCode.OK, result);
	}

	public void testVerifyDistributionTypeIsJRE() {
		String location = CommonTestData.findClassLocation(TestJvm.class);
		ValidationCode result = createValidator().verifyJavaDistributionType(location + "jre");
		assertEquals("JRE was not detected for " + location + "jre", ValidationCode.WRN_JRE_SELECTED, result);
	}

	public void testRunJavaAndGetPlatformPropertiesString() {
		String[] javaOutput = createOut("Vendor Name","1.X.X_XX","64");
		JREPathValidator validator = new JREPathValidator(createFileExecutor(javaOutput));
		Properties props = validator.runJavaAndGetPlatformProperties(RANDOM_LOCATION);
		assertEquals(3, props.size());
	}

	public void testRunJavaAndGetPlatformPropertiesStringStringArray() {
		createValidator();
	}

	public void testIsGnuVersion() {
		String[] gijOut = new String[] { "gij ", "" };
		ValidationCode result =  createValidator(createFileExecutor(gijOut)).runAndVerifyVersion("");
		assertTrue("GNU Java was not detected", result==ValidationCode.ERR_GNU_JVM);
	}

	public void testGetDefaultJava7LocationFound() {
		String location = System.getProperty(SYSPN_TEMP_DIR);
		String[] macOut = { location, ""};
		File result = createValidator(createFileExecutor(macOut)).getDefaultJavaLocation(JREPathValidator.JAVA_APPLET_PLUGIN);
		assertEquals(location, result.getPath());
	}
	
	public void testGetDefaultJava7LocationNotFound() {
		String location = CommonTestData.findClassLocation(TestJvm.class) + JREPathValidator.JAVA_APPLET_PLUGIN;
		String[] macOut = { RANDOM_LOCATION, ""};
		File result = createValidator(createFileExecutor(macOut)).getDefaultJavaLocation(location);
		assertEquals(location, result.getPath());
	}

	public void testGetDefaultJava7LocationNoNeedToFind() {
		File result = createValidator().getDefaultJavaLocation(RANDOM_LOCATION);
		assertEquals(RANDOM_LOCATION, result.getPath());
	}

	public void testValidationCodeEnum() {
		for (ValidationCode code : ValidationCode.values()) {
			assertTrue(
				code.name().startsWith("WRN") && !code.isError()
				|| code.name().startsWith("ERR") && code.isError() 
				|| code == ValidationCode.OK && !code.isError());
		}
	}

	//-------------------------------------------------------------------------------------------------------
	// factory methods for stabbing and creating test data
	//-------------------------------------------------------------------------------------------------------

	public static JREPathValidator createValidatorWithVersionOutput(String version) {
		return createValidator(createFileExecutor(createOutForVersion(version)));
	}
	public static JREPathValidator createValidatorWithVersionOutput(String version, String arch) {
		return createValidator(createFileExecutor(createOutForVersion(version,arch)));
	}
	
	public static JREPathValidator createValidator(FileExecutor fe) {
		return new JREPathValidator(fe);
	}

	public static JREPathValidator createValidator() {
		return new JREPathValidator();
	}
	
	public static FileExecutor createFileExecutor(final String[] requiredOutput ) {
		
		FileExecutor feMock = Mockito.mock(FileExecutor.class);
		doAnswer(new Answer<Integer>() {
			public Integer answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				String[] output = (String[]) args[1];
				output[0] = requiredOutput[0];
				output[1] = requiredOutput[1];
				return Integer.valueOf(0);
			}
			
		}).when(feMock).executeCommand(Mockito.any(String[].class), Mockito.any(String[].class));
		return feMock;
	}
	
	
	public static FileExecutor createFileExecutorDynamicOut() {
		
		FileExecutor feMock = Mockito.mock(FileExecutor.class);
		doAnswer(new Answer<Integer>() {
			public Integer answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				String[] params = (String[]) args[0];
				String[] output = (String[]) args[1];
				if(params[0].endsWith("7/bin/java")) {
					output[0] = createOut("Oracle","1.7.0_41","64")[0];
				} else if (params[0].endsWith("8/bin/java")) {
					output[0] = createOut("Oracle","1.8.0_41","64")[0];
				} else if (params[0].endsWith("9/bin/java")) {
					output[0] = createOut("Oracle","9-ea","64")[0];
				} else {
					output[0] = createOut("Oracle","1.8.0_41","64")[0];
				}
				output[1] = EMPTY_OUT[1];
				return Integer.valueOf(0);
			}
			
		}).when(feMock).executeCommand(Mockito.any(String[].class), Mockito.any(String[].class));
		return feMock;
	}
	
	public static FileExecutor createFileExecutor(String vendor, String version, String arch) {
		return createFileExecutor(createOut(vendor, version, arch));
	}
	
	public static String[] createOut(String vendor, String version, String arch) { 
		return new String[] {
						Java.SYSPN_JAVA_VENDOR + ":" + vendor + "\n" + 
						Java.SYSPN_JAVA_VERSION + ":" + version + "\n" +
						Java.SYSPN_SUN_ARCH_DATA_MODEL + ":" + arch,
						EMPTY_OUT[1]
					};
	}
	
	public static String[] createOutForVersion(String version) { 
		return createOut("Oracle", version, "64");
	}
	
	public static String[] createOutForVersion(String version, String arch) { 
		return createOut("Oracle", version, arch);
	}
}
