/**
 * 
 */
package org.wikitolearn.wikirating.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.wikitolearn.wikirating.exception.RevisionNotFoundException;
import org.wikitolearn.wikirating.exception.TemporaryVoteValidationException;
import org.wikitolearn.wikirating.exception.UserNotFoundException;
import org.wikitolearn.wikirating.model.Revision;
import org.wikitolearn.wikirating.model.TemporaryVote;
import org.wikitolearn.wikirating.model.User;
import org.wikitolearn.wikirating.model.Vote;
import org.wikitolearn.wikirating.repository.TemporaryVoteRepository;
import org.wikitolearn.wikirating.repository.VoteRepository;

/**
 * @author aletundo
 *
 */
@Service
public class VoteService {
	private static final Logger LOG = LoggerFactory.getLogger(VoteService.class);
	@Autowired
	private TemporaryVoteRepository temporaryVoteRepository;
	@Autowired
	private VoteRepository voteRepository;
	@Autowired
	private UserService userService;
	@Autowired
	private RevisionService revisionService;
		
	/**
	 * Validate temporary votes added before the given timestamp
	 * @param timestamp the timestamp used for comparison
	 * @throws TemporaryVoteValidationException
	 */
	@Async
	public CompletableFuture<Boolean> validateTemporaryVotes(Date timestamp) throws TemporaryVoteValidationException{
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
		return CompletableFuture.completedFuture(true);
	}
	
	/**
	 * Get all votes of the requested revision
	 * @param langRevId the langRevId of the revision
	 * @return the list of votes
	 */
	public List<Vote> getAllVotesOfRevision(String langRevId){
		// FIXME: Check the different return type when there are no votes
		// but the revision exists and change the logic.
		List<Vote> votes;
		votes = voteRepository.getAllVotesOfRevision(langRevId);
		if(votes == null){
			return new ArrayList<Vote>();
		}
		return votes;
	}
}
