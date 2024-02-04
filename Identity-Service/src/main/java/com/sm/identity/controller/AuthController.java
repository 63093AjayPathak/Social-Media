package com.sm.identity.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sm.identity.dto.AuthRequest;
import com.sm.identity.entity.AuthUser;
import com.sm.identity.service.AuthService;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {
	
	@Value("${gateway_url}")
	private String gateway;
	
	@Autowired
	private AuthService authService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@PostMapping("/register")
	public ResponseEntity<String> addNewUSer(@RequestBody AuthRequest user) {

		authService.saveUser(user, gateway);
		
		return ResponseEntity.status(HttpStatus.CREATED).body("Link to activate accout has been sent to "+user.getEmail());
	}
	
	@PostMapping("/token")
	public ResponseEntity<String> getToken(@RequestBody AuthRequest authRequest) {
		
		Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(),authRequest.getPassword()));
		
		if(authenticate.isAuthenticated()) {
			if(authService.findUserByEmail(authRequest.getEmail()).isAuthorized())
			    return ResponseEntity.status(HttpStatus.OK).body(authService.generateToken(authRequest.getEmail(),new Date(System.currentTimeMillis() +1000*60*60*24)));
			else {
//				sent the mail for verification
				authService.sendEmail(authRequest.getEmail(),
						"Link to verify account ( Valid for 1 hour )",
						"<html><body><a href="+gateway+"/auth/verify/"+authService.generateToken(authRequest.getEmail(),new Date(System.currentTimeMillis() +1000*60*60))+">Click here to verify</a></body></html>");
				return ResponseEntity.status(HttpStatus.LOCKED).body("Link to activate accout has been sent to "+authRequest.getEmail());
			}
				
		}
		else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong credentials");
		}
	}
	
	@GetMapping("/verify/{token}")
	public ResponseEntity<String> verifyEmail(@PathVariable String token){
//		whenever we verify user we have to send request to User-Service to create new user
		return ResponseEntity.status(HttpStatus.OK).body(authService.authorizeUser(token));
	}
	
	@PostMapping("/change_password_link/{email}")
	public ResponseEntity<String> sendChangePasswordLink(@PathVariable String email){
//		check if use with given email exists or not
		AuthUser user=authService.findUserByEmail(email);
		
//		sent email to change password
		authService.sendEmail(email,
				"Change password (Valid for 1 hour)",
				"<html>"
			   +"<body>"
		       +"<form action='"+gateway+"/auth/change_password/"+authService.generateToken(user.getEmail(),new Date(System.currentTimeMillis() +1000*60*60))+"' method='post'>"
			   +"<label for='password'>New Password: </label>"
		       +"<input type='text' id='password' name='password' placeholder='Enter new Password'"+" required>"
		       +"<button type='submit'>Submit</button>"
			   +"</form>"
			   +"</body>"
			   +"</html>"
				);
		return ResponseEntity.status(HttpStatus.CREATED).body("Mail to change password has been sent to "+email);
	}
	
	
	@PostMapping("/change_password/{token}")
	public ResponseEntity<String> changePassword(@PathVariable String token, @RequestBody String password){
		System.out.println("New Password: "+password.substring(9));
		return ResponseEntity.status(HttpStatus.OK).body(authService.changePassword(password.substring(9), token));
	}
	
	
}


