package com.sm.profile_service.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
import org.springframework.web.multipart.MultipartFile;

import com.sm.profile_service.document.Post;
import com.sm.profile_service.dto.LikeRequest;
import com.sm.profile_service.dto.PostResponse;
import com.sm.profile_service.service.PostService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@RestController
@RequestMapping("/")
public class ProfileController {

	@Autowired
	private PostService postService;
	
	private Logger logger = LoggerFactory.getLogger(ProfileController.class);
	

	@GetMapping("/")
	public ResponseEntity<?> hello() {
		String message = "All ok";
		return ResponseEntity.ok(message);
	}

	@PostMapping("/savePost")
	public ResponseEntity<String> savePost(@RequestParam("file") MultipartFile file, @RequestParam("userId") int id, 
			@RequestParam("isPublic") boolean isPublic, @RequestParam("caption") String caption) {
		
		Post post= new Post();
		post.setCaption(caption);
		post.setUserId(id);
		post.setPublic(isPublic);
		
		postService.savePost(post,file);
		
		String message="Post created";
		
		return ResponseEntity.status(HttpStatus.CREATED).body(message);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deletePost(@PathVariable long id) {
		return ResponseEntity.status(HttpStatus.OK).body(postService.deletePost(id));
	}

	@PostMapping("/like")
	@CircuitBreaker(name="UserCircuitBreaker", fallbackMethod = "likeDisLikeFallBack")
	public ResponseEntity<String> likeOrDisLike(@RequestBody LikeRequest likeRequest, HttpServletRequest req) {
		String token = req.getHeader(HttpHeaders.AUTHORIZATION);
		String message = postService.handleLike(likeRequest.getPostId(), likeRequest.getUserId(),
				likeRequest.getUserName(), req.getHeader("X-Corelation-ID"), token);

		return ResponseEntity.status(HttpStatus.CREATED).body(message);
	}
	
//	fallback method for '/like' API
	public ResponseEntity<String> likeDisLikeFallBack(LikeRequest likeRequest, HttpServletRequest req, Exception ex){
		logger.info("{}", "Req with Correlation Id: "+req.getHeader("X-Corelation-ID")+" failed due to user service being down");
		return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).body("Please try again later, Exception: "+ex.getMessage());
	}

//	sort this data ( on the basis of postedOn datetime) in frontend
	@GetMapping("/user_profile")
	@CircuitBreaker(name="UserCircuitBreaker", fallbackMethod = "userProfileFallBack")
	public ResponseEntity<PostResponse> getUserprofile(@RequestParam int user_id, @RequestParam int requester_id, HttpServletRequest req) {
		String token = req.getHeader(HttpHeaders.AUTHORIZATION);
		PostResponse postResp= new PostResponse();
		postResp.getPosts().addAll(postService.userProfile(user_id, requester_id, req.getHeader("X-Corelation-ID"), token));
		postResp.setUrl(postService.url()+user_id+"/");
		return ResponseEntity.status(HttpStatus.OK).body(postResp);
	}
	
	public ResponseEntity<PostResponse> userProfileFallBack(int user_id, int requester_id, HttpServletRequest req, Exception ex){
		logger.info("{}", "Req with Correlation Id: "+req.getHeader("X-Corelation-ID")+" failed due to user service being down, Exception: "+ex.getMessage());
		PostResponse resp= new PostResponse();
		return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).body(resp);
		
	}

	
//	sort this data ( on the basis of postedOn datetime )in frontend
	@GetMapping("/user_feed")
	@CircuitBreaker(name="UserCircuitBreaker", fallbackMethod = "userFeedFallBack")
	public ResponseEntity<PostResponse> getUserFeed(@RequestParam int user_id, HttpServletRequest req) { // take HttpServletRequest as method parameter
		String token = req.getHeader(HttpHeaders.AUTHORIZATION);
		PostResponse postResp= new PostResponse();
		postResp.getPosts().addAll(postService.userFeed(user_id, req.getHeader("X-Corelation-ID"), token));
		postResp.setUrl(postService.url());
		return ResponseEntity.status(HttpStatus.OK).body(postResp);
	}
	
	public ResponseEntity<PostResponse> userFeedFallBack(int user_id, HttpServletRequest req, Exception ex){
		logger.info("{}", "Req with Correlation Id: "+req.getHeader("X-Corelation-ID")+" failed due to user service being down, Exception: "+ex.getMessage());
		PostResponse resp= new PostResponse();
		return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).body(resp);
	}

}
