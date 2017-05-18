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
public class MetadataRepositoryTest {
	
	@Autowired
	private Session session;
	
	@Autowired
	private MetadataRepository metadataRepository;
	
	@Autowired
	private ProcessRepository processRepository;
	
	private Process candidateLatestProcess;
	
	@Before
	public void setup(){
		Metadata m = new Metadata(MetadataType.PROCESSES);
		Process p1 = new Process(UUID.randomUUID().toString(), ProcessType.INIT, ProcessStatus.DONE, new Date(), new Date());
		candidateLatestProcess = new Process(ProcessType.FETCH);
		
		m.setLatestProcess(p1);
		candidateLatestProcess.setPreviousProcess(p1);


		metadataRepository.save(m);
		processRepository.save(candidateLatestProcess);
	}
	
	@After
	public void teardown() {
		session.purgeDatabase();
	}
	
	@Test
	public void testUpdateLatestProcess(){
		metadataRepository.updateLatestProcess();
		Process result = processRepository.getLatestProcess();
		assertTrue(candidateLatestProcess.equals(result));
	}

}
