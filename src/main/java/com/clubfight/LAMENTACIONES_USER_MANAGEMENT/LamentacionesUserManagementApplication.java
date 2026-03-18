package com.clubfight.LAMENTACIONES_USER_MANAGEMENT;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableMongoRepositories(basePackages = "com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.repositories.mongo")
@EnableRedisRepositories(basePackages = "com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.repositories.redis")
public class LamentacionesUserManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(LamentacionesUserManagementApplication.class, args);
	}

}
