package com.sm.user_service.node;

import java.util.Set;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Relationship.Direction;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Node
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
	
	@Id
	private int id;
	
	private String email;
	
	
//	for relationship with friends
	@JsonIgnore
	@Relationship(type = "FRIENDS", direction=Direction.OUTGOING)
	private Set<User> friends;
	
	
//	for received requests
	@JsonIgnore
	@Relationship(value = "REQUEST_RECEIVED",direction =Direction.INCOMING)
	private Set<User> requests;
	
	
//	for requests send
	@JsonIgnore
	@Relationship(value="REQUEST_SEND", direction=Direction.OUTGOING)
	private Set<User> requestSend;


//	based on id
	@Override
	public boolean equals(Object obj) {
		if(obj==null)
			return false;
		
		User user = (User)obj;
		
		if(user.getId()==(this.getId())) {
			return true;
		}
		else {
			return false;
		}
	}

	public int hashCode() {	
		return this.getId();
	}
}
