package com.sm.apigateway.exception;

public class UnAuthorizedAccessException extends RuntimeException {

	public UnAuthorizedAccessException(String message) {
		super(message);
	}
}
