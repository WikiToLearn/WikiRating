/**
 * 
 */
package org.wikitolearn.wikirating.repository;

import static org.junit.Assert.assertEquals;

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
import org.wikitolearn.wikirating.model.graph.Page;

/**
 * @author aletundo
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class PageRepositoryTest {
	
	@Autowired
	private Session session;
	
	@Autowired
	private PageRepository<Page> pageRepository;
	
	
	@Before
	public void setup() {
		Page page1 = new Page(1, "Title1", "en", "en_1");
		Page page2 = new Page(2, "Title2", "it", "it_2");
		Page page3 = new Page(3, "Title3", "en", "en_3");
		Page page4 = new Page(4, "Title4", "en", "en_4");
		Page page5 = new Page(5, "Title5", "en", "en_5");
		Page page6 = new Page(6, "Title6", "it", "it_6");
		
		pageRepository.save(page1);
		pageRepository.save(page2);
		pageRepository.save(page3);
		pageRepository.save(page4);
		pageRepository.save(page5);
		pageRepository.save(page6);
		
		page2.addLabel("CourseRoot");
		page3.addLabel("CourseLevelTwo");
		
		pageRepository.save(page2);
		pageRepository.save(page3);
		
	}

	@After
	public void teardown() {
		session.purgeDatabase();
	}
	
	@Test
	public void testFindAllUncategorizedPages(){
		List<Page> result = pageRepository.findAllUncategorizedPages();
		assertEquals(4, result.size());
		
	}
	
	@Test
	public void testFindAllUncategorizedPagesByLang(){
		List<Page> resultEn = pageRepository.findAllUncategorizedPages("en");
		assertEquals(3, resultEn.size());
		
		List<Page> resultIt = pageRepository.findAllUncategorizedPages("it");
		assertEquals(1, resultIt.size());
		
	}
	

}
