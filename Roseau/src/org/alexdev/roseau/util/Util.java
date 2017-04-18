package org.alexdev.roseau.util;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;

import org.alexdev.roseau.log.DateTime;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;

public class Util {

	private Wini configuration;
	private SecureRandom secureRandom;
	private Wini habboConfig;

	public Util() throws InvalidFileFormatException, IOException {
		this.configuration = new Wini(new File("roseau.properties"));
		this.habboConfig =  new Wini(new File("habbohotel.properties"));
		this.secureRandom = new SecureRandom();
	}
	
	public boolean isNullOrEmpty(String param) { 
	    return param == null || param.trim().length() == 0;
	}
	
	public long getTimestamp() {
		return System.currentTimeMillis() / 1000L; // return timestamp converted to seconds
	}
	
	public Wini getConfiguration() {
		return configuration;
	}

	public SecureRandom getRandom() {
		return secureRandom;
	}

	public Wini getHabboConfig() {
		return habboConfig;
	}
}
