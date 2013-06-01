package com.jboss.jbds.installer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.izforge.izpack.LocaleDatabase;

public class CommonTestData {
	public static final String LANGPACK_XML_PATH = "src/config/resources/CustomLangpack_eng.xml";
	public static final String PROJECT_ROOT = System.getProperty("devstudio-installer.project.root.dir");

	public static LocaleDatabase langpack = null;
	public static File serverRoots = null;
	public static File serverServerRoots = null;
	
	static {
		if(PROJECT_ROOT==null) {
			throw new IllegalArgumentException("System Property 'devstudio-installer.project.root.dir' is not set");
		}
		
		try {
			langpack = new LocaleDatabase(new BufferedInputStream(new FileInputStream(new File(PROJECT_ROOT, LANGPACK_XML_PATH))));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		serverRoots = new File(PROJECT_ROOT,"src/test-resources/servers");
		serverServerRoots = new File(PROJECT_ROOT,"src/test-resources/servers/servers");
	}
	
	public static File AS_3_2_8_SP1 = new File(serverRoots,"jboss-3.2.8.SP1");
	public static File AS_4_0_5_GA = new File(serverRoots,"jboss-4.0.5.GA");
	public static File AS_4_2_2_GA = new File(serverRoots,"jboss-as-4.2.2.GA");
	public static File AS_4_2_3_GA = new File(serverRoots,"jboss-as-4.2.3.GA");
	public static File EAP_4_3_0_GA_CP1 = new File(serverRoots,"jboss-eap-4.3.0.GA_CP1");
	public static File SOAP_4_3_0_GA = new File(serverRoots,"jboss-soa-p.4.3.0");
	public static File SOAP_4_3_0_STD_GA = new File(serverRoots,"jboss-soa-p-standalone.4.3.0");	
	public static File SOAP_5_0_0_GA = new File(serverRoots,"jboss-soa-p.5.0.0");
	public static File SOAP_5_0_0_STD_GA = new File(serverRoots,"jboss-soa-p-standalone.5.0.0");
	public static File SOAP_5_1_0_GA = new File(serverRoots,"jboss-soa-p.5.1.0");
	public static File SOAP_5_1_0_STD_GA = new File(serverRoots,"jboss-soa-p-standalone.5.1.0");
	public static File UNKNOWN_SERVER = new File(serverRoots,"unknown-server");
	
	public static File AS_3_2_8_SP1_1 = new File(serverServerRoots,"jboss-3.2.8.SP1");
	public static File AS_4_0_5_GA_2 = new File(serverServerRoots,"jboss-4.0.5.GA");
	public static File AS_4_2_2_GA_3 = new File(serverServerRoots,"jboss-as-4.2.2.GA");
	public static File AS_4_2_3_GA_4 = new File(serverServerRoots,"jboss-as-4.2.3.GA");
	public static File EAP_4_3_0_GA_CP1_5 = new File(serverServerRoots,"jboss-eap-4.3.0.GA_CP1");
	public static File SOAP_4_3_0_GA_6 = new File(serverServerRoots,"jboss-soa-p.4.3.0");
}
