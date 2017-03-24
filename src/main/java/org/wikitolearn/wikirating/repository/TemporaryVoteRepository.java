package org.wikitolearn.wikirating.repository;

import org.springframework.data.neo4j.repository.GraphRepository;
import org.wikitolearn.wikirating.model.TemporaryVote;

/**
 * Created by valsdav on 24/03/17.
 */
public interface TemporaryVoteRepository extends GraphRepository<TemporaryVote> {

    TemporaryVote findByLangRevId(String langRevId);
}
