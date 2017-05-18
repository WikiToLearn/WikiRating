/**
 * 
 */
package org.wikitolearn.wikirating.repository;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.wikitolearn.wikirating.model.graph.Revision;
import org.wikitolearn.wikirating.model.graph.User;
import org.wikitolearn.wikirating.model.graph.Vote;

/**
 * @author aletundo
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class VoteRepositoryTest {
	
	@Autowired
	private Session session;
	@Autowired
	private VoteRepository voteRepository;
	@Autowired
	private RevisionRepository revisionRepository;
	@Autowired
	private UserRepository userRepository;
	
	@Before
	public void setup(){
		User u1 = new User("User", 1, 0.0, 0.0, 0.0);
		User u2 = new User("User2", 2, 0.0, 0.0, 0.0);
		Revision revision =  new Revision(2, "en", 1, 1, 123456, new Date());
		
		Vote v1 = new Vote(2.5, 1.0, new Date());
		Vote v2 = new Vote(5, 1.0, new Date());
		Vote v3 = new Vote(3.5, 1.0, new Date());
		v1.setRevision(revision);
		v2.setRevision(revision);
		v3.setRevision(revision);
		v1.setUser(u1);
		v2.setUser(u1);
		v3.setUser(u2);
		
		Set<Vote> votes = new HashSet<>();
		votes.add(v1);
		votes.add(v2);
		votes.add(v3);
		
		voteRepository.save(votes);
	}
	
	@After
	public void teardown() {
		session.purgeDatabase();
	}
	
	@Test
	public void testGetAllVotesOfRevision(){
		List<Vote> votes = voteRepository.getAllVotesOfRevision("en_2");
		User u1 = userRepository.findByUserId(1);
		User u2 = userRepository.findByUserId(2);
		Revision revision = revisionRepository.findByLangRevId("en_2");
		assertEquals(2, u1.getVotes().size());
		assertEquals(1, u2.getVotes().size());
		assertEquals(3, revision.getVotes().size());
		assertEquals(3, votes.size());
	}

}
