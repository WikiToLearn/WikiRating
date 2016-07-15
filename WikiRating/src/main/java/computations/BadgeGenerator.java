package main.java.computations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import main.java.utilities.Connections;
import main.java.utilities.PropertiesAccess;



/**
 * This class will calculate the Badges that will be assigned
 * to the Pages based on the analysis of Page Rating distribution
 */

public class BadgeGenerator {
	
	//These varibales will store the computed cutoffs for the various badges
	static double platinumBadgeRatingCutoff;
	static double goldBadgeRatingCutoff;
	static double silverBadgeRatingCutoff;
	static double bronzeBadgeRatingCutoff;
	static double stoneBadgeRatingCutoff;
	
	/**
	 * This enum has the percentile ranges for various badges
	 */
	public enum Badges {
		PLATINUM(1),
		GOLD(2),
		SILVER(3),
		BRONZE(4),
		STONE(5),
		PLATINUM_BADGE_START_PERCENTILE(80),
		GOLD_BADGE_START_PERCENTILE(60),
		SILVER_BADGE_START_PERCENTILE(40),
		BRONZE_BADGE_START_PERCENTILE(20),
		STONE_BADGE_START_PERCENTILE(0);
 
    private  int value;
 
    Badges(int value) { this.value = value; }
    public int getValue() { return value; }
    
	}
    

 /**
  * This class will store our PageObjects to insert into Arraylist for
  * percentile calculations
  */
	public class PageRatingData{
		
		int pid;
		double pageRating;
		String pageName;
		public PageRatingData(int pid,double pageRating,String pageName) {
			this.pid=pid;
			this.pageRating=pageRating;
			this.pageName=pageName;
		}
		
	}
	
	/**
	 * This is the custom comparator to sort the pageList in the ascending 
	 * order of PageRatings
	 */
	 class PageRatingComparator implements Comparator<PageRatingData>{
		 
		 @Override
		 public int compare(PageRatingData pageRating1, PageRatingData pageRating2) {
			 if(pageRating1.pageRating>pageRating2.pageRating)
				 return 1;
			 else
				 return -1;
		 }
	    
	 }

		

	/**
	 * This method will assign badges based on the percentile
	 */
	public void generateBadges(){
		

		ArrayList<PageRatingData> pageList=new ArrayList<PageRatingData>();
		OrientGraph graph = Connections.getInstance().getDbGraph();
		
		Vertex currentPageNode=null;
		int badgeNumber=4;
		int currentPageID=0,noOfPages=0;
		double currentPageRating=0,maxPageRating=0;
		String currentPageName="";
		
		for(Vertex pageNode:graph.getVertices("@class","Page")){
			currentPageID=pageNode.getProperty("pid");
			currentPageRating=pageNode.getProperty("PageRating");
			currentPageName=pageNode.getProperty("title");
			pageList.add(new PageRatingData(currentPageID, currentPageRating,currentPageName));
		}
		Collections.sort(pageList,new PageRatingComparator());
		calculateBadgeCutoff(pageList);
		
		noOfPages=pageList.size();
		int noOfPagesCounter=0; 
		for(PageRatingData currentPage:pageList){
			badgeNumber=getBadgeNumber(currentPage.pageRating);
			System.out.println(currentPage.pageName + " ------with ratings= "+currentPage.pageRating+" earned "+badgeNumber);
			
			currentPageNode=graph.getVertices("pid",currentPage.pid).iterator().next();
			currentPageNode.setProperty("badgeNumber",badgeNumber);
			graph.commit();
			noOfPagesCounter++;
			if(noOfPagesCounter==noOfPages)
				maxPageRating=currentPage.pageRating;
		}
		//Adding the max value to the Preferences for later access
		PropertiesAccess.putParameter("maxRating", maxPageRating);
		
		graph.shutdown();
	}
	
	/**
	 * This method will calculate the cutoff for the various badges
	 * @param pageList The Arraylist containg Page Objects
	 */
	public static void calculateBadgeCutoff(ArrayList<PageRatingData> pageList){
		
		int noOfPages=pageList.size();
		int platinumPageIndex;
		int goldPageIndex;
		int silverPageIndex;
		int bronzePageIndex;
		int stonePageIndex;
		
		//Storing index where the cutoff of badges start to get the respective cutoffs
		platinumPageIndex=(int)(noOfPages*(Badges.PLATINUM_BADGE_START_PERCENTILE.value/100.00));
		goldPageIndex=(int)(noOfPages*(Badges.GOLD_BADGE_START_PERCENTILE.value/100.00));
		silverPageIndex=(int)(noOfPages*(Badges.SILVER_BADGE_START_PERCENTILE.value/100.00));
		bronzePageIndex=(int)(noOfPages*(Badges.BRONZE_BADGE_START_PERCENTILE.value/100.00));
		stonePageIndex=(int)(noOfPages*(Badges.STONE_BADGE_START_PERCENTILE.value/100.00));
		
		//Storing cutoffs
		platinumBadgeRatingCutoff=pageList.get(platinumPageIndex).pageRating;
		goldBadgeRatingCutoff=pageList.get(goldPageIndex).pageRating;
		silverBadgeRatingCutoff=pageList.get(silverPageIndex).pageRating;
		bronzeBadgeRatingCutoff=pageList.get(bronzePageIndex).pageRating;
		stoneBadgeRatingCutoff=pageList.get(stonePageIndex).pageRating;
		
		System.out.println("Index "+platinumPageIndex+"marks platinum cutoff -------"+platinumBadgeRatingCutoff);
		System.out.println("Index "+goldPageIndex+"marks gold cutoff------"+goldBadgeRatingCutoff);
		System.out.println("Index "+silverPageIndex+"marks silver cutoff------"+silverBadgeRatingCutoff);
		System.out.println("Index "+bronzePageIndex+"marks bronze cutoff------"+bronzeBadgeRatingCutoff);
		System.out.println("Index "+stonePageIndex+"marks stone cutoff------"+stoneBadgeRatingCutoff);
		
	}
	
	/**
	 * This method will pick the badge according to the passed pageRating
	 * @param pageRating	PageRating of the page under consideration
	 * @return	The name of the Badge earned
	 */
	public static int getBadgeNumber(double pageRating){
		
		if(pageRating>=platinumBadgeRatingCutoff)
			return Badges.PLATINUM.value;
		
		else if(pageRating>=goldBadgeRatingCutoff)
				return Badges.GOLD.value;
		
		else if(pageRating>=silverBadgeRatingCutoff)
				return Badges.SILVER.value;
		
		else if(pageRating>=bronzeBadgeRatingCutoff)
				return Badges.BRONZE.value;
		
			else
				return Badges.STONE.value;
	}

}
