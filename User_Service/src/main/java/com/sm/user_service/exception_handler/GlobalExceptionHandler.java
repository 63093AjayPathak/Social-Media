package com.sm.user_service.exception_handler;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.sm.user_service.DTO.ApiResponse;
import com.sm.user_service.exceptions.NoUserFound;


@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	
	private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(NoUserFound.class)
	public ResponseEntity<ApiResponse> handleNoUserFoundException(NoUserFound ex){
		logger.error("{}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).body(ApiResponse.builder().message(ex.getMessage()).timeStamp(LocalDateTime.now()).build());
	}
	
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<String> handleRuntimeException(RuntimeException ex){
		logger.error("{}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
	}
}
