package main.java.utilities;

import java.io.IOException;
import java.util.Properties;

/**This class handles the resources fetch . 
 * 
 */

public class PropertiesAccess {
	

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
 
			e.printStackTrace();
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
 
			e.printStackTrace();
		}
		return value;
	}

}
