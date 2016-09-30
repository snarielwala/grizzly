package com.grizzly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Created by Samarth 9/25/16
 */
@SpringBootApplication
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class GrizzlyApplication {

	public static void main(String[] args) {
		SpringApplication.run(GrizzlyApplication.class, args);
	}
}
