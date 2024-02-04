package com.sm.user_service.serviceImpl;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.sm.user_service.DTO.FriendListDTO;
import com.sm.user_service.DTO.UserDTO;
import com.sm.user_service.DTO.UserFriendStatusDTO;
import com.sm.user_service.exceptions.NoUserFound;
import com.sm.user_service.node.User;
import com.sm.user_service.repository.UserRepository;
import com.sm.user_service.service.S3Service;
import com.sm.user_service.service.UserService;


@Service
@Transactional()
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private S3Service s3Service;
	
	@Autowired
	RestTemplate restTemplate;

	@Override
	public String newUser(UserDTO userdto) {
		User user =userdto.getUser();
		user.setAccountCreatedOn(new Date(System.currentTimeMillis()));
		userRepo.save(user);
		return "New User Node Created";
	}
	
	public UserFriendStatusDTO getFriendshipstatus(int user_id, int requested_id) {
//		if user requesting it's own profile
		if(user_id==requested_id)
			return UserFriendStatusDTO.builder().self(true).build();
		
//		if user requesting profile of someone who has sent user a friend request
		Set<Integer> info=getFriendRequests(user_id).stream().map((u)->u.getId()).collect(Collectors.toSet());
		if(info.contains(requested_id))
			return UserFriendStatusDTO.builder().isRequestReceived(true).build();
		
//		if user requests profile of a friend
		info=getFriends(user_id).stream().map((u)->u.getId()).collect(Collectors.toSet());
		if(info.contains(requested_id))
			return UserFriendStatusDTO.builder().isFriend(true).build();
		
//		if user requests profile of someone he/she sent a friend request to
		info=sentfriendsRequests(user_id).stream().map((u)->u.getId()).collect(Collectors.toSet());
		if(info.contains(requested_id))
			return UserFriendStatusDTO.builder().isRequestSent(true).build();
		
		return new UserFriendStatusDTO();
	}

	@Override
	public Set<FriendListDTO> getFriends(int id) {
		
		User user = userRepo.findById(id).orElseThrow(() -> new NoUserFound("No user with email id: " + id));
		Set<User> friends=user.getFriends();
		Set<FriendListDTO> f= friends.stream().map((User u)->{
			return FriendListDTO.builder().id(u.getId()).user_name(u.getName()).build();
	}).collect(Collectors.toSet());
		return f;
	}

	@Override
	public Set<FriendListDTO> getFriendRequests(int id) {
		User user = userRepo.findById(id).orElseThrow(() -> new NoUserFound("No user with email id: " + id));
		Set<User> friend_requests=user.getRequests();
		Set<FriendListDTO> f= friend_requests.stream().map((User u)->{
			return FriendListDTO.builder().id(u.getId()).user_name(u.getName()).build();
	        }).collect(Collectors.toSet());
		
		return f;
	}

	@Override
	public Set<FriendListDTO> sentfriendsRequests(int id) {
		User user = userRepo.findById(id).orElseThrow(() -> new NoUserFound("No user with email id: " + id));
		Set<User> sent_friends_requests=user.getRequestSend();
		Set<FriendListDTO> f= sent_friends_requests.stream().map((User u)->{
			return FriendListDTO.builder().id(u.getId()).user_name(u.getName()).build();
	        }).collect(Collectors.toSet());
		return f;
	}

	@Override
	public String sendFriendRequest(int sender, int receiver) {
		User send = userRepo.findById(sender).orElseThrow(() -> new NoUserFound("Sender not found"));
		User receive = userRepo.findById(receiver).orElseThrow(() -> new NoUserFound("Sender not found"));
		String resp = "Request sent";
		
		if(send.getRequests().contains(receive))
			return "Request already received from user "+receive.getName()+" with email: "+receive.getEmail();

		if (!send.getRequestSend().contains(receive)) {
			send.getRequestSend().add(receive);
			receive.getRequests().add(send);

			userRepo.save(send);
			userRepo.save(receive);
		} else {
			resp = "Request already sent";
		}

		return resp;
	}
