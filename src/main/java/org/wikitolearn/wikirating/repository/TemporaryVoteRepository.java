package org.wikitolearn.wikirating.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.wikitolearn.wikirating.model.graph.TemporaryVote;

/**
 * Created by valsdav on 24/03/17.
 */
public interface TemporaryVoteRepository extends GraphRepository<TemporaryVote> {

    TemporaryVote findByLangRevId(String langRevId);
    
    /**
     * Find temporary votes with added before the given timestamp
     * @param timestamp the vote timestamp
     * @return the temporary votes list
     */
    @Query("MATCH (n:TemporaryVote) WHERE n.timestamp < {timestamp} RETURN n")
    List<TemporaryVote> findByTimestamp(@Param("timestamp") Date timestamp);
}
