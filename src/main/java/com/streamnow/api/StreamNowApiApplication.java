package com.streamnow.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
		info = @Info(
				title = "StreamNow API",
				version = "1.0"
		)
)
@SpringBootApplication
public class StreamNowApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(StreamNowApiApplication.class, args);
	}
}
