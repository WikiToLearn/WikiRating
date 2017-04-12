/**
 * 
 */
package org.wikitolearn.wikirating.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.wikitolearn.wikirating.exception.RevisionNotFoundException;
import org.wikitolearn.wikirating.exception.TemporaryVoteValidationException;
import org.wikitolearn.wikirating.exception.UserNotFoundException;
import org.wikitolearn.wikirating.model.Revision;
import org.wikitolearn.wikirating.model.TemporaryVote;
import org.wikitolearn.wikirating.model.User;
import org.wikitolearn.wikirating.model.Vote;
import org.wikitolearn.wikirating.repository.TemporaryVoteRepository;

/**
 * @author aletundo
 *
 */
public class VoteService {
	private static final Logger LOG = LoggerFactory.getLogger(VoteService.class);
	@Autowired
	private TemporaryVoteRepository temporaryVoteRepository;
	@Autowired
	private UserService userService;
	@Autowired
	private RevisionService revisionService;
		
	/**
	 * Validate temporary votes added before the given timestamp
	 * @param timestamp the timestamp used for comparison
	 * @throws TemporaryVoteValidationException
	 */
	public void validateTemporaryVotes(Date timestamp) throws TemporaryVoteValidationException{
		List<TemporaryVote> temporaryVotes = temporaryVoteRepository.findByTimestamp(timestamp);
		for(TemporaryVote temporaryVote: temporaryVotes){
			try{
				User user = userService.getUser(temporaryVote.getUserId());
				Revision revision = revisionService.getRevision(temporaryVote.getLangRevId());
				Vote vote = new Vote(temporaryVote.getValue(), temporaryVote.getReliability(), temporaryVote.getTimestamp());
				vote.setRevision(revision);
				vote.setUser(user);
				revision.addVote(vote);
				revisionService.updateRevision(revision);
			}catch(UserNotFoundException | RevisionNotFoundException e){
				LOG.error("An error occurred during temporary vote validation: {}", temporaryVote);
				throw new TemporaryVoteValidationException(e.getMessage());
			}
		}
	}
}
