package com.sm.user_service.exceptions;

public class NoUserFound extends RuntimeException {
	
	public NoUserFound(String message) {
		super(message);
	}
}
