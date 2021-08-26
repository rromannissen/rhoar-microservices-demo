package io.konveyor.demo.config;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
	
	private Properties config;

	public Configuration() {
		super();
		this.config = loadProperties();
		
	}

	private Properties loadProperties() {
		Properties properties = new Properties();
		
		try (InputStream inputStream = new FileInputStream("/home/rroman/persistence.properties")) {
			
			properties.load(inputStream);
			
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}

		return properties;
	}
	
	String getProperty (String name) {
		return config.getProperty(name);
	}
	
	

}
