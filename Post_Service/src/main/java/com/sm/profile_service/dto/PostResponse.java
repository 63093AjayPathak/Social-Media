package com.sm.profile_service.dto;

import java.util.ArrayList;
import java.util.List;

import com.sm.profile_service.document.Post;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostResponse {

	private List<Post> posts;
	
	private String url;
	
	public PostResponse() {
		super();
		this.posts= new ArrayList<>();
	}
}
