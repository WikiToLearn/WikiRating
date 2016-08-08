package main.java.utilities;

import org.apache.log4j.Logger;

import main.java.computations.PageRating;


public class Logs {
	

/**
 * This method returns a logger object for the passes classname
 * @param className
 * @return
 */
public static Logger getLogs(Class className){
	//Logs.getLogs(className).info("This is a log message from Page");
	final Logger logger = Logger.getLogger(className);
	return logger;
	
}

}
