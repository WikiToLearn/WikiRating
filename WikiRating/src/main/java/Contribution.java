package main.java;

/*Class to calculate the contributions of User to the platfrom
 * 
 */
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

public class Contribution {

	public static HashMap<Integer, Integer> getPageEdits() {
		OrientGraph graph = Connections.getInstance().getDbGraph();
		HashMap<Integer, Integer> totalPageEdits = new HashMap<Integer, Integer>();
		int pageEdits = 0;
		Vertex revisionNode = null, nextRevisionNode = null;
		boolean loopCounter = false;

		// Iterating over pages to calculate their total edits
		for (Vertex pageNode : graph.getVertices("@class", "Page")) {

			// To check for any null links
			if (pageNode.getEdges(Direction.OUT, "@class", "Pversion").iterator().hasNext() == false)
				continue;
			else
				loopCounter = true;
			try {
				pageEdits = 0;
				revisionNode = pageNode.getEdges(Direction.OUT, "@class", "Pversion").iterator().next()
						.getVertex(Direction.IN); // Getting the latest version
													// to iterate on the
													// revisons

				// Finding out the total edits by traversing all the revisions
				// for a particular page.
				while (loopCounter) {
					if (revisionNode.getEdges(Direction.OUT, "@class", "Version").iterator().hasNext()) {
						nextRevisionNode = revisionNode.getEdges(Direction.OUT, "@class", "Version").iterator().next()
								.getVertex(Direction.IN);
						pageEdits += Math.abs(
								(int) nextRevisionNode.getProperty("size") - (int) revisionNode.getProperty("size"));
						revisionNode = revisionNode.getEdges(Direction.OUT, "@class", "Version").iterator().next()
								.getVertex(Direction.IN);
					} else {
						pageEdits += (int) revisionNode.getProperty("size");
						loopCounter = false;
					}
				}

				totalPageEdits.put((Integer) pageNode.getProperty("pid"), pageEdits);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// Iterating through the Map to print the results for total edits on the
		// pages.
		Iterator it = totalPageEdits.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			System.out.println(graph.getVertices("pid", pair.getKey()).iterator().next().getProperty("name")
					+ " has got insertions= " + pair.getValue());
		}

		graph.shutdown();

		return totalPageEdits;
	}

}
