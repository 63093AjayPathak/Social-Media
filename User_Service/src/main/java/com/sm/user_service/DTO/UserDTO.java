package com.sm.user_service.DTO;

import com.sm.user_service.Entity.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

	
	private String name;
	private String password;
	private String email;
	private String phoneNumber;
	
	public User converToUserEntity () {
		
		User user = new User();
		
		user.setName(this.name);
		user.setMobile(this.phoneNumber);
		user.setEmail(this.email);
		user.setPassword(this.password);
		
		return user;
		
	}
	
}
