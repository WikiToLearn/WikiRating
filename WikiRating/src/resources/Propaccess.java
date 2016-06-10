package resources;

import java.io.IOException;
import java.util.Properties;

public class Propaccess {
	
	public static String getPropaccess(String key){
		String value="";
		Properties configFile = new Properties();
		try {
			configFile.load(Propaccess.class.getClassLoader().getResourceAsStream("config.properties"));
			value = configFile.getProperty(key);
			return value;
		} catch (IOException e) {
 
			e.printStackTrace();
		}
		return value;
	}

}
