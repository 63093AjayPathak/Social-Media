package com.sm.profile_service.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.sm.profile_service.document.Post;

public interface PostService {

	public Post savePost(Post post, MultipartFile file);
	
	public String deletePost(long id);
	
	public String handleLike(long post_id, int user_id, String user_name);
	
	public List<Post> userProfile(int user_id, int requester_id);
	
	public List<Post> userFeed(int user_id);
	
	public String url();
}
