package com.sm.user_service.DTO;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {

	
	private String message;
	private LocalDateTime timeStamp;
	

	public ApiResponse(String message) {
		this();
		this.message = message;
		this.timeStamp=LocalDateTime.now();
	}
}
