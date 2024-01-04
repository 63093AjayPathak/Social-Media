package com.sm.profile_service.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sm.profile_service.document.Post;

public interface PostRepository extends MongoRepository<Post, Long> {

	public List<Post> findByUserId(int user_id);
}
