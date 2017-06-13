/**
 * 
 */
package org.wikitolearn.wikirating.repository;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.wikitolearn.wikirating.model.graph.TemporaryVote;

/**
 * @author aletundo
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class TemporaryVoteRepositoryTest {
	@Autowired
	private Session session;
	@Autowired
	private TemporaryVoteRepository temporaryVoteRepository;
	
	@Before
	public void setup(){
		Date d1 = new GregorianCalendar(2017, Calendar.JUNE, 1).getTime();
		Date d2 = new GregorianCalendar(2017, Calendar.JUNE, 3).getTime();
		
		TemporaryVote tv1 = new TemporaryVote(3.5, 0.3, 1, 1, "en_1", d1);
		TemporaryVote tv2 = new TemporaryVote(1, 0.1, 2, 1, "en_1", d1);
		TemporaryVote tv3 = new TemporaryVote(3, 0.6, 3, 2, "en_2", d1);
		TemporaryVote tv4 = new TemporaryVote(2.5, 0.1, 1, 2, "en_2", d2);
		TemporaryVote tv5 = new TemporaryVote(5, 0.4, 1, 3, "en_3", d2);
		
		temporaryVoteRepository.save(tv1);
		temporaryVoteRepository.save(tv2);
		temporaryVoteRepository.save(tv3);
		temporaryVoteRepository.save(tv4);
		temporaryVoteRepository.save(tv5);
	}
	
	@After
	public void teardown(){
		session.purgeDatabase();
	}
	
	@Test
	public void testFindByTimestamp(){
		List<TemporaryVote> temporaryVotes = temporaryVoteRepository.findByTimestamp(new GregorianCalendar(2017, Calendar.JUNE, 2).getTime());
		assertEquals(3, temporaryVotes.size());
	}
}
