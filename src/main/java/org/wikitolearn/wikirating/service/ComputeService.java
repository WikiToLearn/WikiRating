package org.wikitolearn.wikirating.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wikitolearn.wikirating.model.graph.Revision;
import org.wikitolearn.wikirating.model.graph.Vote;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Created by valsdav on 07/05/17.
 */

@Service
public class ComputeService {

    @Autowired PageService pageService;
    @Autowired RevisionService revisionService;
    @Autowired UserService userService;
    @Autowired VoteService voteService;

    //@Async
    public CompletableFuture<Boolean> computeRevisionsRating(String langPageId){
        // Get all the revision ordered from the oldest
        List<Revision> revisions = revisionService.getRevisionsFromLastValidated(langPageId);
        // The order of the revision ensures that we have always the values
        // for the previous revision.
        for (Revision revision : revisions){
            calculateVotesMean(revision);
            calculateTotalVotesMean(revision);
            calculateSigmaAndScore(revision);
            calculateTotalSigmaAndScore(revision);
        }
        revisionService.updateRevisions(revisions);
        return CompletableFuture.completedFuture(true);
    }

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

            revision.setTotalMeanVote(total/weights);
            // Calculate normalized number of votes
            revision.setNormalizedNumberOfVotes(previousNVotes * changeCoefficient + currentNVotes);
        }
    }

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
