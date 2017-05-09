package org.wikitolearn.wikirating.model.graph.queryresult;

import org.springframework.data.neo4j.annotation.QueryResult;
import org.wikitolearn.wikirating.model.graph.Revision;

/**
 * Created by valsdav on 09/05/17.
 */
@QueryResult
public class RevisionResult {
    public Revision revision;
}
