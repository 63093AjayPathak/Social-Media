package com.sm.identity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class IdentityService1Application {

	public static void main(String[] args) {
		SpringApplication.run(IdentityService1Application.class, args);
	}

}
