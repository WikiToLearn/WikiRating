/**
 * 
 */
package org.wikitolearn.wikirating.service;

import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.wikitolearn.wikirating.exception.PageNotFoundException;
import org.wikitolearn.wikirating.model.graph.Page;
import org.wikitolearn.wikirating.model.graph.Revision;
import org.wikitolearn.wikirating.repository.PageRepository;

/**
 * @author aletundo
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class PageServiceTest {
	
	@Mock
	private PageRepository pageRepository;
	
	@InjectMocks
	private PageService pageService;
	
	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testGetPageByLangPageId(){
		Page page = new Page(1, "Title", "en", "en_1");
		when(pageRepository.findByLangPageId("en_1")).thenReturn(page);
		Page result = pageService.getPage("en_1");
		assertEquals(1, result.getPageId());
		assertEquals("Title", result.getTitle());
		assertEquals("en", result.getLang());
		assertEquals("en_1", result.getLangPageId());
	}
	
	@Test
	public void testGetPageByPageIdAndLang(){
		Page page = new Page(1, "Title", "en", "en_1");
		when(pageRepository.findByLangPageId("en_1")).thenReturn(page);
		Page result = pageService.getPage(1, "en");
		assertEquals(1, result.getPageId());
		assertEquals("Title", result.getTitle());
		assertEquals("en", result.getLang());
		assertEquals("en_1", result.getLangPageId());
	}
	
	@Test(expected = PageNotFoundException.class)
	public void testGetPageByPageIdAndLangNotFound(){
		when(pageRepository.findByLangPageId("en_1")).thenReturn(null);
		pageService.getPage(1, "en");
	}
	
	@Test(expected = PageNotFoundException.class)
	public void testGetPageByLangPageIdNotFound(){
		when(pageRepository.findByLangPageId("en_1")).thenReturn(null);
		pageService.getPage("en_1");
	}
	
	@Test(expected = PageNotFoundException.class)
	public void testDeletePageNotFound(){
		when(pageRepository.findByTitleAndLang("Title", "en")).thenReturn(null);
		pageService.deletePage("Title", "en");
	}
	
	@Test
	public void testDeletePage(){
		Page page = new Page(1, "Title", "en", "en_1");
		when(pageRepository.findByTitleAndLang("Title", "en")).thenReturn(page);
		pageService.deletePage("Title", "en");
		verify(pageRepository, times(1)).delete(page);
	}
	
	@Test(expected = PageNotFoundException.class)
	public void testMovePageNotFound(){
		when(pageRepository.findByLangPageId("en_1")).thenReturn(null);
		pageService.movePage("Title", "NewTitle", "en");
	}
	
	@Test
	public void testMovePage(){
		Page page = new Page(1, "Title", "en", "en_1");
		when(pageRepository.findByTitleAndLang("Title", "en")).thenReturn(page);
		Page result = pageService.movePage("Title", "NewTitle", "en");
		assertEquals("NewTitle", result.getTitle());
	}
	
	@Test
	public void testAddPage(){
		Page page = new Page(1, "Title", "en", "en_1");
		when(pageRepository.save(page)).thenReturn(page);
		Page result = pageService.addPage(1, "Title", "en", new Revision());
		assertEquals(1, result.getPageId());
		assertEquals("Title", result.getTitle());
		assertEquals("en", result.getLang());
		assertEquals("en_1", result.getLangPageId());
	}
	
	@Test
	public void testGetUncategorizedPages(){
		List<Page> uncategorizedPages = new ArrayList<Page>();
		uncategorizedPages.add(new Page(1, "Title", "en", "en_1"));
		uncategorizedPages.add(new Page(2, "Title2", "en", "en_2"));
		uncategorizedPages.add(new Page(3, "Title3", "en", "en_4"));
		when(pageRepository.findAllUncategorizedPages("en")).thenReturn(uncategorizedPages);
		List<Page> result = pageService.getUncategorizedPages("en");
		assertEquals(3, result.size());
	}
	
	@Test
	public void testGetCourseRootPages(){
		List<Page> uncategorizedPages = new ArrayList<Page>();
		Page p1 = new Page(1, "Title", "en", "en_1");
		p1.addLabel("CourseRoot");
		Page p2 = new Page(2, "Title2", "en", "en_2");
		p2.addLabel("CourseRoot");
		Page p3 = new Page(3, "Title3", "en", "en_3");
		p3.addLabel("CourseRoot");
		uncategorizedPages.add(p1);
		uncategorizedPages.add(p2);
		uncategorizedPages.add(p3);
		when(pageRepository.findAllCourseRootPages("en")).thenReturn(uncategorizedPages);
		List<Page> result = pageService.getCourseRootPages("en");
		assertEquals(3, result.size());
	}

}
