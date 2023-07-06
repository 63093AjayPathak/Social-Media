package com.sm.user_service.Service;

import java.util.List;

import com.sm.user_service.DTO.UserDTO;
import com.sm.user_service.Entity.User;

public interface IUserService {

	// method to get all users
	public List<User> allUsers();
	
	// method to create new user
	public User signUp(UserDTO user);
	
	// method for existing user validation
	public User signIn(UserDTO user);
	
	
	// method for editing user details
}
