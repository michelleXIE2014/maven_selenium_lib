package com.lazywork.testframework.selenium.listeners;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import com.lazywork.testframework.selenium.config.WebDriverThread;
import static com.lazywork.testframework.selenium.DriverFactory.getDriver;


public class ScreenshotListener extends TestListenerAdapter {
	private static final Logger LOGGER = LogManager.getLogger(WebDriverThread.class.getName());

	private boolean createFile(File screenshot) {
		boolean fileCreated = false;

		if (screenshot.exists()) {
			fileCreated = true;
		} else {
			File parentDirectory = new File(screenshot.getParent());
			if (parentDirectory.exists() || parentDirectory.mkdirs()) {
				try {
					fileCreated = screenshot.createNewFile();
				} catch (IOException errorCreatingScreenshot) {
					errorCreatingScreenshot.printStackTrace();
				}
			}
		}

		return fileCreated;
	}

	private void writeScreenshotToFile(WebDriver driver, File screenshot) {
		try {
			FileOutputStream screenshotStream = new FileOutputStream(screenshot);
			screenshotStream.write(((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES));
			screenshotStream.close();
		} catch (IOException unableToWriteScreenshot) {
			LOGGER.error("Unable to write {}", screenshot.getAbsolutePath());
			unableToWriteScreenshot.printStackTrace();
		}
	}

	@Override
	public void onTestFailure(ITestResult failingTest) {
		try {
			WebDriver driver = getDriver();
			String screenshotDirectory = System.getProperty("screenshotDirectory", "target/screenshots");
			String screenshotAbsolutePath = screenshotDirectory + File.separator + System.currentTimeMillis() + "_"
					+ failingTest.getName() + ".png";
			File screenshot = new File(screenshotAbsolutePath);
			if (createFile(screenshot)) {
				try {
					writeScreenshotToFile(driver, screenshot);
				} catch (ClassCastException weNeedToAugmentOurDriverObject) {
					writeScreenshotToFile(new Augmenter().augment(driver), screenshot);
				}
				LOGGER.info("Written screenshot to {}", screenshotAbsolutePath);
			} else {
				LOGGER.info("Unable to create {}", screenshotAbsolutePath);
			}
		} catch (Exception ex) {
			LOGGER.info("Unable to capture screenshot...");
			ex.printStackTrace();
		}
	}
}
