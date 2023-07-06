package com.sm.gateway.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sm.gateway.DTO.AuthResponse;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins="/**")
public class AuthController {

	private Logger logger= LoggerFactory.getLogger(AuthController.class);
	
	
	@GetMapping("/login")
	public ResponseEntity<AuthResponse> login(
			@RegisteredOAuth2AuthorizedClient("okta") OAuth2AuthorizedClient client,
			@AuthenticationPrincipal OidcUser user,
			Model model){
		
		logger.info("user email id"+user.getEmail());
		
		AuthResponse authresponse = new AuthResponse();
		
		// setting the userID in authresponse that we can get from OidcUser object
		authresponse.setUserId(user.getEmail());
		
		// setting the AccessToken value that we can get from OAuth2AuthorizedClient object
		authresponse.setAccessToken(client.getAccessToken().getTokenValue());
		
		// setting Refresh Token value that we can get from OAuth2AuthorizedClient object
		authresponse.setRefreshToken(client.getRefreshToken().getTokenValue());
		
		// setting Expires At value that we can get from OAuth2AuthorizedClient object
		authresponse.setExpireAt(client.getAccessToken().getExpiresAt().getEpochSecond());
		
		
		// getting the authorities from OidcUser object
		List<String> authorites=user.getAuthorities()
				.stream()
				.map(grantedAuthority-> grantedAuthority.getAuthority())
				.collect(Collectors.toList());
		
		// setting the authorities in APiResponse object
		authresponse.setAuthorities(authorites);
		
		return ResponseEntity.status(HttpStatus.OK).body(authresponse);
	}
}
