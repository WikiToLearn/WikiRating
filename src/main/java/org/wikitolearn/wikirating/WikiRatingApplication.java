package org.wikitolearn.wikirating;

import java.util.concurrent.Executor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 
 * @author aletundo
 *
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
@EntityScan("org.wikitolearn.wikirating.model.graph")
@EnableNeo4jRepositories(basePackages = "org.wikitolearn.wikirating.repository")
@EnableTransactionManagement
public class WikiRatingApplication extends AsyncConfigurerSupport{

	public static void main(String[] args) {
		SpringApplication.run(WikiRatingApplication.class, args);
	}
	
	@Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setThreadNamePrefix("ServicesThread-");
        executor.initialize();
        return executor;
    }
}
