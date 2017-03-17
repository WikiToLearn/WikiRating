package org.wikitolearn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@EnableAsync
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WikiRatingApplication {

	public static void main(String[] args) {
		SpringApplication.run(WikiRatingApplication.class, args);
	}
}
