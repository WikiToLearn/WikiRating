package org.wikitolearn.dao;

import com.orientechnologies.orient.core.metadata.schema.OType;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;
import org.springframework.stereotype.Repository;
import org.wikitolearn.models.Process;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class represents Metadata nodes in the DB. There is a unique Metadata node,
 * as a entrypoint, and a chain of Process nodes, saving some useful information at every
 * process in the rating engine. For example we can save the number of fetched pages or
 * saved user votes.
 * @author valsdav, aletundo
 */
@Repository
public class MetadataDAO extends GenericDAO {

    /**
     * This method creates the classes Metadata and Process in the DB.
     * The Metadata node is the entrypoint for the chain of Processes, via the
     * edges LastProcess and PreviousProcess
     */
    @Override
    public void createDatabaseClass() {
        LOG.info("Creating DB classes for MetadataDAO...");
        OrientGraphNoTx graph = connection.getGraphNT();
        try{
            graph.createVertexType("Metadata",1);
            OrientVertexType processVertex = graph.createVertexType("Process",1);
            processVertex.createProperty("timestamp", OType.DATETIME).setMandatory(true);
            graph.createEdgeType("LastProcess");
            graph.createEdgeType("PreviousProcess");
            graph.createEdgeType("SubProcess");

            // Create Metadata vertex
            OrientVertex metadata_main = graph.addVertex("class:Metadata");
            metadata_main.setProperty("creation_date", new Date());
        } catch( Exception e ) {
            LOG.error("Something went wrong during class creation. {}.", e.getMessage());
        } finally {
            graph.shutdown();
        }
    }

    /**
     * This method insert a new Process on the top of the chain in the db.
     * It creates the new link between the Metadata node and the Process.
     * @param process Process to be inserted
     * @return
     */
    public void addProcess(Process process){
        LOG.info("Inserting process...");
        OrientGraph graph = connection.getGraph();
        try {
            // Getting latest Process
            Vertex metadataNode = graph.getVerticesOfClass("Metadata").iterator().next();
            Iterator<Edge> it = metadataNode.getEdges(Direction.OUT, "LastProcess").iterator();
            Edge lastProcessEdge = null;
            Vertex lastProcess = null;
            if (it.hasNext()) {
                lastProcessEdge = it.next();
                lastProcess = lastProcessEdge.getVertex(Direction.OUT);
                graph.removeEdge(lastProcessEdge);
            }
            // Adding a new Process vertex
            Map<String, Object> props = new HashMap<>();
            props.put("timestamp", process.getTimestamp());
            props.put("processType", process.getProcessType());
            props.put("processResult", process.getProcessResult());
            Vertex newProcess = graph.addVertex("class:Process", props);
            // Linking the node
            graph.addEdge("class:LastProcess", metadataNode, newProcess, "LastProcess");
            if (lastProcess != null){
                graph.addEdge("class:PreviousProcess", newProcess, lastProcess, "PreviousProcess");
            }
            graph.commit();
        } catch (Exception e){
            LOG.error("Something went wrong during the insertion of the process. {}.", e.getMessage());
            graph.rollback();
        } finally {
            graph.shutdown();
        }
    }
}


