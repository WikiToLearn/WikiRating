package main.java;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

public class EdgesTest {
	public static void testMe(){
		
		OrientGraph graph = Connections.getInstance().getDbGraph();
		
		try{
			
			Vertex ver = graph.addVertex("class:AA"); // 1st OPERATION: will implicitly begin the transaction and this command will create the class too.
			ver.setProperty( "myid", 69);
			
			Vertex ver2 = graph.addVertex("class:BB"); // 1st OPERATION: will implicitly begin the transaction and this command will create the class too.
			ver2.setProperty( "myid", 96);
			graph.commit();
			
			
			
			for(int i=0;i<200;i++){
				Edge tt = graph.addEdge("class:Adi", ver, ver2, "Bazzuka");
				tt.setProperty("name", "Iamedge");
				
				System.out.println(".");
			}
		} catch( Exception e ) {
			e.printStackTrace();
			graph.rollback();
		
		}
		graph.commit();
		graph.shutdown();
	}

}
