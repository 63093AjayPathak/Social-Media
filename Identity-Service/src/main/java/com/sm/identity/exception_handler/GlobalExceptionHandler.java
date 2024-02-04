package com.sm.identity.exception_handler;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException ex){
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Entered mail is already registered, use a different mail or try logging in");
	}
	
	@ExceptionHandler(InternalAuthenticationServiceException.class)
	public ResponseEntity<String> handleInternalAuthenticationServiceException(InternalAuthenticationServiceException ex){
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body("wrong mail id, use your registered mail id");
	}
	
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException ex){
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Wrong password, please try again");
	}
	
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<String> handleRuntimeException(RuntimeException ex){
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}
}
