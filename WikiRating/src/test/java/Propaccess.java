package test.java;

/**This class handles the resources fetch . 
 * 
 */
import java.io.IOException;
import java.util.Properties;


public class Propaccess {
	
	//Return the corresponding value for the passed key.
	public static String getPropaccess(String key){
		String value="";
		Properties configFile = new Properties();
		try {
			configFile.load(Propaccess.class.getClassLoader().getResourceAsStream("test/resources/config.properties"));
			value = configFile.getProperty(key);
			return value;
		} catch (IOException e) {
 
			e.printStackTrace();
		}
		return value;
	}

}
