package com.sm.user_service.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sm.user_service.DTO.ApiResponse;
import com.sm.user_service.DTO.FriendListDTO;
import com.sm.user_service.DTO.UserDTO;
import com.sm.user_service.DTO.UserFriendStatusDTO;
import com.sm.user_service.node.User;
import com.sm.user_service.service.UserService;

@RestController
@RequestMapping("/")
public class UserController {
	
	
	@Autowired
	private UserService userService;

//	create new user node
	@PostMapping("/")
	public ResponseEntity<String> createnewUser(@RequestBody UserDTO userDto){
		String message=userService.newUser(userDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(message);
	}
	
//	below method is just for testing via postman
	@GetMapping("/")
	public List<User> getAllUsers(){
		return userService.findAllUsers();
	}
	
	
//	from here we are sending a DTO which has fields like isFriend, isRequestSent, isRequestReceived, self 
//	on the basis of these fields we will show the appropriate option on UI
//	to send friend request, to cancel already sent req, to cancel incoming request, option to like a post
//	if user is viewing his/her own profile then no such option is to be shown 
//	but some other functionality is to be provided like editing post, deleting post, adding new post and so on
//	where user interacts with it's own profile
	@GetMapping("/user_details")
	public ResponseEntity<UserFriendStatusDTO> getFriendshipDetails(@RequestParam int user_id, @RequestParam int requested_id){
		User user= userService.getSpecificUser(requested_id);
		
		UserDTO dto=UserDTO.builder().name(user.getName()).about(user.getAbout()).dateOfBirth(user.getDateOfBirth())
				.accountCreatedOn(user.getAccountCreatedOn()).userEmail(user.getEmail()).city(user.getCity())
				.country(user.getCountry()).hobbies(user.getHobbies()).profilePicUrl(user.getProfilePicUrl())
				.mainUrl(userService.getProfleURL()+requested_id+"/").build();
		
		UserFriendStatusDTO friend=userService.getFriendshipstatus(user_id, requested_id);
		friend.setUser(dto);
		return ResponseEntity.status(HttpStatus.OK).body(friend);
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
		
//		process of removing the REQUEST_SENT and REQUEST_RECEIVED , creating the relationship FRIENDS need to be done
//		in same transaction but here we are doing them in separate transaction
		String resp= userService.acceptRequest(sender,receiver);
		userService.removeFriendRequest(sender,receiver );
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.builder().message(resp).timeStamp(LocalDateTime.now()).build());
		
	}
	
//	API end point to reject a friend request
//	1 use case when user who has received the request cancels it( user = sender, other user=receiver)
//	2 use case when user who sent the request wants to delete it (user=receiver, other user=sender
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
	public ResponseEntity<Set<FriendListDTO>> getAllReceivedRequests(@PathVariable int id){
		Set<FriendListDTO> requests=userService.getFriendRequests(id);
		return ResponseEntity.status(HttpStatus.FOUND).body(requests);
	}
	
//	API end point for sending user's friend list
//	return the list of dto containing essential details only like id 
	@GetMapping("/getFriends/{id}")
	public ResponseEntity<Set<FriendListDTO>> getAllFirends(@PathVariable int id){
		Set<FriendListDTO> friends=userService.getFriends(id);
		return ResponseEntity.status(HttpStatus.OK).body(friends);
	}
	
//	API end point for sending user's sent requests as response
	@GetMapping("/sent_requests/{id}")
	public ResponseEntity<Set<FriendListDTO>> getAllSentRequests(@PathVariable int id){
		Set<FriendListDTO> friendRequestsSent=userService.sentfriendsRequests(id);
		return ResponseEntity.status(HttpStatus.FOUND).body(friendRequestsSent);
	}
	
//	API end point to get friend suggestions for a specific
	@GetMapping("/suggestions/{id}")
	public List<FriendListDTO> getSuggestions(@PathVariable int id){
		return userService.friendSuggestions(id);
	}
	
//	method to change profile picture
//	this method would be used to update the profile picture both when user tries to change profile picture as well as when he/she
//	tries to remove the profile picture
//	whenever we call this API we have to call Post-Service API to create a post from frotnend itself
	@PatchMapping("/update_profile_picture")
	public ResponseEntity<String> updateProfilePicture(@RequestParam MultipartFile file, @RequestParam int user_id) {
		String message= userService.updateDisplayPicture(file, user_id);
		return ResponseEntity.status(HttpStatus.CREATED).body(message);
	}

//	add an API for editing user info
	@PatchMapping("/update_info")
	public ResponseEntity<String> updateInfo(@RequestBody UserDTO info){
		String message=userService.editInfo(info);
		return ResponseEntity.status(HttpStatus.OK).body(message);
	}
	
//	add an api to search user by name
	@GetMapping("/search")
	public ResponseEntity<List<FriendListDTO>> searchByName(@RequestParam String name){
		return ResponseEntity.status(HttpStatus.OK).body(userService.searchByName(name.trim()));
	}
}
