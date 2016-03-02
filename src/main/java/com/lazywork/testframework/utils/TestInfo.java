package com.lazywork.testframework.utils;

public class TestInfo {
	public static String getClassName(Thread thread) {
		String testName = thread.getStackTrace()[2].getClassName();
		testName = testName.substring(testName.lastIndexOf("."), testName.length());
		return testName;
	}

}
