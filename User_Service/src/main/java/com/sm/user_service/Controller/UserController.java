package com.sm.user_service.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sm.user_service.DTO.ApiResponse;
import com.sm.user_service.DTO.UserDTO;
import com.sm.user_service.Entity.User;
import com.sm.user_service.Service.IUserService;

@RestController
@RequestMapping("/")
@CrossOrigin
public class UserController {
	
	@Autowired
	IUserService userService;
	
	@GetMapping("/")
	public ResponseEntity<?> getAllUsers(){
		return ResponseEntity.status(HttpStatus.OK).body(userService.allUsers());
	}

	@PostMapping("/signup")
	public ResponseEntity<?> createNewUser(@RequestBody UserDTO user){
		
//		return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("new user created",userService.signUp(user)));
		return ResponseEntity.status(HttpStatus.CREATED).body(new String("New User Created"));
		
	}
	
	@PostMapping("/signin")
	public ResponseEntity<?> logIn(@RequestBody UserDTO user){
		
		User us=userService.signIn(user);
		
		if(user!=null) {
			return ResponseEntity.status(HttpStatus.OK).body(us);
		}
		else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new String("User with given email and password not found"));
		}
	}
}
