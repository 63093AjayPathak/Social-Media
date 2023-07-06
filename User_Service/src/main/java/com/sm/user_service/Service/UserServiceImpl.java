package com.sm.user_service.Service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Optional;
import com.sm.user_service.DTO.UserDTO;
import com.sm.user_service.Entity.User;
import com.sm.user_service.Repository.UserRepository;


@Service
@Transactional
public class UserServiceImpl implements IUserService {
	
	@Autowired
	UserRepository userRepo;

	@Override
	public User signUp(UserDTO user) {
		
		return userRepo.save(user.converToUserEntity());
		 
	}

	@Override
	public User signIn(UserDTO user) {
		
		Optional<User> op=userRepo.findByEmailAndPassword(user.getEmail(), user.getPassword());
		
		if(op.isPresent()) {
			return op.get();
		}
		return null;
	}

	@Override
	public List<User> allUsers() {

		return userRepo.findAll();
	}

}
