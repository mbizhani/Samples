package org.devocative.samples.spring.bean;

import java.io.IOException;
import java.util.Properties;

public class DConfig {
	private Properties properties = new Properties();

	public DConfig() {
		System.out.println("DConfig Const!");

		try {
			properties.load(DConfig.class.getResourceAsStream("/config.properties"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String getString(String key) {
		return properties.getProperty(key);
	}
}
