/**
 * 
 */
package org.wikitolearn.wikirating.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
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
import org.wikitolearn.wikirating.model.graph.Author;
import org.wikitolearn.wikirating.model.graph.CourseLevelThree;
import org.wikitolearn.wikirating.model.graph.Revision;
import org.wikitolearn.wikirating.model.graph.User;
import org.wikitolearn.wikirating.model.graph.queryresult.RevisionResult;

/**
 * @author aletundo
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class RevisionRepositoryTest {
	
	@Autowired
	private Session session;
	
	@Autowired
	private RevisionRepository revisionRepository;
	
	@Autowired
	private CourseLevelThreeRepository courseLevelThreeRepository;
	
	@Before
	public void setup() {
		CourseLevelThree page = new CourseLevelThree(1, "Title1", "en", "en_1");
		Revision r1 =  new Revision(2, "en", 1, 1, 123456, new Date());
		Revision r2 =  new Revision(3, "en", 2, 1, 123456, new Date());
		Revision r3 =  new Revision(4, "en", 3, 1, 123456, new Date());
		Revision r4 =  new Revision(5, "en", 4, 1, 123456, new Date());
		Revision r5 =  new Revision(6, "en", 5, 1, 123456, new Date());
		// Used to check if the revision is hydrated when a RevisionResult is returned
		User u1 = new User("User", 1, 0.0, 0.0, 0.0);
		Author a = new Author(0.4);
		a.setRevision(r1);
		a.setUser(u1);
		r1.setAuthor(a);
		
		page.setFirstRevision(r1);
		page.setLastRevision(r5);
		page.setLastValidatedRevision(r2);
		r5.setPreviousRevision(r4);
		r4.setPreviousRevision(r3);
		r3.setPreviousRevision(r2);
		r2.setPreviousRevision(r1);
		
		// Related nodes are automatically persisted
		courseLevelThreeRepository.save(page);
	}
	
	@After
	public void teardown() {
		session.purgeDatabase();
	}
	
	@Test
	public void findAllRevisionOfPageTest(){
		Set<Revision> result = revisionRepository.findAllRevisionOfPage("en_1");
		assertEquals(5, result.size());
	}
	
	@Test
	public void findAllRevisionOfPageOrderedTest(){
		List<Revision> result = revisionRepository.findAllRevisionOfPageOrdered("en_1");
		assertEquals(2, result.get(0).getRevId());
		assertEquals(3, result.get(1).getRevId());
		assertEquals(4, result.get(2).getRevId());
		assertEquals(5, result.get(3).getRevId());
		assertEquals(6, result.get(4).getRevId());
	}
	
	@Test
	public void getNotValidatedRevisionsChainTest(){
		RevisionResult result = revisionRepository.getNotValidatedRevisionsChain("en_1");
		assertTrue("en_3".equals(result.revision.getLangRevId()));
		
		// Check existing related nodes traversing :PREVIOUS_REVISION forward and backward
		assertNotNull(result.revision.getPreviousRevision());
		assertNotNull(result.revision.getPreviousRevision().getAuthor());
		assertEquals(1, result.revision.getPreviousRevision().getAuthor().getUser().getUserId());
		assertTrue("en_2".equals(result.revision.getPreviousRevision().getLangRevId()));
		assertNotNull(result.revision.getNextRevision());
		assertTrue("en_4".equals(result.revision.getNextRevision().getLangRevId()));
		assertNotNull(result.revision.getNextRevision().getNextRevision());
		assertTrue("en_5".equals(result.revision.getNextRevision().getNextRevision().getLangRevId()));
		assertNotNull(result.revision.getNextRevision().getNextRevision().getNextRevision());
		assertTrue("en_6".equals(result.revision.getNextRevision().getNextRevision().getNextRevision().getLangRevId()));
	}
	
	@Test
	public void findLastValidatedRevisionTest(){
		RevisionResult result = revisionRepository.findLastValidatedRevision("en_1");
		assertTrue("en_3".equals(result.revision.getLangRevId()));
		// Check existing related nodes
		assertNotNull(result.revision.getPreviousRevision());
		assertNotNull(result.revision.getPreviousRevision().getAuthor());
		assertEquals(1, result.revision.getPreviousRevision().getAuthor().getUser().getUserId());
		assertTrue("en_2".equals(result.revision.getPreviousRevision().getLangRevId()));
	}
	
	@Test
	public void findPreviousRevision(){
		Revision result = revisionRepository.findPreviousRevision("en_4");
		assertTrue("en_3".equals(result.getLangRevId()));
	}
}
