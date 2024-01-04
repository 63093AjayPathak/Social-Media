package com.sm.profile_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sm.profile_service.document.Post;
import com.sm.profile_service.dto.LikeRequest;
import com.sm.profile_service.service.PostService;

@RestController
@RequestMapping("/")
public class ProfileController {

	@Autowired
	private PostService postService;

	@GetMapping("/")
	public ResponseEntity<?> hello() {
		String message = "All ok";
		return ResponseEntity.ok(message);
	}

	@PostMapping("/savePost")
	public ResponseEntity<?> savePost(@RequestBody Post post) {
		return ResponseEntity.status(HttpStatus.CREATED).body(postService.savePost(post));
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deletePost(@PathVariable long id) {
		return ResponseEntity.status(HttpStatus.OK).body(postService.deletePost(id));
	}

	@PostMapping("/like")
	public ResponseEntity<?> likeOrDisLike(@RequestBody LikeRequest likeRequest) {
		String message = postService.handleLike(likeRequest.getPostId(), likeRequest.getUserId(),
				likeRequest.getUserName());

		return ResponseEntity.status(HttpStatus.CREATED).body(message);
	}

//	sort this data ( on the basis of postedOn datetime) in frontend
	@GetMapping("/user_profile")
	public ResponseEntity<List<Post>> getUserprofile(@RequestParam int user_id, @RequestParam int requester_id) {
		List<Post> profile = postService.userProfile(user_id, requester_id);
		return ResponseEntity.status(HttpStatus.OK).body(profile);
	}

	
//	sort this data ( on the basis of postedOn datetime) in frontend
	@GetMapping("/user_feed")
	public ResponseEntity<List<Post>> getUserFeed(@RequestParam int user_id) {
		List<Post> feed = postService.userFeed(user_id);
		return ResponseEntity.status(HttpStatus.OK).body(feed);
	}

}
