package org.alexdev.roseau.util;

import java.io.File;
import java.security.SecureRandom;

public class Util {

	private Configuration configuration;
	private SecureRandom secureRandom;

	public Util() {
		this.configuration = new Configuration(new File("roseau.properties"));
		this.secureRandom = new SecureRandom();
	}
	
	public boolean isNullOrEmpty(String param) { 
	    return param == null || param.trim().length() == 0;
	}
	
	public long getTimestamp() {
		return System.currentTimeMillis() / 1000L; // return timestamp converted to seconds
	}
	
	public Configuration getConfiguration() {
		return configuration;
	}

	public SecureRandom getRandom() {
		return secureRandom;
	}


}
