package org.wikitolearn.dao;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * This class represents Metadata nodes in the DB. There is a unique Metadata node,
 * as a entrypoint, and a chain of Process nodes, saving some useful information at every
 * process in the rating engine. For example we can save the number of fetched pages or
 * saved user votes.
 * Created by valsdav on 21/03/17.
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

            //We want also to create the singleton node for Metadata.
            OrientVertex metadata_main = graph.addVertex("class:Metadata");
            metadata_main.setProperty("creation_date", new Date());
        } catch( Exception e ) {
            LOG.error("Something went wrong during class creation. {}.", e.getMessage());
        } finally {
            graph.shutdown();
        }
    }
    
}


