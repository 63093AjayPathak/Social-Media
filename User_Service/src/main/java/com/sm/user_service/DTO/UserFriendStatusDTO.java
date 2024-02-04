package com.sm.user_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFriendStatusDTO {

	private boolean isFriend;
	private boolean isRequestSent;
	private boolean isRequestReceived;
	private boolean self;
	private UserDTO user;
}
