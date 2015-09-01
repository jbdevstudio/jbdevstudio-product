package com.jboss.devstudio.core.installer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.izforge.izpack.adaptator.IXMLElement;
import com.izforge.izpack.adaptator.IXMLParser;
import com.izforge.izpack.adaptator.impl.XMLParser;
import com.izforge.izpack.installer.AutomatedInstallData;
import com.izforge.izpack.installer.InstallerException;

import config.TestConfig;
import junit.framework.TestCase;
import jvm.TestJvm;

public class JREAutomationHelperTest extends TestCase {
	
	private static final JREPathValidator VALIDATOR = JREPathValidatorTest.createValidator(JREPathValidatorTest.createFileExecutorDynamicOut());
	private static final String XML_CONFIG_FOLDER = CommonTestData.findClassLocation(TestConfig.class);
	private static final String JVM_FOLDER = CommonTestData.findClassLocation(TestJvm.class);
	private static final JREPathPanelMessages MESSAGES = new JREPathPanelMessages(CommonTestData.langpack);
	private static final AutomatedInstallData DATA = new AutomatedInstallData();
	
	public void testAutomationNoConfiguredJavaRunningWithJava7JDK() throws IOException {

		DATA.setVariable(JREPathPanel.VAR_JAVA_HOME, new File(JVM_FOLDER,"jdk7").getAbsolutePath());
		DATA.langpack = CommonTestData.langpack;
		IXMLElement rtn = createIXMLElement("test-automation-no-jre.xml");
		PrintStream out = Mockito.mock(PrintStream.class);
		
		JREPathPanelAutomationHelper helper = new JREPathPanelAutomationHelper(MESSAGES, VALIDATOR, out);
		
		try {
			helper.runAutomated(DATA, rtn);
		} catch (InstallerException e) {
			ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
			Mockito.verify(out,Mockito.times(5)).println(captor.capture());
			List<String> allStrings = captor.getAllValues();
			return;
		}
		fail("No InstallerException thrown for jdk7");
	}
	
	public void testAutomationNoConfiguredJavaRunningWithJava8JDK() throws IOException, InstallerException {

		DATA.setVariable(JREPathPanel.VAR_JAVA_HOME, new File(JVM_FOLDER,"jdk8").getAbsolutePath());
		DATA.langpack = CommonTestData.langpack;
		IXMLElement rtn = createIXMLElement("test-automation-no-jre.xml");
		PrintStream out = Mockito.mock(PrintStream.class);
		
		JREPathPanelAutomationHelper helper = new JREPathPanelAutomationHelper(MESSAGES, VALIDATOR, out);
		
		helper.runAutomated(DATA, rtn);
		
		Mockito.verify(out,Mockito.times(5)).println(Mockito.anyString());
	}
	
	public void testAutomationConfiguredJava8JDK() throws IOException, InstallerException {
		DATA.setVariable(JREPathPanel.VAR_JAVA_HOME, "");
		DATA.langpack = CommonTestData.langpack;
		IXMLElement rtn = createIXMLElement("test-automation.xml", new File(JVM_FOLDER,"jdk8/bin/java").getAbsolutePath());
		PrintStream out = Mockito.mock(PrintStream.class);
		
		JREPathPanelAutomationHelper helper = new JREPathPanelAutomationHelper(MESSAGES, VALIDATOR, out);
		
		helper.runAutomated(DATA, rtn);
		
		Mockito.verify(out,Mockito.times(5)).println(Mockito.anyString());
	}

	public void testAutomationConfiguredJava9JDK() throws IOException, InstallerException {
		DATA.setVariable(JREPathPanel.VAR_JAVA_HOME, "");
		DATA.langpack = CommonTestData.langpack;
		IXMLElement rtn = createIXMLElement("test-automation.xml", new File(JVM_FOLDER,"jdk9/bin/java").getAbsolutePath());
		PrintStream out = Mockito.mock(PrintStream.class);
		
		JREPathPanelAutomationHelper helper = new JREPathPanelAutomationHelper(MESSAGES, VALIDATOR, out);
		
		helper.runAutomated(DATA, rtn);
		ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(out,Mockito.times(6)).println(captor.capture());
		List<String> allStrings = captor.getAllValues();
		allStrings.get(5).matches("\\[WARNING\\].*");
	}
	
