/**
 * 
 */
package org.wikitolearn.wikirating.repository;

import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.wikitolearn.wikirating.model.graph.CourseLevelThree;
import org.wikitolearn.wikirating.model.graph.Revision;

/**
 * @author aletundo
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class CourseLevelThreeRepositoryTest {
	@Autowired
	private Session session;
	
	@Autowired
	private CourseLevelThreeRepository courseLevelThreeRepository;
	
	@Autowired
	private RevisionRepository revisionRepository;
	
	private Revision candidateLatestRevision;
	
	@Before
	public void setup(){
		Revision r1 = new Revision(2, "en", 1, 1, 123456, new Date());
		CourseLevelThree page = new CourseLevelThree(1, "Title", "en", "en_1", r1);
		candidateLatestRevision = new Revision(3, "en", 2, 1, 123456, new Date());
		
		candidateLatestRevision.setPreviousRevision(r1);
		
		courseLevelThreeRepository.save(page);
		revisionRepository.save(candidateLatestRevision);
	}
	
	@After
	public void teardown() {
		session.purgeDatabase();
	}
	
	@Test
	public void updateLastRevision(){
		courseLevelThreeRepository.updateLastRevision("en_1");
		CourseLevelThree result = courseLevelThreeRepository.findByLangPageId("en_1");
		
		assertTrue(candidateLatestRevision.equals(result.getLastRevision()));
	}

}
