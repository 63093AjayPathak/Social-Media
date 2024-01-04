package com.sm.user_service.service;

import java.util.List;
import java.util.Set;

import com.sm.user_service.DTO.FriendListDTO;
import com.sm.user_service.DTO.UserDTO;
import com.sm.user_service.DTO.UserFriendStatusDTO;
import com.sm.user_service.node.User;

public interface UserService {

//	method to create a new user
	public String newUser(UserDTO user);
	
//	method to get friendship info
	public UserFriendStatusDTO getFriendshipstatus(int user_id, int requested_id);
	
//	method to get user's friends
	public Set<FriendListDTO> getFriends(int id);
	
//	method to get user friend requests ( incoming)
	public Set<FriendListDTO> getFriendRequests(int id);
	
//	method to get user friend requests ( outgoing )
	public Set<FriendListDTO> sentfriendsRequests(int id);
	
//	method to send a friend request
	public String sendFriendRequest(int sender, int receiver);
	
//	method to accept friend request
	public String acceptRequest(int accepter, int reqSender);
	
//	method to reject a friend request
	public String removeFriendRequest(int rejecter, int reqSender);
	
//	method to remove a friend from friend list
	public String removefriend(int remover, int beingRemoved);
	
//	method to get friend suggestions for user
	public List<FriendListDTO> friendSuggestions(int id);
	
//	testing methods for postman API
	public List<User> findAllUsers();
	
	public User getSpecificUser(int id);
	
	public String editInfo(UserDTO info);
	
	public List<FriendListDTO> searchByName(String name);
	
//	add a method to update profile pic and call post-service API to create a new post for updation of profile picture
}
