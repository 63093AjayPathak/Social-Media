package com.sm.user_service.DTO;

import com.sm.user_service.node.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

	
	private int sender;
	private int receiver;
	
	private int userId;
	private String userEmail;
	
	public User getUser() {
		return User.builder().email(userEmail).id(userId).build();
	}
	
}
