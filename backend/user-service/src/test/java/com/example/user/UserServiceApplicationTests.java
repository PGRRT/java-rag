package com.example.user;

import com.example.common.config.CommonJwtAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class UserServiceApplicationTests {

//	@Container
//	@ServiceConnection
//	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

//	@Test
//	void contextLoads() {
//		assert postgres != null;
//	}

}
