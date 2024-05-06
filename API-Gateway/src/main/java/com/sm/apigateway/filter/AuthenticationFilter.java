package com.sm.apigateway.filter;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import com.sm.apigateway.exception.UnAuthorizedAccessException;
import com.sm.apigateway.util.JwtUtil;


@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

	@Autowired
	private RouteValidator validator;
	
	@Autowired
	private JwtUtil jwtUtil;
	 
	public AuthenticationFilter() {
        super(Config.class);
    }
	

	@Override
	public GatewayFilter apply(Config config) {
		
		return new OrderedGatewayFilter ((exchange,chain)->{
			if(validator.isSecured.test(exchange.getRequest())) {
//				header contains token or not
				if(!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
					throw new UnAuthorizedAccessException("missing authorization header");
				}
				
				String authHeader=exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
				if(authHeader!=null && authHeader.startsWith("Bearer ")) {
					authHeader=authHeader.substring(7);
				}
				try {
					jwtUtil.validateToken(authHeader);
				}
				catch(Exception ex) {
					throw new UnAuthorizedAccessException("Unauthorized access to application");
				}
			}
			return chain.filter(exchange);
		},1);
	}
	
	public static class Config {

    }
}
