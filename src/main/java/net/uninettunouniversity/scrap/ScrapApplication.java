package net.uninettunouniversity.scrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;

@SpringBootApplication
public class ScrapApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScrapApplication.class, args);
	}
	
	@Bean
	public RecordMessageConverter converter() {
		return new JsonMessageConverter();
	}

}
