package main.java.computations;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import main.java.utilities.Connections;
import main.java.utilities.Loggings;

/**
 * This class will calculate the total size of all the edits done on the single page.
 *
 */

public class Contribution {
	static Class className=Contribution.class;

	public static HashMap<Integer, Integer> getPageEdits() {
		/**
		 * This method is used to calculate the total edits on a page and make a
		 * HashMap linking pid(PageID) and corresponding edits on the Page
		 *
		 * @return  This returns a HashMap containing all the pid along
		 *         with the total number of edits on that page.
		 */
		OrientGraph graph = Connections.getInstance().getDbGraph();
		HashMap<Integer, Integer> totalPageEdits = new HashMap<Integer, Integer>();
		int pageEdits = 0;
		Vertex revisionNode = null, nextRevisionNode = null;
		boolean loopCounter = false;

		// Iterating over pages to calculate their total edits
		for (Vertex pageNode : graph.getVertices("@class", "Page")) {

			// To check for any null links
			if (pageNode.getEdges(Direction.OUT, "@class", "PreviousVersionOfPage").iterator().hasNext() == false)
				continue;
			else
				loopCounter = true;
			try {
				pageEdits = 0;
				revisionNode = pageNode.getEdges(Direction.OUT, "@class", "PreviousVersionOfPage").iterator().next()
						.getVertex(Direction.IN); // Getting the latest version
													// to iterate on the
													// revisons

				// Finding out the total edits by traversing all the revisions  for a particular page.

				while (loopCounter) {
					if (revisionNode.getEdges(Direction.OUT, "@class", "PreviousRevision").iterator().hasNext()) {
						nextRevisionNode = revisionNode.getEdges(Direction.OUT, "@class", "PreviousRevision").iterator()
								.next().getVertex(Direction.IN);
						pageEdits += Math.abs(
								(int) nextRevisionNode.getProperty("size") - (int) revisionNode.getProperty("size"));
						revisionNode = revisionNode.getEdges(Direction.OUT, "@class", "PreviousRevision").iterator()
								.next().getVertex(Direction.IN);
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

		// Iterating through the Map to print the results for total edits on the pages.

		Iterator it = totalPageEdits.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			Loggings.getLogs(className).info(graph.getVertices("pid", pair.getKey()).iterator().next().getProperty("title")
					+ " has got insertions= " + pair.getValue());
			
		}

		graph.shutdown();

		return totalPageEdits;
	}

}
