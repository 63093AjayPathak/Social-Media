package com.sm.apigateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import reactor.core.publisher.Mono;

@Configuration
public class GatewayConfig {
	
	@Bean
	public KeyResolver keyResolver() {
		System.out.println("KeyResolver called");
		return exchange -> Mono.just(exchange.getRequest().getURI().toString().split("/")[3]);
	}
	
//	@Bean
//	public RedisRateLimiter redisRateLimiter() {
//		return new RedisRateLimiter(2,4,1);
//	}
}
