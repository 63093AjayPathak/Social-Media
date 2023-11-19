package com.sm.user_service.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(length=40, nullable=false)
	@NotBlank(message="Name must be supplied")
	private String name;
	
	
	@Column(length=15)
	private String mobile;
	
	@Column(unique =true, length=40)
	@NotBlank(message="Email cannot be blank")
	@Email(message="Invalid email format")
	private String email;
	
	@Column(length=250, nullable=false)
	@NotBlank(message="Password must be supplied")
	private String password;
	
}

