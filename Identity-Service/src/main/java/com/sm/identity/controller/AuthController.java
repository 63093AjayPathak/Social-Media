package com.sm.identity.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;

import com.google.common.net.HttpHeaders;
import com.sm.identity.dto.AuthRequest;
import com.sm.identity.entity.AuthUser;
import com.sm.identity.service.AuthService;
import com.sm.identity.service.LoggingService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.servlet.http.HttpServletRequest;

@RestController
//@CrossOrigin()
@RequestMapping("/auth")
@Validated
public class AuthController {
	
	@Value("${gateway_url}")
	private String gateway;
	
	@Autowired
	private AuthService authService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private LoggingService loggingService;
	
	private Logger logger = LoggerFactory.getLogger(AuthController.class);
	
	@PostMapping("/register")
	public ResponseEntity<String> addNewUSer(@Valid @RequestBody AuthRequest user, HttpServletRequest request) {
		String correlationId = request.getHeader("X-Corelation-ID");
		Map<String, Object> params = new HashMap<>();
		params.put("email", user.getEmail());
		
		loggingService.logInfo("Received registration request", correlationId, request, 
							 "AuthController", "addNewUser", params, null, user.getEmail(), null);
		
		authService.saveUser(user, gateway);
		
		loggingService.logInfo("Registration process completed", correlationId, request, 
							 "AuthController", "addNewUser", params, null, user.getEmail(), null);
		
		return ResponseEntity.status(HttpStatus.CREATED).body("Link to activate account has been sent to " + user.getEmail());
	}
	
	@PostMapping("/token")
	public ResponseEntity<String> getToken(@Valid @RequestBody AuthRequest authRequest, HttpServletRequest request) {
		String correlationId = request.getHeader("X-Corelation-ID");
		Map<String, Object> params = new HashMap<>();
		params.put("email", authRequest.getEmail());
		
		loggingService.logInfo("Login/token request received", correlationId, request, 
							 "AuthController", "getToken", params, null, authRequest.getEmail(), null);
		
		Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
		
		if (authenticate.isAuthenticated()) {
			if (authService.findUserByEmail(authRequest.getEmail()).isAuthorized()) {
				loggingService.logInfo("User authenticated and authorized", correlationId, request, 
									 "AuthController", "getToken", params, null, authRequest.getEmail(), "USER");
				return ResponseEntity.status(HttpStatus.OK).body(authService.generateToken(authRequest.getEmail(), new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)));
			} else {
				loggingService.logInfo("User authenticated but not yet authorized", correlationId, request, 
									 "AuthController", "getToken", params, null, authRequest.getEmail(), "PENDING");
				authService.sendEmail(authRequest.getEmail(),
						"Link to verify account ( Valid for 1 hour )",
						"<html><body><a href=" + gateway + "/auth/verify/" + authService.generateToken(authRequest.getEmail(), new Date(System.currentTimeMillis() + 1000 * 60 * 60)) + ">Click here to verify</a></body></html>");
				return ResponseEntity.status(HttpStatus.LOCKED).body("Link to activate account has been sent to " + authRequest.getEmail());
			}
		} else {
			loggingService.logWarn("Failed login attempt", correlationId, request, 
								 "AuthController", "getToken", params, null, authRequest.getEmail(), null);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong credentials");
		}
	}
	
	@GetMapping("/verify/{token}")
	@CircuitBreaker(name = "UserCircuitBreaker", fallbackMethod = "verifyAndCreateUserFallback")
	public ResponseEntity<String> verifyEmail(@PathVariable String token, HttpServletRequest request) {
		String correlationId = request.getHeader("X-Corelation-ID");
		Map<String, Object> params = new HashMap<>();
		params.put("token", token);
		
		loggingService.logInfo("Email verification request received", correlationId, request, 
							 "AuthController", "verifyEmail", params, null, null, null);
		
		return ResponseEntity.status(HttpStatus.OK).body(authService.authorizeUser(token, correlationId));
	}

	public ResponseEntity<String> verifyAndCreateUserFallback(String token, HttpServletRequest request, Exception ex) {
		String correlationId = request.getHeader("X-Corelation-ID");
		Map<String, Object> params = new HashMap<>();
		params.put("token", token);
		
		loggingService.logError("Verification fallback triggered", correlationId, request, 
							  "AuthController", "verifyAndCreateUserFallback", params, null, null, null, ex);
		
		return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).body("Unable to process request");
	}

	@PostMapping("/change_password_link/{email}")
	public ResponseEntity<String> sendChangePasswordLink(@PathVariable String email, HttpServletRequest request) {
		String correlationId = request.getHeader("X-Corelation-ID");
		Map<String, Object> params = new HashMap<>();
		params.put("email", email);
		
		loggingService.logInfo("Change password link request received", correlationId, request, 
							 "AuthController", "sendChangePasswordLink", params, null, email, null);
		
		AuthUser user = authService.findUserByEmail(email);
		authService.sendEmail(email,
				"Change password (Valid for 1 hour)",
				"<html>"
						+ "<body>"
						+ "<form action='" + gateway + "/auth/change_password/" + authService.generateToken(user.getEmail(), new Date(System.currentTimeMillis() + 1000 * 60 * 60)) + "' method='post'>"
						+ "<label for='password'>New Password: </label>"
						+ "<input type='text' id='password' name='password' placeholder='Enter new Password' required>"
						+ "<button type='submit'>Submit</button>"
						+ "</form>"
						+ "</body>"
						+ "</html>"
		);
		
		loggingService.logInfo("Change password link sent", correlationId, request, 
							 "AuthController", "sendChangePasswordLink", params, null, email, null);
		
		return ResponseEntity.status(HttpStatus.CREATED).body("Mail to change password has been sent to " + email);
	}

	@PostMapping("/change_password/{token}")
	public ResponseEntity<String> changePassword(@PathVariable String token, @RequestBody String password, HttpServletRequest request) {
		String correlationId = request.getHeader("X-Corelation-ID");
		Map<String, Object> params = new HashMap<>();
		params.put("token", token);
		// Do not log password in params
		
		loggingService.logInfo("Password change request received", correlationId, request, 
							 "AuthController", "changePassword", params, null, null, null);
		
		return ResponseEntity.status(HttpStatus.OK).body(authService.changePassword(password, token));
	}
	
	
}


