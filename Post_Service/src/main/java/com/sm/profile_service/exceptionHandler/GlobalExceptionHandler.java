package com.sm.profile_service.exceptionHandler;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.sm.profile_service.exceptions.ContentNotFoundException;

public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	
	private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	@ExceptionHandler(ContentNotFoundException.class)
	public ResponseEntity<String> contentNotFoundExceptionHandler(ContentNotFoundException ex){
		
		logger.error("{}", Arrays.asList(ex.getMessage(), ex.getStackTrace().toString()));
		
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new String(ex.getMessage()));
	}
}
