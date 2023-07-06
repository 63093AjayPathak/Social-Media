package com.sm.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity  // here we are using this annotation instead of EnableWebSecurity because Spring Cloud Gateway supports WebFluxsecurity
public class SecurityConfig {

	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {
		
		httpSecurity
		.cors()
		.and()
		.authorizeExchange()
		.anyExchange()
		.authenticated()
		.and()
		.oauth2Client()
		.and()
		.oauth2ResourceServer()
		.jwt();
		
		return httpSecurity.build();
	}
}
