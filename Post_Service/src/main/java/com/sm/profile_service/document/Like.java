package com.sm.profile_service.document;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//we have to maintain relationship between post and like
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Like {

	private long postId;
	private int userId;
	private String userName;
	private LocalDateTime likedAt;
	
	public boolean equals(Object o) {
		Like like=(Like)o;
		
		if(like==null)
			return false;
		if(like.hashCode()==this.hashCode())
			return true;
		else
			return false;
	}
	
	public int hashCode() {
		return (""+userId+postId).hashCode();
	}
}
