package com.lazywork.testframework.utils;

import org.testng.asserts.Assertion;
import org.testng.asserts.SoftAssert;

public class Assertions {

	public static void hardAssertionTrue(Boolean condition) {
		Assertion hardAssert = new Assertion();
		hardAssert.assertTrue(condition);
	}

	public static SoftAssert softAssertion(Boolean condition) {
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertTrue(condition);
		return softAssert;
		// softAssert.assertAll();
	}
}
