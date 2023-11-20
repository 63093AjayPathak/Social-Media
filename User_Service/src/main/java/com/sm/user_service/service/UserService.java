package com.sm.user_service.service;

import java.util.List;
import java.util.Set;

import com.sm.user_service.DTO.UserDTO;
import com.sm.user_service.node.User;

public interface UserService {

//	method to create a new user
	public String newUser(UserDTO user);
	
//	method to get user's friends
	public Set<User> getFriends(int id);
	
//	method to get user friend requests ( incoming)
	public Set<User> getFriendRequests(int id);
	
//	method to get user friend requests ( outgoing )
	public Set<User> sentfriendsRequests(int id);
	
//	method to send a friend request
	public String sendFriendRequest(int sender, int receiver);
	
//	method to accept friend request
	public String acceptRequest(int accepter, int reqSender);
	
//	method to reject a friend request
	public String removeFriendRequest(int rejecter, int reqSender);
	
//	method to remove a friend from friend list
	public String removefriend(int remover, int beingRemoved);
	
//	method to get friend suggestions for user
	public List<User> friendSuggestions(int id);
	
//	testing methods for postman API
	public List<User> findAllUsers();
	
	public User getSpecificUser(int id);
}
