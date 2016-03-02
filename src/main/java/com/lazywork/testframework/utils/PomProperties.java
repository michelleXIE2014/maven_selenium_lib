package com.lazywork.testframework.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PomProperties {
	private static final Logger LOGGER = LogManager.getLogger(PomProperties.class.getName());
	private String browser;
	private String browserVersion;
	private String projectName;

	public PomProperties() {
		Properties props = new Properties();
		FileInputStream in = null;

		try {
			in = new FileInputStream("target/automation.properties");
			props.load(in);
			this.browser = props.getProperty("browser");
			this.browserVersion = props.getProperty("browserVersion");
			this.projectName = props.getProperty("projectName");
			LOGGER.debug("projectName= {}, browser={}, browserVersion={} ", projectName, browser, browserVersion);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

	public String getBrowser() {
		return this.browser;
	}

	public String getBrowserVersion() {
		return this.browserVersion;
	}

	public String getProjectName() {
		return this.projectName;
	}

}