	public void testAutomationConfiguredJava9JRE() throws IOException, InstallerException {
		DATA.setVariable(JREPathPanel.VAR_JAVA_HOME, "");
		DATA.langpack = CommonTestData.langpack;
		IXMLElement rtn = createIXMLElement("test-automation.xml", new File(JVM_FOLDER,"jre9/bin/java").getAbsolutePath());
		PrintStream out = Mockito.mock(PrintStream.class);
		
		JREPathPanelAutomationHelper helper = new JREPathPanelAutomationHelper(MESSAGES, VALIDATOR, out);
		
		helper.runAutomated(DATA, rtn);
		ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(out,Mockito.times(7)).println(captor.capture());
		List<String> allStrings = captor.getAllValues();
		allStrings.get(5).matches("\\[WARNING\\].*");
		allStrings.get(6).matches("\\[WARNING\\].*");
	}
	
	public void testAutomationConfiguredJava8JRE() throws IOException, InstallerException {
		DATA.setVariable(JREPathPanel.VAR_JAVA_HOME, "");
		DATA.langpack = CommonTestData.langpack;
		IXMLElement rtn = createIXMLElement("test-automation.xml", new File(JVM_FOLDER,"jre8/bin/java").getAbsolutePath());
		PrintStream out = Mockito.mock(PrintStream.class);
		
		JREPathPanelAutomationHelper helper = new JREPathPanelAutomationHelper(MESSAGES, VALIDATOR, out);
		
		helper.runAutomated(DATA, rtn);
		ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(out,Mockito.times(6)).println(captor.capture());
		List<String> allStrings = captor.getAllValues();
		allStrings.get(5).matches("\\[WARNING\\].*");
	}

	public void testAutomationDefaultJreUpdatedToJdk() throws IOException, InstallerException {
		DATA.setVariable(JREPathPanel.VAR_JAVA_HOME, new File(JVM_FOLDER,"jdk/jre").getAbsolutePath());
		DATA.langpack = CommonTestData.langpack;
		IXMLElement rtn = createIXMLElement("test-automation-no-jre.xml");
		PrintStream out = Mockito.mock(PrintStream.class);
		
		JREPathPanelAutomationHelper helper = new JREPathPanelAutomationHelper(MESSAGES, VALIDATOR, out);
		
		helper.runAutomated(DATA, rtn);
		assertEquals(DATA.getVariable(JREPathPanel.VAR_JAVA_HOME),new File(JVM_FOLDER,"jdk").getAbsolutePath());
	}
	
	public void testAutomationDefaultJreUpdatedToJdkWindows() throws IOException, InstallerException {
		DATA.setVariable(JREPathPanel.VAR_JAVA_HOME, new File(JVM_FOLDER,"jre8").getAbsolutePath());
		DATA.langpack = CommonTestData.langpack;
		IXMLElement rtn = createIXMLElement("test-automation-no-jre.xml");
		PrintStream out = Mockito.mock(PrintStream.class);
		
		JREPathPanelAutomationHelper helper = new JREPathPanelAutomationHelper(MESSAGES, VALIDATOR, out);
		
		helper.runAutomated(DATA, rtn);
		
		assertEquals(DATA.getVariable(JREPathPanel.VAR_JAVA_HOME),new File(JVM_FOLDER,"jdk1.8.0_41").getAbsolutePath());
	}

	// Negative tests
	
	public void testAutomationConfiguredJavaLocationNodeIsEmpty() throws IOException, InstallerException {
		DATA.setVariable(JREPathPanel.VAR_JAVA_HOME, "");
		DATA.langpack = CommonTestData.langpack;
		IXMLElement rtn = createIXMLElement("test-automation-empty-jre.xml", null);
		PrintStream out = Mockito.mock(PrintStream.class);
		
		JREPathPanelAutomationHelper helper = new JREPathPanelAutomationHelper(MESSAGES, VALIDATOR, out);
		
		try {
			helper.runAutomated(DATA, rtn);
		} catch (InstallerException e) {
			assertTrue("Error message should contain [ERROR] prefix", e.getMessage().startsWith("[ERROR]"));
			return;
		}
		fail("No InstallerException thrown for empty java location");
	}

	public void testAutomationConfiguredJavaLocationNodeHasEmptyContent() throws IOException, InstallerException {
		DATA.setVariable(JREPathPanel.VAR_JAVA_HOME, "");
		DATA.langpack = CommonTestData.langpack;
		String jreLocation = new File(JVM_FOLDER,"wronlocation/bin/java").getAbsolutePath();
		IXMLElement rtn = createIXMLElement("test-automation.xml", "   \t  \n  \t  \n  ");
		PrintStream out = Mockito.mock(PrintStream.class);
		
		JREPathPanelAutomationHelper helper = new JREPathPanelAutomationHelper(MESSAGES, VALIDATOR, out);
		
		try {
			helper.runAutomated(DATA, rtn);
		} catch (InstallerException e) {
			assertTrue("Error message should contain [ERROR] prefix", e.getMessage().startsWith("[ERROR]"));
			return;
		}
		fail("No InstallerException thrown for wron java location");
	}

