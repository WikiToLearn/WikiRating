package org.wikitolearn.wikirating;

import java.util.concurrent.Executor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 
 * @author aletundo
 *
 */
@SpringBootApplication
@EnableAsync
@EntityScan("org.wikitolearn.wikirating.wikirating.model")
@EnableNeo4jRepositories(basePackages = "org.wikitolearn.wikirating.wikirating..repository")
@EnableTransactionManagement
public class WikiRatingApplication extends AsyncConfigurerSupport{

	public static void main(String[] args) {
		SpringApplication.run(WikiRatingApplication.class, args);
	}
	
	@Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setThreadNamePrefix("ServicesTread-");
        executor.initialize();
        return executor;
    }
}
