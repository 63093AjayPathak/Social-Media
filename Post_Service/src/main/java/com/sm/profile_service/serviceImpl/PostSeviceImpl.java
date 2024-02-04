package com.sm.profile_service.serviceImpl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.sm.profile_service.document.Like;
import com.sm.profile_service.document.Post;
import com.sm.profile_service.exceptions.ContentNotFoundException;
import com.sm.profile_service.repository.PostRepository;
import com.sm.profile_service.service.PostService;
import com.sm.profile_service.service.S3Service;

@Service
public class PostSeviceImpl implements PostService {
	
	@Autowired
	private S3Service s3Service;

	@Autowired
	private PostRepository postRepo;
	
	@Autowired
	private RestTemplate restTemplate;
	
	public String url() {
		return s3Service.getUrl();
	}
	
	private Set<Integer> getFriends(int user_id){
//		call user-service and getFriends of current user which we have to convert to a Set<Integer> and then return it
		Set<LinkedHashMap> friends=restTemplate.getForObject("http://User-Service/user/getFriends/"+user_id, Set.class);
		Set<Integer> result=new HashSet<>();
		friends.stream().forEach((LinkedHashMap u)->result.add((Integer)u.get("id")));
			
		return result;
	}
	
//	check whether two people are friends or not
	private boolean isFriend(int user_id, int requester_id) {
	    Set<Integer> friends= this.getFriends(user_id);
	    if(friends.contains(requester_id))
	    	return true;
	    
	    return false;
	}
	
	@Override
	public Post savePost(Post post,MultipartFile file) {
//		call S3Service method to save media to S3
		String id="Post";
		String fileName="";
		if(file!=null) {
			fileName=s3Service.saveFile(file, post.getUserId());
		}
		
		if(fileName.length()==0)
			id+=Timestamp.valueOf(LocalDateTime.now()).toString();
		else {
			post.setUrl(fileName.hashCode());
			id+=fileName;
		}
			
		post.setId(id.hashCode());
		post.setPostedOn(LocalDateTime.now());
		return postRepo.save(post);
	}

	@Override
	public String deletePost(long id) {
		Post post=postRepo.findById(id).orElseThrow(()-> new ContentNotFoundException("No Post found with given id"));
		s3Service.deleteFile("Post/"+post.getUserId()+"/"+post.getUrl());
		postRepo.deleteById(id);
		return "Post Deleted";
	}

	@Override
	public String handleLike(long post_id, int user_id, String user_name) {
		
		Post post=postRepo.findById(post_id).orElseThrow(()-> new ContentNotFoundException("No Post found with id: "+post_id));
		
		
//		if user who's liking/disliking the post is a friend of postowner then only perform liking/disliking
		if(this.isFriend(post.getUserId(),user_id)) {
			Set<Like> likes=post.getLikes();
			Like like=Like.builder().postId(post_id).userId(user_id).userName(user_name).likedAt(LocalDateTime.now()).build();
			if(likes.contains(like)) {
				likes.remove(like);
			}
			else {
				
				likes.add(like);
			}
			post.setLikes(likes);
			postRepo.save(post);
		}
			
		
		return "Like handled";
	}

	@Override
	public List<Post> userProfile(int user_id, int requester_id) {
		
		List<Post> posts= postRepo.findByUserId(user_id);
		
//		1. user requests his/her own profile
		if(user_id == requester_id)
			return posts;
		
//		2. user requests one of the friends' profile
		if(this.isFriend(user_id,requester_id))
			return posts;
		else  //		3. user requests profile or non-friend user.
		    return posts.stream().filter((p)-> p.isPublic()).collect(Collectors.toList());
	}

	@Override
	public List<Post> userFeed(int user_id) {
		// TODO Auto-generated method stub
		Set<Integer> friends=this.getFriends(user_id);
		
		List<Post> feed=new ArrayList<>();
		for(int i:friends) {
			feed.addAll(postRepo.findByUserId(i));
		}
		
//		deal with the situation when user has no friends+ when user feed ends and user keeps on requesting more feed
//		a way to deal with this is show a box containing  message "add new friends to see more" and clicking on that box
//		will take to suggestions page
		return feed;
	}

}
