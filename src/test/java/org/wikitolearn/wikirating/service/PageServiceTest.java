/**
 * 
 */
package org.wikitolearn.wikirating.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.wikitolearn.wikirating.exception.PageNotFoundException;
import org.wikitolearn.wikirating.model.graph.Page;
import org.wikitolearn.wikirating.repository.PageRepository;
import org.wikitolearn.wikirating.service.mediawiki.PageMediaWikiService;

/**
 * @author aletundo
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class PageServiceTest {
	
	@Mock
	private PageRepository<Page> pageRepository;
	
	@Mock
	private PageMediaWikiService pageMediaWikiService;
	
	@InjectMocks
	private PageService pageService;
	
	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testInitPages() throws InterruptedException, ExecutionException{
		List<Page> pages = new ArrayList<Page>();
		pages.add(new Page(1, "Title", "en", "en_1"));
		pages.add(new Page(2, "Title2/Subtitle2", "en", "en_2"));
		pages.add(new Page(3, "Title3/Subtitle3/Subsubtitle3", "en", "en_4"));
		when(pageMediaWikiService.getAll("https://en.domain.org/api.php")).thenReturn(pages);
		CompletableFuture<Boolean> result = pageService.initPages("en", "https://en.domain.org/api.php");
		assertTrue(result.get());
		verify(pageRepository, times(1)).save(pages);
		
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
		Page result = pageService.addPage(1, "Title", "en");
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
	

}
