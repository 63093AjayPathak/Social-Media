package com.sm.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@GetMapping("/login")
	public ResponseEntity<?> auathenticate(){
		
		return ResponseEntity.status(HttpStatus.FOUND).body(new String("User logged in"));
	}
}
