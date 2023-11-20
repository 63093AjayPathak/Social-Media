package com.sm.user_service.exception_handler;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.sm.user_service.DTO.ApiResponse;
import com.sm.user_service.exceptions.NoUserFound;


@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(NoUserFound.class)
	public ResponseEntity<ApiResponse> handleNoUserFoundException(NoUserFound ex){
		return ResponseEntity.internalServerError().body(ApiResponse.builder().message(ex.getMessage()).timeStamp(LocalDateTime.now()).build());
	}
}
