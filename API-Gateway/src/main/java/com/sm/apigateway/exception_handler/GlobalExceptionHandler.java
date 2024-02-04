package com.sm.apigateway.exception_handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sm.apigateway.exception.UnAuthorizedAccessException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(UnAuthorizedAccessException.class)
	public ResponseEntity<String> handleUnAuthorizedAccessException(UnAuthorizedAccessException ex){
		
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
	}
	
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<String> handleRuntimeException(RuntimeException ex){
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
	}
}
