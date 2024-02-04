package com.sm.user_service.DTO;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sm.user_service.node.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDTO {

	
	private int sender;
	private int receiver;
	
	private int userId;
	private String userEmail;
	private String name;
	private String about;
	private Date dateOfBirth;
	private Date accountCreatedOn;
	private String country;
	private String city;
	private String hobbies;
	private int profilePicUrl;
	
	private String mainUrl;
	
	@JsonIgnore
	public User getUser() {
		return User.builder().email(userEmail).id(userId).name(name).about(about).dateOfBirth(dateOfBirth).accountCreatedOn(accountCreatedOn)
				.country(country).city(city).hobbies(hobbies).profilePicUrl(profilePicUrl)
				.build();
	}
	
}
