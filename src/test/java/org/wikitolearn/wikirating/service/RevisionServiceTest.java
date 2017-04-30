/**
 * 
 */
package org.wikitolearn.wikirating.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.wikitolearn.wikirating.exception.RevisionNotFoundException;
import org.wikitolearn.wikirating.model.Revision;
import org.wikitolearn.wikirating.repository.RevisionRepository;

/**
 * @author aletundo
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class RevisionServiceTest {
	
	@Mock
	private RevisionRepository revisionRepository;
	
	@InjectMocks
	private RevisionService revisionService;
	
	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testGetRevision(){
		Date now = new Date();
		Revision revision =  new Revision(2, "en", 1, 1, 123456, now);
		when(revisionRepository.findByLangRevId("en_2")).thenReturn(revision);
		Revision result = revisionService.getRevision("en_2");
		assertEquals(2, result.getRevId());
		assertEquals("en", result.getLang());
		assertEquals(1, result.getParentId());
		assertEquals(1, result.getUserId());
		assertEquals(123456, result.getLength());
		assertEquals(now, result.getTimestamp());
	}
	
	@Test(expected = RevisionNotFoundException.class)
	public void testGetRevisionNotFound(){
		when(revisionRepository.findByLangRevId("en_1")).thenReturn(null);
		revisionService.getRevision("en_1");
	}
	
	@Test
	public void testAddPage(){
		Date now = new Date();
		Revision revision =  new Revision(2, "en", 1, 1, 123456, now);
		when(revisionRepository.save(revision)).thenReturn(revision);
		Revision result = revisionService.addRevision(2, "en", 1, 1, 123456, now);
		assertEquals(2, result.getRevId());
		assertEquals("en", result.getLang());
		assertEquals(1, result.getParentId());
		assertEquals(1, result.getUserId());
		assertEquals(123456, result.getLength());
		assertEquals(now, result.getTimestamp());
	}
	
	@Test
	public void testUpdateRevision(){
		Date now = new Date();
		Revision revision =  new Revision(2, "en", 1, 1, 123456, now);
		when(revisionRepository.save(revision)).thenReturn(revision);
		Revision result = revisionService.updateRevision(revision);
		assertEquals(2, result.getRevId());
		assertEquals("en", result.getLang());
		assertEquals(1, result.getParentId());
		assertEquals(1, result.getUserId());
		assertEquals(123456, result.getLength());
		assertEquals(now, result.getTimestamp());
	}
	
	@Test
	public void testDeleteRevisionsOfPage(){
		Set<Revision> revisions = new HashSet<Revision>();
		Revision r1 =  new Revision(2, "en", 1, 1, 123456, new Date());
		Revision r2 =  new Revision(3, "en", 2, 1, 123456, new Date());
		Revision r3 =  new Revision(4, "en", 3, 1, 123456, new Date());
		revisions.add(r1);
		revisions.add(r2);
		revisions.add(r3);
		when(revisionRepository.findAllRevisionOfPage("en_1")).thenReturn(revisions);
		revisionService.deleteRevisionsOfPage("en_1");
		verify(revisionRepository, times(1)).delete(revisions);
	}
	
	@Test(expected = RevisionNotFoundException.class)
	public void testDeleteRevisionsOfPageNotFound(){
		Set<Revision> revisions = new HashSet<Revision>();
		when(revisionRepository.findAllRevisionOfPage("en_1")).thenReturn(revisions);
		revisionService.deleteRevisionsOfPage("en_1");
	}
	
	@Test
	public void testGetRevisionsOfPage(){
		Set<Revision> revisions = new HashSet<Revision>();
		Revision r1 =  new Revision(2, "en", 1, 1, 123456, new Date());
		Revision r2 =  new Revision(3, "en", 2, 1, 123456, new Date());
		Revision r3 =  new Revision(4, "en", 3, 1, 123456, new Date());
		revisions.add(r1);
		revisions.add(r2);
		revisions.add(r3);
		when(revisionRepository.findAllRevisionOfPage("en_1")).thenReturn(revisions);
		Set<Revision> result = revisionService.getRevisionsOfPage("en_1");
		assertEquals(3, result.size());
	}
	
	@Test(expected = RevisionNotFoundException.class)
	public void testGetRevisionsOfPageNotFound(){
		Set<Revision> revisions = new HashSet<Revision>();
		when(revisionRepository.findAllRevisionOfPage("en_1")).thenReturn(revisions);
		revisionService.getRevisionsOfPage("en_1");
	}
}
