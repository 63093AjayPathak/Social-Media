package com.sm.identity.service;

import java.util.Date;

import com.sm.identity.dto.AuthRequest;
import com.sm.identity.entity.AuthUser;

public interface AuthService {

	public String saveUser(AuthRequest user, String gateway);
	
	public AuthUser findUserByEmail(String email);
	
	public String generateToken(String email, Date date);
	
	public String sendEmail(String to, String subject, String mailBody);
	
	public String authorizeUser(String email);
	
	public String changePassword(String password, String token);
	
	public String validateToken(String token);
}
