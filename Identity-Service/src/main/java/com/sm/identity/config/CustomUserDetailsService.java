package com.sm.identity.config;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.sm.identity.entity.AuthUser;
import com.sm.identity.repository.AuthUserRepository;
import com.sm.identity.exception_handler.UserNotFoundException;

@Component
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private AuthUserRepository userRepo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<AuthUser> user=userRepo.findByEmail(username);
		
		return user.map(CustomUserDetails::new).orElseThrow(() -> new UserNotFoundException("No user found with email: " + username));
	}

}
