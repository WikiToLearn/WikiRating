package testing;

import java.util.Iterator;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

public class Dataret {
	
	
	public static String printVertex(){
		String result="";
	OrientGraph graph=Connections.getInstance().getDbGraph();
			for (Vertex v : graph.getVertices("@class","Page")) {
			result=result+" \n"+v.getProperty("name");
	}
	
	return result;
}
	
	
	
	
	}