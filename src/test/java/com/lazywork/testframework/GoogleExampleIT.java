package com.lazywork.testframework;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.lazywork.testframework.selenium.DriverFactory;
import com.lazywork.testframework.utils.TestInfo;

/*
 * Notice: This example does not use the PageFactory.
 * */

public class GoogleExampleIT extends DriverFactory {
	private static final Logger LOGGER = LogManager.getLogger(GoogleExampleIT.class.getName());
	private WebDriver driver;
	@FindBy(css = "[name='q']")
	private WebElement searchBar;

	@BeforeMethod
	public void setUp() {
		driver = getDriver(TestInfo.getClassName(Thread.currentThread()));
		PageFactory.initElements(new AjaxElementLocatorFactory(driver, 60), this);
		driver.get("http://www.google.com");
	}

	@Test
	public void googleCheeseExample() throws Exception {
		searchBar.clear();
		searchBar.sendKeys("Cheese!");
		searchBar.sendKeys(Keys.ENTER);
		LOGGER.info("Page title is: " + driver.getTitle());
	}

	@Test(enabled = true)
	public void googleMilkExample() throws Exception {
		searchBar.clear();
		searchBar.sendKeys("Milk!");
		searchBar.sendKeys(Keys.ENTER);
		LOGGER.info("Page title is: {}", driver.getTitle());
	}

	@AfterMethod
	public void tearDown() {
		closeDriverObjects();
	}
}
