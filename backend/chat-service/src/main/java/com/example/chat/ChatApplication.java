package com.example.chat;

//import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import com.example.common.config.CommonJwtAutoConfiguration;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableCaching
@EnableJpaAuditing
@EnableRabbit
@EnableAsync
@Import({CommonJwtAutoConfiguration.class})
public class ChatApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatApplication.class, args);
	}

}
