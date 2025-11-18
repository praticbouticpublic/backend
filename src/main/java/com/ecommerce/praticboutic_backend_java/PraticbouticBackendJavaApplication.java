package com.ecommerce.praticboutic_backend_java;

import com.ecommerce.praticboutic_backend_java.configurations.ClientUrlsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
@RestController
@EnableConfigurationProperties(ClientUrlsProperties.class)
public class PraticbouticBackendJavaApplication {

	public static void main(String[] args) {
		SpringApplication.run(PraticbouticBackendJavaApplication.class, args);
	}
	
    @PostMapping("/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
      return String.format("Hello %s! avec git", name);
    }

}
