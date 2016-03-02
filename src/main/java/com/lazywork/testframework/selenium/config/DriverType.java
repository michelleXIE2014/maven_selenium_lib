package com.lazywork.testframework.selenium.config;

import static org.openqa.selenium.remote.CapabilityType.PROXY;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;

public enum DriverType implements DriverSetup {

	FIREFOX {
		@Override
		public DesiredCapabilities getDesiredCapabilities(Proxy proxySettings) {
			DesiredCapabilities capabilities = DesiredCapabilities.firefox();
			return addProxySettings(capabilities, proxySettings);
		}

		@Override
		public WebDriver getWebDriverObject(DesiredCapabilities capabilities) {
			File f = new File("firefox/firefox");
			if (f.exists() && !f.isDirectory()) {
				FirefoxBinary binary = new FirefoxBinary(f);
				FirefoxProfile profile = new FirefoxProfile();
				System.out.println("fire existes????????????");
				return new FirefoxDriver(binary, profile, capabilities);
			} else {
				System.out.println("fire NOT existes????????????");
				return new FirefoxDriver(capabilities);
			}

		}
	},
	CHROME {
		@Override
		public DesiredCapabilities getDesiredCapabilities(Proxy proxySettings) {
			DesiredCapabilities capabilities = DesiredCapabilities.chrome();
			capabilities.setCapability("chrome.switches", Arrays.asList("--no-default-browser-check"));
			HashMap<String, String> chromePreferences = new HashMap<String, String>();
			chromePreferences.put("profile.password_manager_enabled", "false");
			capabilities.setCapability("chrome.prefs", chromePreferences);
			return addProxySettings(capabilities, proxySettings);
		}

		@Override
		public WebDriver getWebDriverObject(DesiredCapabilities capabilities) {
			return new ChromeDriver(capabilities);
		}
	},
	IE {
		@Override
		public DesiredCapabilities getDesiredCapabilities(Proxy proxySettings) {
			DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
			capabilities.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
			capabilities.setCapability(InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING, true);
			capabilities.setCapability("requireWindowFocus", true);
			return addProxySettings(capabilities, proxySettings);
		}

		@Override
		public WebDriver getWebDriverObject(DesiredCapabilities capabilities) {
			return new InternetExplorerDriver(capabilities);
		}
	},
	SAFARI {
		@Override
		public DesiredCapabilities getDesiredCapabilities(Proxy proxySettings) {
			DesiredCapabilities capabilities = DesiredCapabilities.safari();
			capabilities.setCapability("safari.cleanSession", true);
			return addProxySettings(capabilities, proxySettings);
		}

		@Override
		public WebDriver getWebDriverObject(DesiredCapabilities capabilities) {
			return new SafariDriver(capabilities);
		}
	},
	OPERA {
		@Override
		public DesiredCapabilities getDesiredCapabilities(Proxy proxySettings) {
			DesiredCapabilities capabilities = DesiredCapabilities.operaBlink();
			return addProxySettings(capabilities, proxySettings);
		}

		@Override
		public WebDriver getWebDriverObject(DesiredCapabilities capabilities) {
			return new OperaDriver(capabilities);
		}
	},
	PHANTOMJS {
		@Override
		public DesiredCapabilities getDesiredCapabilities(Proxy proxySettings) {
			DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
			final List<String> cliArguments = new ArrayList<String>();
			cliArguments.add("--web-security=false");
			cliArguments.add("--ssl-protocol=any");
			cliArguments.add("--ignore-ssl-errors=true");
			capabilities.setCapability("phantomjs.cli.args", applyPhantomJSProxySettings(cliArguments, proxySettings));
			capabilities.setCapability("takesScreenshot", true);

			return capabilities;
		}

		@Override
		public WebDriver getWebDriverObject(DesiredCapabilities capabilities) {
			return new PhantomJSDriver(capabilities);
		}
	};

	protected DesiredCapabilities addProxySettings(DesiredCapabilities capabilities, Proxy proxySettings) {
		if (null != proxySettings) {
			capabilities.setCapability(PROXY, proxySettings);
		}

		return capabilities;
	}

	protected List<String> applyPhantomJSProxySettings(List<String> cliArguments, Proxy proxySettings) {
		if (null == proxySettings) {
			cliArguments.add("--proxy-type=none");
		} else {
			cliArguments.add("--proxy-type=http");
			cliArguments.add("--proxy=" + proxySettings.getHttpProxy());
		}
		return cliArguments;
	}
}
