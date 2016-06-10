package testing;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

import resources.Propaccess;

public class Initialise {
	
	 private static Initialise initial = new Initialise( );
	 private Initialise(){ };
	 
	 public static Initialise getInstance( ) {
	      return initial;
	   }
	
		  public  void setClass(){
		  OrientGraphFactory factory = new OrientGraphFactory(Propaccess.getPropaccess("DB_URL"),Propaccess.getPropaccess("USER"),Propaccess.getPropaccess("PASSWD"));
			 OrientGraph graph = factory.getTx();
			 graph.createVertexType("Page");
			 graph.createVertexType("User");
			 graph.createVertexType("Revision");
	}

}
