package com.sm.profile_service.document;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "post_collection")
@Getter
@Setter
@Builder
public class Post {

	@Id
	private long id;
	private int userId;
	private LocalDateTime postedOn;
	
	@JsonProperty
	private boolean isPublic;
	
	private String caption;
	
	private int url;
	
	@Field("likes")
	private Set<Like> likes;
	
	public Post() {
		super();
		this.likes=new HashSet<>();
	}
	
	public Post(long id, int userId, LocalDateTime postedOn, boolean isPublic, String caption,int url,
			Set<Like> likes) {
		super();
		this.id = id;
		this.userId = userId;
		this.postedOn = postedOn;
		this.isPublic = isPublic;
		this.caption = caption;
		this.url=url;
		
		if(likes!=null)
		this.likes = likes;
		else
			this.likes=new HashSet<>();
	}
	
	public void setLikes(Set<Like> likes) {
		if(likes==null)
			this.likes=new HashSet<>();
		else
			this.likes=likes;
	}

	public boolean equals(Object o) {
		Post post = (Post) o;

		if (post == null)
			return false;

		if (post.id == this.id)
			return true;
		else
			return false;
	}

	public int hashCode() {
		return ("" + id).hashCode();
	}

	

}
