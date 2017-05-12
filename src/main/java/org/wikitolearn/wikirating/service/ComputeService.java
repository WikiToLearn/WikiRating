package org.wikitolearn.wikirating.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wikitolearn.wikirating.model.graph.Revision;
import org.wikitolearn.wikirating.model.graph.Vote;

import java.util.concurrent.CompletableFuture;

/**
 * @author valsdav
 */
@Service
public class ComputeService {

    @Autowired PageService pageService;
    @Autowired RevisionService revisionService;
    @Autowired UserService userService;
    @Autowired VoteService voteService;
    
    /**
     * Calculate the rating of all starting from last validated revision of the 
     * requested page
     * @param langPageId the langPageId of the page
     * @return a boolean CompletableFuture set to true if the computation succeed
     */
    //@Async
    public CompletableFuture<Boolean> computeRevisionsRating(String langPageId){
        // Get all the revision ordered from the oldest
        Revision lastValidated = revisionService.getRevisionsChainFromLastValidated(langPageId);
        // The order of the revision ensures that we have always the values
        // for the previous revision.
        Revision currentRevision = lastValidated;
        while( currentRevision.hasNextRevision()){
            currentRevision = currentRevision.getNextRevision();
            calculateVotesMean(currentRevision);
            calculateTotalVotesMean(currentRevision);
            calculateSigmaAndScore(currentRevision);
            calculateTotalSigmaAndScore(currentRevision);
        }
        // It saves all the chain
        revisionService.updateRevision(lastValidated);
        return CompletableFuture.completedFuture(true);
    }
    
    /**
     * 
     * @param revision
     */
    private void calculateVotesMean(Revision revision){
        double total = 0;
        double weights = 0;
        if (revision.getCurrentNumberOfVotes() > 0){
            for (Vote vote : revision.getVotes()){
                double reliabilitySquared = Math.pow(vote.getReliability(),2);
                total += reliabilitySquared * vote.getValue();
                weights += reliabilitySquared;
            }
            double currentMean = total/weights;
            revision.setCurrentMeanVote(currentMean);
        } else {
            revision.setCurrentMeanVote(0.0);
        }
    }
    
    /**
     * 
     * @param revision
     */
    private void calculateTotalVotesMean(Revision revision){
        Revision previousRevision = revision.getPreviousRevision();
        if (previousRevision == null){
            // First revision of the page
            revision.setTotalMeanVote(revision.getCurrentMeanVote());
        }else {
            double changeCoefficient = revision.getChangeCoefficient();
            // Calculate total mean
            double previousNVotes = previousRevision.getNormalizedNumberOfVotes();
            int currentNVotes = revision.getCurrentNumberOfVotes();

            double total = (previousNVotes * changeCoefficient) * previousRevision.getTotalMeanVote();
            total += currentNVotes * revision.getCurrentMeanVote();
            double weights = previousNVotes * changeCoefficient + currentNVotes;

            if (weights != 0.0){
                revision.setTotalMeanVote(total/weights);
            }else{
                revision.setTotalMeanVote(0.0);
            }

            // Calculate normalized number of votes
            revision.setNormalizedNumberOfVotes(previousNVotes * changeCoefficient + currentNVotes);
        }
    }
    
    /**
     * 
     * @param revision
     */
    private void calculateSigmaAndScore(Revision revision){
        double totalMean = revision.getTotalMeanVote();
        double sigmaTotal = 0;
        double score = 0;
        for (Vote vote : revision.getVotes()){
            double deltaVote = totalMean - vote.getValue();
            double sigma =  Math.pow(vote.getReliability(),2)* (1 - Math.pow(deltaVote,2));
            sigmaTotal += sigma;
            score += sigma * Math.pow(vote.getValue(), 2);
        }
        revision.setCurrentVotesReliability(sigmaTotal);
        revision.setCurrentScore(score);
    }
    
    /**
     * 
     * @param revision
     */
    private void calculateTotalSigmaAndScore(Revision revision){
        Revision previousRevision = revision.getPreviousRevision();
        if (previousRevision == null){
            // First revision of the page
            revision.setTotalVotesReliability(revision.getCurrentVotesReliability());
            revision.setTotalScore(revision.getCurrentScore());
        }else {
            double changeCoefficient = revision.getChangeCoefficient();
            // Calculate total reliability of votes
            revision.setTotalVotesReliability(previousRevision.getTotalVotesReliability()*changeCoefficient +
                    revision.getCurrentVotesReliability());
            // Calculate total score
            revision.setTotalScore(previousRevision.getTotalScore() * changeCoefficient +
                    revision.getCurrentScore());
        }
    }

}