	public void testAutomationConfiguredWrongJavaExecName() throws IOException, InstallerException {
		DATA.setVariable(JREPathPanel.VAR_JAVA_HOME, "");
		DATA.langpack = CommonTestData.langpack;
		String jreLocation = new File(JVM_FOLDER,"bin/java1").getAbsolutePath();
		IXMLElement rtn = createIXMLElement("test-automation.xml", jreLocation);
		PrintStream out = Mockito.mock(PrintStream.class);
		
		JREPathPanelAutomationHelper helper = new JREPathPanelAutomationHelper(MESSAGES, VALIDATOR, out);
		
		try {
			helper.runAutomated(DATA, rtn);
		} catch (InstallerException e) {
			assertTrue("Error message should contain [ERROR] prefix", e.getMessage().startsWith("[ERROR]"));
			assertTrue("Error message should contain path to configured jrelocation", e.getMessage().contains(jreLocation));
			return;
		}
		fail("No InstallerException thrown for wron java location");
	}
	
	public void testAutomationConfiguredWrongJavaExecParentName() throws IOException, InstallerException {
		DATA.setVariable(JREPathPanel.VAR_JAVA_HOME, "");
		DATA.langpack = CommonTestData.langpack;
		String jreLocation = new File(JVM_FOLDER,"bin1/java").getAbsolutePath();
		IXMLElement rtn = createIXMLElement("test-automation.xml", jreLocation);
		PrintStream out = Mockito.mock(PrintStream.class);
		
		JREPathPanelAutomationHelper helper = new JREPathPanelAutomationHelper(MESSAGES, VALIDATOR, out);
		
		try {
			helper.runAutomated(DATA, rtn);
		} catch (InstallerException e) {
			assertTrue("Error message should contain [ERROR] prefix", e.getMessage().startsWith("[ERROR]"));
			assertTrue("Error message should contain path to configured jrelocation", e.getMessage().contains(jreLocation));
			return;
		}
		fail("No InstallerException thrown for wron java location");
	}

	public void testAutomationConfiguredWrongJavaLocation() throws IOException, InstallerException {
		DATA.setVariable(JREPathPanel.VAR_JAVA_HOME, "");
		DATA.langpack = CommonTestData.langpack;
		String jreLocation = new File(JVM_FOLDER,"wronlocation/bin/java").getAbsolutePath();
		IXMLElement rtn = createIXMLElement("test-automation.xml", jreLocation);
		PrintStream out = Mockito.mock(PrintStream.class);
		
		JREPathPanelAutomationHelper helper = new JREPathPanelAutomationHelper(MESSAGES, VALIDATOR, out);
		
		try {
			helper.runAutomated(DATA, rtn);
		} catch (InstallerException e) {
			assertTrue("Error message should contain [ERROR] prefix", e.getMessage().startsWith("[ERROR]"));
			assertTrue("Error message should contain path to configured jrelocation", e.getMessage().contains(jreLocation));
			return;
		}
		fail("No InstallerException thrown for wron java location");
	}

	// Utils methods 
	
	private IXMLElement createIXMLElement(String fileName) throws FileNotFoundException, IOException {
		return createIXMLElement(fileName, null);
	}

	private IXMLElement createIXMLElement(String fileName,String jreLocation) throws FileNotFoundException, IOException {
		File input = new File(XML_CONFIG_FOLDER,fileName);
		FileInputStream in = new FileInputStream(input);
		IXMLParser parser = new XMLParser();
		IXMLElement rtn = parser.parse(in, input.getAbsolutePath());
		in.close();
		if(jreLocation!=null) {
			IXMLElement jreLocationNode = rtn.getFirstChildNamed(CreateLinkPanel.class.getName()).getFirstChildNamed("jrelocation");
			if(jreLocationNode!=null) {
				jreLocationNode.setContent(jreLocation);
			} else {
				throw new IllegalArgumentException("Specific path setup requested for file without jreLocation node");
			}
		}
		return rtn.getFirstChildNamed(JREPathPanel.class.getName());
	}
}
