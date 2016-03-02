package com.lazywork.testframework.selenium.config;

import static com.lazywork.testframework.selenium.config.DriverType.FIREFOX;
import static com.lazywork.testframework.selenium.config.DriverType.valueOf;
import static org.openqa.selenium.Proxy.ProxyType.MANUAL;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Platform;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.lazywork.testframework.utils.Date;
import com.lazywork.testframework.utils.PomProperties;

public class WebDriverThread {
	private static final Logger LOGGER = LogManager.getLogger(WebDriverThread.class.getName());
	private WebDriver webdriver;
	private DriverType selectedDriverType;
	private String testName;

	private final DriverType defaultDriverType = FIREFOX;
	private String browser = System.getProperty("browser", defaultDriverType.toString()).toUpperCase();
	private final String operatingSystem = System.getProperty("os.name").toUpperCase();
	private final String systemArchitecture = System.getProperty("os.arch");
	private final boolean useRemoteWebDriver = Boolean.valueOf(System.getProperty("remote"));
	private final boolean useSaucelabs = Boolean.valueOf(System.getProperty("useSaucelabs"));
	private final boolean proxyEnabled = Boolean.getBoolean("proxyEnabled");
	private final String proxyHostname = System.getProperty("proxyHost");
	private final Integer proxyPort = Integer.getInteger("proxyPort");
	private final String proxyDetails = String.format("%s:%d", proxyHostname, proxyPort);

	public WebDriver getDriver() throws Exception {
		return getDriver("");
	}

	public WebDriver getDriver(String testName) throws Exception {
		this.testName = testName;
		if (null == webdriver) {
			Proxy proxy = null;
			if (proxyEnabled) {
				proxy = new Proxy();
				proxy.setProxyType(MANUAL);
				proxy.setHttpProxy(proxyDetails);
				proxy.setSslProxy(proxyDetails);
			}
			determineEffectiveDriverType();
			DesiredCapabilities desiredCapabilities = selectedDriverType.getDesiredCapabilities(proxy);
			instantiateWebDriver(desiredCapabilities);
		}
		webdriver.manage().window().maximize();
		return webdriver;
	}

	public String getBrowserName() {
		LOGGER.info("getBrowserName={}", this.browser);
		return this.browser;
	}

	public void quitDriver() {
		if (null != webdriver) {
			webdriver.quit();
		}
	}

	private void determineEffectiveDriverType() {
		DriverType driverType = defaultDriverType;
		try {
			driverType = valueOf(browser);
		} catch (IllegalArgumentException ignored) {
			LOGGER.error("Unknown driver specified, defaulting to ' {} '...", driverType);
		} catch (NullPointerException ignored) {
			LOGGER.error("No driver specified, defaulting to ' {} ' ...", driverType);
		}
		selectedDriverType = driverType;
	}

	private void instantiateWebDriver(DesiredCapabilities desiredCapabilities) throws MalformedURLException {
		LOGGER.info(
				"\n Current Operating System: {}" + "\n Current Architecture: {}"
						+ "\n Current Browser Selection: {} \n",
				operatingSystem, systemArchitecture, desiredCapabilities.getBrowserName());

		LOGGER.info("use remote driver = {}", useRemoteWebDriver);
		if (useRemoteWebDriver) {
			// URL seleniumGridURL = new
			// URL(System.getProperty("seleniumGridURL"));
			URL seleniumGridURL = null;
			if (useSaucelabs) {
				seleniumGridURL = new URL(""); // if you have the account, set
												// here
				String desiredBrowserVersion = System.getProperty("desiredBrowserVersion");
				String desiredPlatform = System.getProperty("desiredPlatform");
				if (null != desiredPlatform && !desiredPlatform.isEmpty()) {
					desiredCapabilities.setPlatform(Platform.valueOf(desiredPlatform.toUpperCase()));
				}

				if (null != desiredBrowserVersion && !desiredBrowserVersion.isEmpty()) {
					desiredCapabilities.setVersion(desiredBrowserVersion);
				}

			} else {
				// use browserstack
				seleniumGridURL = new URL("http://username:key@hub.browserstack.com/wd/hub");
				String projectName;
				PomProperties pomProperties = new PomProperties();
				this.browser = pomProperties.getBrowser();
				String browserVersion = pomProperties.getBrowserVersion();
				projectName = pomProperties.getProjectName();
				String commandLineBrowser = System.getProperty("browser");
				String commandLineBrowerVersion = System.getProperty("browserVersion");
				if (!(null == commandLineBrowser || commandLineBrowser == "")) {
					if (null == commandLineBrowerVersion || commandLineBrowerVersion == "") {
						LOGGER.error("Please provide the browser version.");
						throw new RuntimeException();
					}
					this.browser = commandLineBrowser;
					browserVersion = commandLineBrowerVersion;

				}
				LOGGER.info("browser= {}, browser Version =  {}, prjectName = {}", this.browser, browserVersion,
						projectName);
				desiredCapabilities.setCapability("browser", this.browser);
				desiredCapabilities.setCapability("browser_version", browserVersion);
				desiredCapabilities.setCapability("os", "Windows");
				desiredCapabilities.setCapability("os_version", "7");
				desiredCapabilities.setCapability("browserstack.debug", "true");
				desiredCapabilities.setCapability("project", projectName);
				desiredCapabilities.setCapability("build", this.browser.toUpperCase() + "-" + Date.nowYYMMDD());
				desiredCapabilities.setCapability("name", "Tests:" + this.testName + " " + Date.now());
				desiredCapabilities.setCapability("resolution", "1280x1024");

			}

			LOGGER.info("seleniumGridURL = {}", seleniumGridURL);
			webdriver = new RemoteWebDriver(seleniumGridURL, desiredCapabilities);
			LOGGER.info("remote driver: webdriver = {}", webdriver);

		} else {
			webdriver = selectedDriverType.getWebDriverObject(desiredCapabilities);
		}
	}
}
