package com.sm.identity.exception_handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.sm.identity.filters.IdentityServiceLoggingFilter;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	private Logger logger = LoggerFactory.getLogger(IdentityServiceLoggingFilter.class);

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException ex){
		logger.error("{}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Entered mail is already registered, use a different mail or try logging in");
	}
	
	@ExceptionHandler(InternalAuthenticationServiceException.class)
	public ResponseEntity<String> handleInternalAuthenticationServiceException(InternalAuthenticationServiceException ex){
		logger.error("{}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body("wrong mail id, use your registered mail id");
	}
	
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException ex){
		logger.error("{}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Wrong password, please try again");
	}
	
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<String> handleRuntimeException(RuntimeException ex){
		logger.error("{}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}
}
