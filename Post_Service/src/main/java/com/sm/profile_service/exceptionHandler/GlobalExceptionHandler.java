package com.sm.profile_service.exceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.sm.profile_service.exceptions.ContentNotFoundException;

public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	
	@ExceptionHandler(ContentNotFoundException.class)
	public ResponseEntity<String> contentNotFoundExceptionHandler(ContentNotFoundException ex){
		
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new String(ex.getMessage()));
	}
}
