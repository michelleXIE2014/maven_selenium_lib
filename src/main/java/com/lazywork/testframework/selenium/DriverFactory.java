package com.lazywork.testframework.selenium;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import com.lazywork.testframework.selenium.config.WebDriverThread;
import com.lazywork.testframework.selenium.listeners.ScreenshotListener;

@Listeners(ScreenshotListener.class)
public class DriverFactory {
	private static final Logger LOGGER = LogManager.getLogger(DriverFactory.class);
	private static List<WebDriverThread> webDriverThreadPool = Collections
			.synchronizedList(new ArrayList<WebDriverThread>());
	private static ThreadLocal<WebDriverThread> driverThread;

	@BeforeSuite
	public static void instantiateDriverObject() {
		driverThread = new ThreadLocal<WebDriverThread>() {
			@Override
			protected WebDriverThread initialValue() {
				LOGGER.info("Driver Factory creating new thread");
				WebDriverThread webDriverThread = new WebDriverThread();
				LOGGER.info("Driver thread pool add new thread");
				webDriverThreadPool.add(webDriverThread);
				LOGGER.info("new thread: {}", webDriverThread);
				return webDriverThread;
			}
		};
	}

	public static WebDriver getDriver() {
		return getDriver("");
	}

	public static WebDriver getDriver(String testName) {
		instantiateDriverObject();
		LOGGER.info("getDriver()-> driverThread: {}", driverThread);
		try {
			LOGGER.info("getDriver(): {}", driverThread.get().getDriver(testName));
			return driverThread.get().getDriver();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@AfterMethod
	public static void clearCookies() throws Exception {
		// getDriver().manage().deleteAllCookies();
	}

	@AfterSuite
	public static void closeDriverObjects() {
		for (WebDriverThread webDriverThread : webDriverThreadPool) {
			webDriverThread.quitDriver();
		}
	}
}
