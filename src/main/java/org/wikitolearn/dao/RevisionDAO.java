package org.wikitolearn.dao;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.storage.ORecordDuplicatedException;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.wikitolearn.models.Revision;
import org.wikitolearn.utils.DbConnection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * This class will handle the Revision data processing on the OrientDB.
 * Created by valsdav on 14/03/17.
 */
@Repository
public class RevisionDAO {
    private static final Logger LOG = LoggerFactory.getLogger(RevisionDAO.class);

    @Autowired
    private DbConnection connection;

    /**
     * This method is used to create the classes on the DB.
     * Moreover it creates a unique index on the userid property to avoid duplication.
     */
    public void createDBClass() {
        LOG.info("Creating DB classes for RevisionDAO...");
        OrientGraphNoTx graph = connection.getDbGraphNT();
        try{
            // Vertex type for the revision
            OrientVertexType vertex = graph.createVertexType("Revision");
            vertex.createProperty("revid", OType.INTEGER).setMandatory(true);
            vertex.createIndex("revid", OClass.INDEX_TYPE.UNIQUE, "revid");
            // Edge type for the created edge from User to Revision
            graph.createEdgeType("Author");
            // Edge type to connect revision to parent revision
            graph.createEdgeType("ParentRevision");
            // Edge type to connect last revision to page vertex
            graph.createEdgeType("LastRevision");
            // Edge type to connect the first revision of a page
            graph.createEdgeType("FirstRevision");
        } catch( Exception e ) {
            LOG.error("Something went wrong during class creation. Operation will be rollbacked.", e.getMessage());
            graph.rollback();
        } finally {
            graph.shutdown();
        }
    }


    /**
     * This method will insert the revisions of one page, creating the link ParentRevision between them and
     * the link FirstRevision and LastRevision with the Page vertex.  Moreover it connects the Users to
     * the revisions they have created.
     * This method must be used only for the first INIT import, NOT for incremental insertion.
     * @param pageid
     * @param revs
     * @return
     */
    public Boolean insertRevisions(int pageid, List<Revision> revs){
        OrientGraph graph = connection.getGraph();
        LOG.info("Starting to insert revisions...");
        HashMap<String, Vertex> revsNodes = new HashMap<String, Vertex>();
        Vertex firstRev = null;
        Vertex lastRev = null;
        try{
            for(Revision rev : revs){
                Map<String, Object> props = new HashMap<>();
                props.put("revid", rev.getRevid());
                props.put("length", rev.getLength());
                props.put("changeCoefficient", rev.getChangeCoefficient());
                props.put( "currentMeanVote", rev.getCurrentMeanVote());
                props.put( "currentVotesReliability", rev.getCurrentVotesReliability());
                props.put( "currentNormalizedVotesReliability", rev.getCurrentNormalisesVotesReliability());
                props.put( "totalMeanVote", rev.getTotalMeanVote());
                props.put( "totalVotesReliability", rev.getTotalVotesReliability());
                props.put( "totalNormalizedVotesReliability", rev.getTotalNormalisesVotesReliability());
                props.put("validated", rev.isValidated());

                Vertex revNode = graph.addVertex("class:Revision", props);
                LOG.info("Revision inserted " + revNode.toString());
                revsNodes.put(Integer.toString(rev.getRevid()), revNode);

                if (rev.getParentid() == 0){
                    firstRev = revNode;
                }
                if (lastRev==null || rev.getRevid() > (int) lastRev.getProperty("revid")){
                    lastRev = revNode;
                }

                // Connecting the creator of the revisions
                Vertex userCreator = null;
                try{
                    userCreator = graph.getVertices("User.userid", rev.getUserid()).iterator().next();
                } catch (NoSuchElementException e){
                    //if the user is not found we link it to the Anonimous user.
                    userCreator = graph.getVertices("User.userid", "0" ).iterator().next();
                }
                graph.addEdge("class:Author", userCreator, revNode, "Author");
            }

            // Now we have to create the the links between revisions
            for (Revision r : revs){
                if (r.getParentid() != 0){
                    graph.addEdge("class:ParentRevision", revsNodes.get(Integer.toString(r.getRevid())),
                            revsNodes.get(Integer.toString(r.getParentid())), "ParentRevision");
                }
            }

            // Now let's create the LastRevision and FirstRevision edges
            Vertex page = graph.getVertices("Page.pageid", pageid).iterator().next();
            graph.addEdge("class:LastRevision", page, lastRev, "LastRevision");
            graph.addEdge("class:FirstRevision", page, firstRev, "FirstRevision");

            graph.commit();
            LOG.info(String.format("Revisions of page %s insertion committed", pageid));
            return true;
        } catch (ORecordDuplicatedException or) {
            LOG.error("Some of the pages are duplicates. Operation will be rollbacked.", or.getMessage());
            graph.rollback();
        } catch( Exception e ) {
            LOG.error("Something went wrong during user insertion. Operation will be rollbacked.", e.getMessage());
            graph.rollback();
        } finally {
            graph.shutdown();
        }
        return false;
    }

}
