package com.lazywork.testframework.utils;

import java.time.ZonedDateTime;

public class Date {
	private static ZonedDateTime currentTime = ZonedDateTime.now();

	public static String now() {
		return currentTime.toString();
	}

	public static String nowYYMMDD() {
		return String.format("%s-%s-%s", currentTime.getYear(), currentTime.getMonth(), currentTime.getDayOfMonth());
	}
}
