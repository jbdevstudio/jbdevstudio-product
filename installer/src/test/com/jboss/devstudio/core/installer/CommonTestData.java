package com.jboss.devstudio.core.installer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.MessageFormat;

import com.izforge.izpack.LocaleDatabase;

public class CommonTestData {
	private static final String SPN_PROJ_ROOT_DIR = "devstudio-installer.project.root.dir";
	public static final String LANGPACK_XML_PATH = "src/config/resources/CustomLangpack_eng.xml";
	public static String PROJECT_ROOT = System.getProperty(SPN_PROJ_ROOT_DIR);

	public static LocaleDatabase langpack = null;

	static {

		try {
			if(PROJECT_ROOT==null) {
				String testResourcesPath = findClassLocation(Class.forName("TestResources"));
				PROJECT_ROOT = new File(testResourcesPath).getParentFile().getParentFile().getAbsolutePath();
			};
			File langPackXml = new File(PROJECT_ROOT, LANGPACK_XML_PATH);
			langpack = new LocaleDatabase(new BufferedInputStream(new FileInputStream(langPackXml)));
		} catch (Exception e1) {
			throw new RuntimeException(
							MessageFormat.format("Error occurs when loading messages from {0}", e1));
		}
		if (PROJECT_ROOT == null) {
			throw new IllegalArgumentException(
							MessageFormat.format("System Property {0} is not set", SPN_PROJ_ROOT_DIR));
		}
	}

	public static String findClassLocation(Class<?> context) throws IllegalStateException {
		String jarRelatedPath = "/" + context.getName().replace(".", "/")+ ".class";
		URL location = context.getResource(jarRelatedPath);
		String jarPath = "";
		try {
			jarPath = URLDecoder.decode(location.getPath(),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return jarPath.substring(0, jarPath.lastIndexOf("/")+1);
	}
}
