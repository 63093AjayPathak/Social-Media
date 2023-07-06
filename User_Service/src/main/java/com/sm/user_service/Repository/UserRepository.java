package com.sm.user_service.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.google.common.base.Optional;
import com.sm.user_service.Entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	// method to save a new user ( signup )
	
	
	// method to get details of existing user via email password ( signin ) , keep in mind the deactivation wala funda
	Optional<User> findByEmailAndPassword(String name, String password);
	
	//method to deactivate existing user
}
