package org.alexdev.roseau.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.oldskooler.simplelogger4j.SimpleLog;

public class Configuration {
    private static final SimpleLog logger = SimpleLog.of(Configuration.class);

	private File file;
	private Properties config;
	
	public Configuration(File file) {
		
		this.config = new Properties();
		
		try (FileInputStream stream = new FileInputStream(file.getAbsolutePath())) {
			this.config.load(stream);
		} catch (IOException e) {
			logger.error("Failed to load configuration file: " + file.getAbsolutePath(), e);
		}
	}
	
	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String get(String key) {
		return config.getProperty(key);
	}
	
	public int getInteger(String key) {
		return Integer.parseInt(config.getProperty(key));
	}
	
	public boolean getBoolean(String key) {
		return Boolean.parseBoolean(config.getProperty(key));
	}
}
