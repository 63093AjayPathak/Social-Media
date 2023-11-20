package com.sm.user_service.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sm.user_service.DTO.ApiResponse;
import com.sm.user_service.DTO.UserDTO;
import com.sm.user_service.node.User;
import com.sm.user_service.service.UserService;

@RestController
@RequestMapping("/")
public class UserController {
	
	
	@Autowired
	private UserService userService;

//	API end point to be hit whenever new user is created a node is to be created with given id  (from auth_service) and email
	@PostMapping("/")
	public ResponseEntity<ApiResponse> createnewUser(@RequestBody UserDTO userDto){
		
		String message=userService.newUser(userDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.builder().message(message).timeStamp(LocalDateTime.now()).build());
	}
	
//	below 2 methods are just for testing via postman
	
	@GetMapping("/")
	public List<User> getAllUsers(){	
		return userService.findAllUsers();
	}
	
	@GetMapping("/{id}")
	public User getUser(@PathVariable int id) {
		return userService.getSpecificUser(id);
	}
	
//	API end for sending a friend req to specific user
	@PostMapping("/friend_request")
	public ResponseEntity<ApiResponse> friendRequest(@RequestBody UserDTO friends){
		String message=userService.sendFriendRequest(friends.getSender(), friends.getReceiver());
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.builder().message(message).timeStamp(LocalDateTime.now()).build());
//		check for response that should be send in case of any exception for all methods
		
	}
	
//	API end point for adding a friend
	@PostMapping("/add_friend")
	public ResponseEntity<ApiResponse> addFriend(@RequestBody UserDTO friends){
		
		int sender=friends.getSender();
		int receiver=friends.getReceiver();
		
		userService.removeFriendRequest(sender,receiver );
		String resp= userService.acceptRequest(sender,receiver);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.builder().message(resp).timeStamp(LocalDateTime.now()).build());
		
	}
	
//	API end point to reject a friend request
	@DeleteMapping("/delete_request")
	public ResponseEntity<ApiResponse> deletRequest(@RequestBody UserDTO friends){
		
		int sender=friends.getSender();
		int receiver=friends.getReceiver();
		String message=userService.removeFriendRequest(sender,receiver );
		
		return ResponseEntity.ok(ApiResponse.builder().message(message).timeStamp(LocalDateTime.now()).build());
	}
	
//	API end point for removing a friend
	@DeleteMapping("/remove_friend")
	public ResponseEntity<ApiResponse> removeFriend(@RequestBody UserDTO friends){
		String resp=userService.removefriend(friends.getSender(), friends.getReceiver());
		return ResponseEntity.ok(ApiResponse.builder().message(resp).timeStamp(LocalDateTime.now()).build());
	}
	
//	API end point for sending all incoming friend requests as response
	@GetMapping("/requests/{id}")
	public ResponseEntity<Set<User>> getAllReceivedRequests(@PathVariable int id){
		
		Set<User> requests=userService.getFriendRequests(id);
		return ResponseEntity.status(HttpStatus.FOUND).body(requests);
	}
	
//	API end point for sending user's friend list
	@GetMapping("/getFriends/{id}")
	public ResponseEntity<Set<User>> getAllFirends(@PathVariable int id){
		
		Set<User> friends=userService.getFriends(id);
		return ResponseEntity.status(HttpStatus.FOUND).body(friends);
	}
	
//	API end point for sending user's sent requests as response
	@GetMapping("/sent_requests/{id}")
	public ResponseEntity<Set<User>> getAllSentRequests(@PathVariable int id){
		
		Set<User> friendRequestsSent=userService.sentfriendsRequests(id);
		return ResponseEntity.status(HttpStatus.FOUND).body(friendRequestsSent);
	}
	
//	API end point to get friend suggestions for a specific user
	@GetMapping("/suggestions/{id}")
	public List<User> getSuggestions(@PathVariable int id){
		
		
		return userService.friendSuggestions(id);
	}
}
