package com.sm.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity  // here we are using this annotation instead of EnableWebSecurity because Spring Cloud Gateway supports WebFluxsecurity
public class SecurityConfig {

	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {
		
		httpSecurity
		.cors()
		.and()
		.csrf().disable()
		.authorizeExchange(exchanges -> exchanges
        		.pathMatchers("/user/signup","/user/signin")
        		.permitAll()
        		.anyExchange()
        		.authenticated()
        		);
		
		
		return httpSecurity.build();
	}
}