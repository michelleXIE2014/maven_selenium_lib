package com.lazywork.testframework.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WebPage {
	public static void waitUntilTitle(WebDriver driver, final String expectedTitle, int timeout) {
		(new WebDriverWait(driver, timeout)).until(new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver d) {
				return d.getTitle().toLowerCase().startsWith(expectedTitle);
			}
		});

	}

	public static WebElement waitUntilDisplay(WebDriver driver, final WebElement expectedElement, int timeout) {
		(new WebDriverWait(driver, timeout)).until(new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver d) {
				return expectedElement.isDisplayed();
			}
		});
		return expectedElement;

	}

}
