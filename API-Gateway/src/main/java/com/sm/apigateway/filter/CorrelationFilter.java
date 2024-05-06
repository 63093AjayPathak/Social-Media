package com.sm.apigateway.filter;

import java.util.Arrays;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;


//This filter will add a co-relation id to each request which can be used to trace a request
@Component
public class CorrelationFilter extends AbstractGatewayFilterFactory<CorrelationFilter.Config> {
	
	private Logger logger = LoggerFactory.getLogger(CorrelationFilter.class);
	
	public CorrelationFilter() {
        super(Config.class);
    }

	@Override
	public GatewayFilter apply(Config config) {
		
		return new OrderedGatewayFilter ((exchange,chain)->{
			ServerHttpRequest req=exchange.getRequest();
			
			if(req.getHeaders().get("X-Corelation-ID")==null)
			req.mutate().header("X-Corelation-ID",UUID.randomUUID().toString() ).build();
			
			logger.info("{}", Arrays.asList(req.getHeaders().get("X-Corelation-ID"),req.getURI().toString().split("/")[3], req.getMethod(), req.getRemoteAddress().getAddress().getHostAddress()));
			return chain.filter(exchange.mutate().request(req).build());
		},2);
	}
	
	public static class Config {

    }
}
