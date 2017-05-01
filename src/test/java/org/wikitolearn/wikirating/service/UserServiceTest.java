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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.wikitolearn.wikirating.exception.UserNotFoundException;
import org.wikitolearn.wikirating.model.Revision;
import org.wikitolearn.wikirating.model.User;
import org.wikitolearn.wikirating.repository.RevisionRepository;
import org.wikitolearn.wikirating.repository.UserRepository;
import org.wikitolearn.wikirating.service.mediawiki.UserMediaWikiService;

/**
 * @author aletundo
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class UserServiceTest {
	
	@Mock
	private UserRepository userRepository;
	@Mock
	private RevisionRepository revisionRepository;
	@Mock
	private UserMediaWikiService userMediaWikiService;
	
	@InjectMocks
	private UserService userService;
	
	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testGetUser(){
		User user = new User("User", 1, 0.0, 0.0, 0.0);
		when(userRepository.findByUserId(1)).thenReturn(user);
		User result = userService.getUser(1);
		assertEquals(1, result.getUserId());
		assertEquals("User", result.getUsername());
		assertEquals(0.0, result.getContributesReliability(), 0.1);
		assertEquals(0.0, result.getTotalReliability(), 0.1);
		assertEquals(0.0, result.getVotesReliability(), 0.1);
	}
	
	@Test(expected = UserNotFoundException.class)
	public void testGetUserNotFound(){
		when(userRepository.findByUserId(1)).thenReturn(null);
		userService.getUser(1);
	}
	
	@Test
	public void testAddUsers(){
		User u1 = new User("User", 1, 0.0, 0.0, 0.0);
		User u2 = new User("User2", 2, 0.0, 0.0, 0.0);
		User u3 = new User("User3", 3, 0.0, 0.0, 0.0);
		List<User> users = new ArrayList<>();
		users.add(u1);
		users.add(u2);
		users.add(u3);
		when(userRepository.save(users)).thenReturn(users);
		List<User> result = userService.addUsers(users);
		assertEquals(3, result.size());
	}
	
	@Test
	public void testSetAuthroshipSingleRevision(){
		User user = new User("User", 1, 0.0, 0.0, 0.0);
		Revision revision = new Revision(2, "en", 1, 1, 123456, new Date());
		when(userRepository.findByUserId(revision.getUserId())).thenReturn(user);
		userService.setAuthorship(revision);
		assertEquals(1, user.getAuthorship().size());
		verify(userRepository, times(1)).save(user);
	}
	
	@Test
	public void testSetAuthroshipRevisionsGivenUser(){
		User user = new User("User", 1, 0.0, 0.0, 0.0);
		Set<Revision> revisions = new HashSet<Revision>();
		Revision r1 =  new Revision(2, "en", 1, 1, 123456, new Date());
		Revision r2 =  new Revision(3, "en", 2, 1, 123456, new Date());
		Revision r3 =  new Revision(4, "en", 3, 1, 123456, new Date());
		revisions.add(r1);
		revisions.add(r2);
		revisions.add(r3);
		userService.setAuthorship(revisions, user);
		assertEquals(3, user.getAuthorship().size());
		verify(userRepository, times(1)).save(user);
	}
	
	@Test
	public void testInitUsers() throws InterruptedException, ExecutionException{
		User u1 = new User("User", 1, 0.0, 0.0, 0.0);
		User u2 = new User("User2", 2, 0.0, 0.0, 0.0);
		User u3 = new User("User3", 3, 0.0, 0.0, 0.0);
		List<User> users = new ArrayList<>();
		users.add(u1);
		users.add(u2);
		users.add(u3);
		String apiUrl = "https://en.domain.org/api.php";
		when(userMediaWikiService.getAll(apiUrl)).thenReturn(users);
		CompletableFuture<Boolean> result = userService.initUsers(apiUrl);
		verify(userRepository, times(1)).save(users);
		assertTrue(result.get());
	}
	
	@Test
	public void testInitAuthorship() throws InterruptedException, ExecutionException{
		User u1 = new User("User", 1, 0.0, 0.0, 0.0);
		User u2 = new User("User2", 2, 0.0, 0.0, 0.0);
		User u3 = new User("User3", 3, 0.0, 0.0, 0.0);
		List<User> users = new ArrayList<>();
		users.add(u1);
		users.add(u2);
		users.add(u3);
		Set<Revision> revisionsU0 = new HashSet<Revision>();
		Set<Revision> revisionsU1 = new HashSet<Revision>();
		Set<Revision> revisionsU2 = new HashSet<Revision>();
		Set<Revision> revisionsU3 = new HashSet<Revision>();
		Revision r1 =  new Revision(2, "en", 1, 1, 123456, new Date());
		Revision r2 =  new Revision(3, "en", 2, 1, 123456, new Date());
		Revision r3 =  new Revision(4, "en", 3, 1, 123456, new Date());
		Revision r4 =  new Revision(5, "en", 4, 2, 123456, new Date());
		Revision r5 =  new Revision(6, "en", 5, 3, 123456, new Date());
		Revision r6 =  new Revision(7, "en", 6, 3, 123456, new Date());
		Revision r7 =  new Revision(8, "en", 7, 3, 123456, new Date());
		Revision r8 =  new Revision(9, "en", 0, 0, 123456, new Date());
		revisionsU0.add(r8);
		revisionsU1.add(r1);
		revisionsU1.add(r2);
		revisionsU1.add(r3);
		revisionsU2.add(r4);
		revisionsU3.add(r5);
		revisionsU3.add(r6);
		revisionsU3.add(r7);
		when(userRepository.findAll()).thenReturn(users);
		when(revisionRepository.findByUserId(0)).thenReturn(revisionsU0);
		when(revisionRepository.findByUserId(1)).thenReturn(revisionsU1);
		when(revisionRepository.findByUserId(2)).thenReturn(revisionsU2);
		when(revisionRepository.findByUserId(3)).thenReturn(revisionsU3);
		CompletableFuture<Boolean> result = userService.initAuthorship();
		assertTrue(result.get());
		assertEquals(3, u1.getAuthorship().size());
		assertEquals(1, u2.getAuthorship().size());
		assertEquals(3, u3.getAuthorship().size());
	}
}