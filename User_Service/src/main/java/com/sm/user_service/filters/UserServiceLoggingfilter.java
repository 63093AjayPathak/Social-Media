package com.sm.user_service.filters;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class UserServiceLoggingfilter implements Filter {
	
	private Logger logger = LoggerFactory.getLogger(UserServiceLoggingfilter.class);

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		
		HttpServletRequest req= (HttpServletRequest)request;
		logger.info("{}", Arrays.asList(req.getHeader("X-Corelation-ID"),req.getRequestURI(), req.getMethod(), req.getRemoteHost()));
		chain.doFilter(request, response);

	}

}
