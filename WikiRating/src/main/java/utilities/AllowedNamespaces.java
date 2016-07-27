package main.java.utilities;
/**
 * This enum is the filter for the namespaces
 */
public enum AllowedNamespaces {
	

			MAIN(0),
			TALK(1),
			USER(2),
			USER_TALK(3),
			PROJECT(4),
			PROJECT_TALK(5),
//			FILE(6),
//			FILE_TALK(7),
//			MEDIAWIKI(8),
//			MEDIAWIKI_TALK(9),
//			TEMPLATE(10),
//			TEMPLATE_TALK(11),
			HELP(12),
			HELP_TALK(13),
			CATEGORY(14),
			CATEGORY_TALK(15);
//			SPECIAL(-1),
//			MEDIA(-2);
	 
	    private  int value;
	 
	    AllowedNamespaces(int value) { this.value = value; }
	 
	    public int getValue() { return value; }
	
	 
	  }

