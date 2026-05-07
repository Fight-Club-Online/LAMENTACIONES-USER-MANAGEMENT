package com.clubfight.LAMENTACIONES_USER_MANAGEMENT;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@Disabled("Requiere MongoDB y RabbitMQ corriendo localmente")
class LamentacionesUserManagementApplicationTests {

	@Test
	void contextLoads() {
	}

}
