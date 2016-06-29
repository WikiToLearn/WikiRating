package main.java.utilities;

import java.io.IOException;
import java.util.Properties;

/**This class handles the resources fetch . 
 * 
 */

public class Propaccess {
	
	//
	/**
	 *Return the corresponding value for the passed key. 
	 * @param key	Key name for which value needs to be fetched
	 * @return	String having the key value
	 */
	public static String getPropaccess(String key){
		String value="";
		Properties configFile = new Properties();
		try {
			configFile.load(Propaccess.class.getClassLoader().getResourceAsStream("main/resources/config.properties"));
			value = configFile.getProperty(key);
			return value;
		} catch (IOException e) {
 
			e.printStackTrace();
		}
		return value;
	}

}
