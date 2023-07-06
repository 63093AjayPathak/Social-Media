package com.sm.user_service.DTO;

import java.time.LocalDateTime;

import com.sm.user_service.Entity.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {

	
	private String message;
	private User user;
	private LocalDateTime timeStamp;
	
	public ApiResponse(String message) {
		this();
		this.message = message;
		this.timeStamp=LocalDateTime.now();
	}
	public ApiResponse(String message, User user) {
		this();
		this.message = message;
		this.user=user;
		this.timeStamp=LocalDateTime.now();
	}
}
