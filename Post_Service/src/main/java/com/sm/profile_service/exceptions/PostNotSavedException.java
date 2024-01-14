package com.sm.profile_service.exceptions;

@SuppressWarnings("serial")
public class PostNotSavedException extends RuntimeException {

	public PostNotSavedException() {
		super();
	}
	
	public PostNotSavedException(String message) {
		super(message);
	}
}
