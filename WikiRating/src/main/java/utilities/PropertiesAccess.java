package main.java.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.prefs.Preferences;

/**This class handles the resources fetch .
 *
 */

public class PropertiesAccess {
	static Class className=PropertiesAccess.class;

	/**
	 *Return the corresponding value for the passed key.
	 * @param key	Key name for which value needs to be fetched
	 * @return	String having the key value
	 */
	public static String getConfigProperties(String key){
		String value="";
		Properties configFile = new Properties();
		try {
			configFile.load(PropertiesAccess.class.getClassLoader().getResourceAsStream("main/resources/config.properties"));
			value = configFile.getProperty(key);
			return value;
		} catch (IOException e) {

			Loggings.getLogs(className).error(e);
		}
		return value;
	}

	/**
	 * Returns the corresponding Parameter's value for the passed key
	 * @param key	Key name for which value needs to be fetched
	 * @return	String having the key value
	 */
	public static String getParameterProperties(String key){
		String value="";
		Properties parameterFile = new Properties();
		try {
			parameterFile.load(PropertiesAccess.class.getClassLoader().getResourceAsStream("main/resources/parameters.properties"));
			value = parameterFile.getProperty(key);
			return value;
		} catch (IOException e) {

			Loggings.getLogs(className).error(e);
		}
		return value;
	}

	public static void putParameter(String parameterName,Double parameterValue){

		Preferences prefs = Preferences.userRoot().node(PropertiesAccess.class.getClass().getName());
		prefs.putDouble(parameterName, parameterValue);

	}

	public static double getParameter(String parameterName){

		Preferences prefs = Preferences.userRoot().node(PropertiesAccess.class.getClass().getName());
		double parameter=prefs.getDouble(parameterName, 0);
		return parameter;

	}


}
