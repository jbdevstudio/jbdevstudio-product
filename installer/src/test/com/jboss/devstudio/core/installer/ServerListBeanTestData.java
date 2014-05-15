package com.jboss.devstudio.core.installer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.jboss.devstudio.core.installer.bean.RuntimePath;

public class ServerListBeanTestData {
	private static List<RuntimePath> testList = Collections.unmodifiableList(Arrays.asList(
			new RuntimePath("/home/user/server1",true),
			new RuntimePath("/home/user/server2",false),
			new RuntimePath("/home/user/server3",false)));

	public static List<RuntimePath> getTestList() {
		return testList;
	}
}