//Approach
//create a separate private method for addingFriend( which willdo the task that acceptRequest method is doing now) and call
//	this method along with removeFriendRequest method in the body of acceptRequest method (for reason check controller)
	@Override
	public String acceptRequest(int accepter, int reqSender) {

		String reply = "friend added";

		User sender = userRepo.findById(accepter).orElseThrow(() -> new NoUserFound("Sender not found"));
		User receiver = userRepo.findById(reqSender).orElseThrow(() -> new NoUserFound("Receiver not found"));
		
		if(!sender.getRequests().contains(receiver))
			return "No Request received from "+receiver.getName()+" with email: "+receiver.getEmail();

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
		
		User sender = userRepo.findById(rejecter).orElseThrow(() -> new NoUserFound("Rejecter not found"));
		User receiver = userRepo.findById(reqSender).orElseThrow(() -> new NoUserFound("reqSender not found"));
		
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
// firstly we will check if we can get at least 10 suggestions based on user's friends, if not then we will search by user's city
//	then by user's country and even then if we can't get 10 suggestions we will add top 10 user's from db to suggestions collection
	@Override
	public List<FriendListDTO> friendSuggestions(int id) {
		User user = userRepo.findById(id).orElseThrow(() -> new NoUserFound("No User found"));
		Set<User> friends = user.getFriends();
		Set<User> suggestions = new HashSet<>();
		
		if(suggestions.size()<10) {
			List<User> users=userRepo.findByCityAndCountry(user.getCity(),user.getCountry());
			suggestions.addAll(users);
		}
		
		if(suggestions.size()<10) {
			List<User> users=userRepo.findByCountry(user.getCountry());
			suggestions.addAll(users);
		}
		
//		removing common friends
		List<User> allSuggestions= suggestions.stream().filter((u) -> !(friends.contains(u) || u.getEmail().equals(user.getEmail())) )
				.collect(Collectors.toList());
		System.out.println(allSuggestions);
//		converting to suitable format
		List<FriendListDTO> f= allSuggestions.stream().map((User u)->{
			return FriendListDTO.builder().id(u.getId()).user_name(u.getName()).build();
	        }).collect(Collectors.toList());
		
		return f;
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
	
//	client side validation also required
	public String editInfo(UserDTO info) {
		User user=userRepo.findById(info.getUserId()).
				orElseThrow(()-> new NoUserFound("User with given id couldn't be found"));
		
		if(info.getAbout()!=null)
			user.setAbout(info.getAbout());
		if(info.getCity()!=null)
			user.setCity(info.getCity());
		if(info.getCountry()!=null)
			user.setCountry(info.getCountry());
		if(info.getName()!=null)
			user.setName(info.getName());
		if(info.getHobbies()!=null)
			user.setHobbies(info.getHobbies());
		  userRepo.save(user);
		  
		return "User info updated";
	}
	
	public List<FriendListDTO> searchByName(String name){
		
		List<User> list=userRepo.findByName(name);
		
		return list.stream().
				map((u)-> FriendListDTO.builder().id(u.getId()).user_name(u.getName()).build())
				.collect(Collectors.toList());
	}
	
	public String updateDisplayPicture(MultipartFile file, int user_id) {
		User user=userRepo.findById(user_id).orElseThrow(()-> new NoUserFound(" User with gievn id couldn't be found"));
		
		if(user.getProfilePicUrl()!=0)
			s3Service.deleteFile("Profile/"+user.getId()+"/"+user.getProfilePicUrl());
		
		if(file!=null) {
			String fileName=s3Service.saveFile(file, user_id);
			user.setProfilePicUrl(fileName.hashCode());
		}
		else {
			user.setProfilePicUrl(0);
		}
		userRepo.save(user);
		
		return "Profile Pic updated";
	}
	
	public String getProfleURL() {
		return s3Service.getUrl();
	}

}
