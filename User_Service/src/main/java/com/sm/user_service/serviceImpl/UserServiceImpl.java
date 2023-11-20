package com.sm.user_service.serviceImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sm.user_service.DTO.UserDTO;
import com.sm.user_service.exceptions.NoUserFound;
import com.sm.user_service.node.User;
import com.sm.user_service.repository.UserRepository;
import com.sm.user_service.service.UserService;


@Service
@Transactional
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRepository userRepo;

	@Override
	public String newUser(UserDTO userdto) {
		
		User user = userRepo.save(userdto.getUser());
		return "New User Node Created";
	}

	@Override
	public Set<User> getFriends(int id) {
		
		User user = userRepo.findById(id).orElseThrow(() -> new NoUserFound("No user with email id: " + id));
		return user.getFriends();
	}

	@Override
	public Set<User> getFriendRequests(int id) {
		User user = userRepo.findById(id).orElseThrow(() -> new NoUserFound("No user with email id: " + id));
		return user.getRequests();
	}

	@Override
	public Set<User> sentfriendsRequests(int id) {
		User user = userRepo.findById(id).orElseThrow(() -> new NoUserFound("No user with email id: " + id));
		return user.getRequestSend();
	}

	@Override
	public String sendFriendRequest(int sender, int receiver) {
		User send = userRepo.findById(sender).orElseThrow(() -> new NoUserFound("Sender not found"));
		User receive = userRepo.findById(receiver).orElseThrow(() -> new NoUserFound("Sender not found"));
		String resp = "Request sent";

		if (!send.getRequestSend().contains(receive)) {
			send.getRequestSend().add(receive);
			receive.getRequests().add(send);

			userRepo.save(send);
			userRepo.save(receive);
		} else {
			resp = "Request already sent";
		}
//		send = userRepo.findById(sender).orElseThrow(() -> new RuntimeException("Sender not found"));
//		receive = userRepo.findById(receiver).orElseThrow(() -> new RuntimeException("Sender not found"));
//
//		for (User user : send.getRequestSend()) {
//			System.out.println(user.getEmail());
//		}
//
//		for (User user : receive.getRequests()) {
//			System.out.println(user.getEmail());
//		}

		return resp;
	}

	@Override
	public String acceptRequest(int accepter, int reqSender) {

		String reply = "friend added";

		User sender = userRepo.findById(accepter).orElseThrow(() -> new NoUserFound("Sender not found"));
		User receiver = userRepo.findById(reqSender).orElseThrow(() -> new NoUserFound("Sender not found"));

		if (!sender.getFriends().contains(receiver)) {

			sender.getFriends().add(receiver);
			receiver.getFriends().add(sender);
			userRepo.save(sender);
			userRepo.save(receiver);

		} else {
			reply = "Already friends";
		}

		return reply;
	}

	@Override
	public String removeFriendRequest(int rejecter, int reqSender) {
		
		String response="Request removed";
		
		User sender = userRepo.findById(rejecter).orElseThrow(() -> new NoUserFound("Sender not found"));
		User receiver = userRepo.findById(reqSender).orElseThrow(() -> new NoUserFound("Sender not found"));
		
			userRepo.deleteRequestRelatiotnship(receiver.getEmail(), sender.getEmail());
			userRepo.deleteRequestSendRelationship(receiver.getEmail(), sender.getEmail());
		
		
		return response;
	}

	@Override
	public String removefriend(int remover, int beingRemoved) {
		String reply = "Removed from friends";

		User sender = userRepo.findById(remover).orElseThrow(() -> new NoUserFound("Sender not found"));
		User receiver = userRepo.findById(beingRemoved).orElseThrow(() -> new NoUserFound("Sender not found"));

		if (sender.getFriends().contains(receiver)) {
			userRepo.deleteFriendshipRelationship(sender.getEmail(), receiver.getEmail());
			userRepo.deleteFriendshipRelationship(receiver.getEmail(), sender.getEmail());
		} else {
			reply = "you cannot unfriend someone you aren't friends with";
		}

		return reply;
	}

	@Override
	public List<User> friendSuggestions(int id) {
		User user = userRepo.findById(id).orElseThrow(() -> new NoUserFound("No User found"));
		Set<User> friends = user.getFriends();
		Set<User> suggestions = new HashSet<>();
		for (User u : friends) {
			suggestions.addAll(u.getFriends());
			System.out.println(u.getEmail());
		}

		return suggestions.stream().filter((u) -> !(friends.contains(u) || u.getEmail() == user.getEmail()))
				.collect(Collectors.toList());
	}

	@Override
	public List<User> findAllUsers() {
		// TODO Auto-generated method stub
		return userRepo.findAll();
	}

	@Override
	public User getSpecificUser(int id) {
		// TODO Auto-generated method stub
		return  userRepo.findById(id).orElseThrow(() -> new NoUserFound("No User found"));
	}

}
