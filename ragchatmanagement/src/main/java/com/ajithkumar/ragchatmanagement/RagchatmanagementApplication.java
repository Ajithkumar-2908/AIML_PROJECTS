package com.ajithkumar.ragchatmanagement;

import com.ajithkumar.ragchatmanagement.properties.CorsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication
@EnableConfigurationProperties(CorsProperties.class)
public class RagchatmanagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(RagchatmanagementApplication.class, args);
	}

}
