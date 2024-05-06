package com.sm.identity.filters;

import java.io.IOException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class IdentityServiceLoggingFilter implements Filter {
	
	private Logger logger = LoggerFactory.getLogger(IdentityServiceLoggingFilter.class);

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest req= (HttpServletRequest)request;
		logger.info("{}", Arrays.asList(req.getHeader("X-Corelation-ID"),req.getRequestURI(), req.getMethod(), req.getRemoteHost()));
		chain.doFilter(request, response);
		// TODO Auto-generated method stub

	}

}
