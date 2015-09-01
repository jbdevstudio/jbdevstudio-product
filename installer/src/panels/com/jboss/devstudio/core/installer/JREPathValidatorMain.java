package com.jboss.devstudio.core.installer;

import java.text.MessageFormat;

import com.jboss.devstudio.core.installer.bean.Java;

public class JREPathValidatorMain {

	public static void main(String[] args) {
		final String pattern = "{0} = {1}";
		System.out.println(MessageFormat.format(
						pattern, Java.SYSPN_JAVA_VENDOR, System.getProperty(Java.SYSPN_JAVA_VENDOR,"Unknown")));
		System.out.println(MessageFormat.format(
						pattern, Java.SYSPN_JAVA_VERSION, System.getProperties().get(Java.SYSPN_JAVA_VERSION)));			
		System.out.println(MessageFormat.format(
						pattern, Java.SYSPN_SUN_ARCH_DATA_MODEL, System.getProperties().get(Java.SYSPN_SUN_ARCH_DATA_MODEL)));
	}

}
