package org.wikitolearn.dao;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.sun.org.apache.xpath.internal.operations.Bool;
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
import java.util.Map;

/**
 * This class represents Metadata nodes in the DB. There is a unique Metadata node,
 * as a entrypoint, and a chain of Process nodes, saving some useful information at every
 * process in the rating engine. For example we can save the number of fetched pages or
 * saved user votes.
 * Created by valsdav on 21/03/17.
 */
@Repository
public class MetadataDAO extends GenericDAO {

    private String metadataNodeID;

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

            //We want also to create the singleton node for Metadata.
            OrientVertex metadata_main = graph.addVertex("class:Metadata");
            metadataNodeID = (String) metadata_main.getId();
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
            //getting last Process
            Vertex metadataNode = graph.getVertex(metadataNodeID);
            Edge lastProcessEdge = metadataNode.getEdges(Direction.OUT, "LastProcess").iterator().next();
            Vertex lastProcess = lastProcessEdge.getVertex(Direction.OUT);
            graph.removeEdge(lastProcessEdge);
            //adding new Process
            Map<String, Object> props = new HashMap<>();
            props.put("timestamp", process.getTimestamp());
            props.put("processType", process.getProcessType());
            props.put("processResult", process.getProcessResult());
            Vertex newProcess = graph.addVertex("class:Process", props);
            //linking the node
            metadataNode.addEdge("class:LastProcess", newProcess);
            newProcess.addEdge("class:PreviousProcess", lastProcess);
            graph.commit();
        } catch (Exception e){
            LOG.error("Something went wrong during the insertion of the process. {}.", e.getMessage());
            graph.rollback();
        } finally {
            graph.shutdown();
        }
    }
}


