/**
 * 
 */
package org.wikitolearn.wikirating.repository;

import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.wikitolearn.wikirating.model.graph.Metadata;
import org.wikitolearn.wikirating.model.graph.Process;
import org.wikitolearn.wikirating.util.enums.MetadataType;
import org.wikitolearn.wikirating.util.enums.ProcessStatus;
import org.wikitolearn.wikirating.util.enums.ProcessType;

/**
 * @author aletundo
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class ProcessRepositoryTest {
	
	@Autowired
	private Session session;
	
	@Autowired
	private MetadataRepository metadataRepository;
	
	@Autowired
	private ProcessRepository processRepository;
	
	private Process latestProcess;
	
	@Before
	public void setup(){
		Metadata m = new Metadata(MetadataType.PROCESSES);
		Process p1 = new Process(UUID.randomUUID().toString(), ProcessType.INIT, ProcessStatus.DONE, new Date(), new Date());
		latestProcess = new Process(UUID.randomUUID().toString(), ProcessType.FETCH, ProcessStatus.ONGOING, new Date(), new Date());
		
		latestProcess.setPreviousProcess(p1);
		m.setLatestProcess(latestProcess);

		metadataRepository.save(m);
		processRepository.save(latestProcess);
	}
	
	
	@After
	public void teardown() {
		session.purgeDatabase();
	}
	
	@Test
	public void testGetLatestProcess(){
		Process result = processRepository.getLatestProcess();
		assertTrue(latestProcess.equals(result));
	}
	
	@Test
	public void testGetOnGoingProcess(){
		Process result = processRepository.getOnGoingProcess();
		assertTrue(latestProcess.equals(result));
	}
	
	@Test
	public void testGetLatestProcessByType(){
		Process result = processRepository.getLatestProcessByType(ProcessType.FETCH);
		assertTrue(latestProcess.equals(result));
	}
	
}
