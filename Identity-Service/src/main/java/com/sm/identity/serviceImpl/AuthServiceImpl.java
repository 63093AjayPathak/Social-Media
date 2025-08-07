package com.sm.identity.serviceImpl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.sm.identity.dto.AuthRequest;
import com.sm.identity.entity.AuthUser;
import com.sm.identity.repository.AuthUserRepository;
import com.sm.identity.service.AuthService;
import com.sm.identity.exception_handler.UserNotFoundException;
import com.sm.identity.exception_handler.InvalidTokenException;
import com.sm.identity.exception_handler.EmailSendException;
import com.sm.identity.exception_handler.PasswordValidationException;
import com.sm.identity.service.LoggingService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class AuthServiceImpl implements AuthService {

	private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

	@Autowired
	private AuthUserRepository userRepo;
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private PasswordEncoder encoder;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private JavaMailSender jms;
	
	@Autowired
	private LoggingService loggingService;
	
	@Override
	public String saveUser(AuthRequest user, String gateway) {
		AuthUser authUser = AuthUser.builder().email(user.getEmail()).password(encoder.encode(user.getPassword())).authorized(false).build();
		AuthUser savedUser = userRepo.save(authUser);
		
		Map<String, Object> params = new HashMap<>();
		params.put("email", user.getEmail());
		params.put("userId", savedUser.getId());
		
		loggingService.logInfo("User saved to database", null, null, 
							 "AuthServiceImpl", "saveUser", params, String.valueOf(savedUser.getId()), user.getEmail(), "PENDING");
		
		try {
			this.sendEmail(user.getEmail(),
					"Link to verify account ( Valid for 1 hour )",
					"<html>"
							+ "<body>"
							+ "<a href=" + gateway + "/auth/verify/" + this.generateToken(user.getEmail(), new Date(System.currentTimeMillis() + 1000 * 60 * 60)) + ">"
							+ "Click here to verify"
							+ "</a>"
							+ "</body>"
							+ "</html>");
			loggingService.logInfo("Registration email sent", null, null, 
								 "AuthServiceImpl", "saveUser", params, String.valueOf(savedUser.getId()), user.getEmail(), "PENDING");
		} catch (EmailSendException ex) {
			userRepo.deleteById(savedUser.getId());
			loggingService.logError("Failed to send registration email", null, null, 
								  "AuthServiceImpl", "saveUser", params, String.valueOf(savedUser.getId()), user.getEmail(), "PENDING", ex);
			throw ex;
		}
		loggingService.logInfo("New user registration completed", null, null, 
							 "AuthServiceImpl", "saveUser", params, String.valueOf(savedUser.getId()), user.getEmail(), "PENDING");
		return "New User Registered";
	}

	@Override
	public AuthUser findUserByEmail(String email) {
		return userRepo.findByEmail(email).orElseThrow(() -> {
			Map<String, Object> params = new HashMap<>();
			params.put("email", email);
			loggingService.logWarn("User not found", null, null, 
								 "AuthServiceImpl", "findUserByEmail", params, null, email, null);
			return new UserNotFoundException("No user found with email: " + email);
		});
	}

	@Override
	public String generateToken(String email, Date date) {
		AuthUser user = userRepo.findByEmail(email).orElseThrow(() -> {
			Map<String, Object> params = new HashMap<>();
			params.put("email", email);
			loggingService.logWarn("User not found for token generation", null, null, 
								 "AuthServiceImpl", "generateToken", params, null, email, null);
			return new UserNotFoundException("No user found with email: " + email);
		});
		
		Map<String, Object> params = new HashMap<>();
		params.put("email", email);
		params.put("userId", user.getId());
		
		loggingService.logInfo("Token generated", null, null, 
							 "AuthServiceImpl", "generateToken", params, String.valueOf(user.getId()), email, "USER");
		return jwtService.generateToken(user.getEmail(), date, user.getId());
	}

	@Override
	public String validateToken(String token) {
		try {
			String email = jwtService.validateToken(token);
			Map<String, Object> params = new HashMap<>();
			params.put("token", token);
			params.put("email", email);
			
			loggingService.logInfo("Token validated", null, null, 
								 "AuthServiceImpl", "validateToken", params, null, email, "USER");
			return email;
		} catch (Exception ex) {
			Map<String, Object> params = new HashMap<>();
			params.put("token", token);
			
			loggingService.logWarn("Invalid or expired token", null, null, 
								 "AuthServiceImpl", "validateToken", params, null, null, null);
			throw new InvalidTokenException("Invalid or expired token");
		}
	}

	@Override
	public String authorizeUser(String token, String co_relation_id) {
		String email = this.validateToken(token);
		AuthUser user = userRepo.findByEmail(email).orElseThrow(() -> {
			Map<String, Object> params = new HashMap<>();
			params.put("email", email);
			params.put("correlationId", co_relation_id);
			loggingService.logWarn("User not found for authorization", co_relation_id, null, 
								 "AuthServiceImpl", "authorizeUser", params, null, email, null);
			return new UserNotFoundException("No user found with email: " + email);
		});
		
		user.setAuthorized(true);
		userRepo.save(user);
		
		Map<String, Object> params = new HashMap<>();
		params.put("email", email);
		params.put("userId", user.getId());
		params.put("correlationId", co_relation_id);
		
		loggingService.logInfo("User authorized", co_relation_id, null, 
							 "AuthServiceImpl", "authorizeUser", params, String.valueOf(user.getId()), email, "AUTHORIZED");
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("X-Corelation-ID", co_relation_id);
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.generateToken(email, new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)));
		Map<String, Object> body = new HashMap<>();
		body.put("userId", user.getId());
		body.put("userEmail", user.getEmail());
		HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
		
		try {
			ResponseEntity<String> resp = restTemplate.postForEntity("http://Api-Gateway/user/", requestEntity, String.class);
			if (resp.getStatusCode().isSameCodeAs(HttpStatus.CREATED)) {
				loggingService.logInfo("User creation verified in User-Service", co_relation_id, null, 
									 "AuthServiceImpl", "authorizeUser", params, String.valueOf(user.getId()), email, "AUTHORIZED");
				return "User verified for email: " + user.getEmail();
			} else {
				user.setAuthorized(false);
				userRepo.save(user);
				loggingService.logError("User-Service failed to create user", co_relation_id, null, 
									  "AuthServiceImpl", "authorizeUser", params, String.valueOf(user.getId()), email, "AUTHORIZED", 
									  new RuntimeException("User-Service returned non-CREATED status"));
				throw new RuntimeException("User cannot be verified, try again later");
			}
		} catch (Exception ex) {
			user.setAuthorized(false);
			userRepo.save(user);
			loggingService.logError("Downstream User-Service error", co_relation_id, null, 
								  "AuthServiceImpl", "authorizeUser", params, String.valueOf(user.getId()), email, "AUTHORIZED", ex);
			throw new RuntimeException("User verification failed due to downstream service error");
		}
	}

	@Override
	@Transactional
	public String changePassword(String password, String token) {
		String email = this.validateToken(token);
		AuthUser user = userRepo.findByEmail(email).orElseThrow(() -> {
			Map<String, Object> params = new HashMap<>();
			params.put("email", email);
			params.put("token", token);
			loggingService.logWarn("User not found for password change", null, null, 
								 "AuthServiceImpl", "changePassword", params, null, email, null);
			return new UserNotFoundException("No user found with email: " + email);
		});
		
		if (password == null || password.trim().length() == 0) {
			Map<String, Object> params = new HashMap<>();
			params.put("email", email);
			params.put("token", token);
			loggingService.logWarn("Password validation failed - empty password", null, null, 
								 "AuthServiceImpl", "changePassword", params, String.valueOf(user.getId()), email, "USER");
			throw new PasswordValidationException("Password cannot be empty");
		}
		
		user.setPassword(encoder.encode(password));
		userRepo.save(user);
		
		Map<String, Object> params = new HashMap<>();
		params.put("email", email);
		params.put("userId", user.getId());
		
		loggingService.logInfo("Password updated", null, null, 
							 "AuthServiceImpl", "changePassword", params, String.valueOf(user.getId()), email, "USER");
		return "Password updated for " + email;
	}

	@Override
	public String sendEmail(String to, String subject, String mailBody) {
		try {
			MimeMessage mm = jms.createMimeMessage();
			MimeMessageHelper message = new MimeMessageHelper(mm, false);
			message.setTo(to);
			message.setSubject(subject);
			message.setText(mailBody, true);
			jms.send(mm);
			
			Map<String, Object> params = new HashMap<>();
			params.put("to", to);
			params.put("subject", subject);
			
			loggingService.logInfo("Email sent successfully", null, null, 
								 "AuthServiceImpl", "sendEmail", params, null, to, null);
		} catch (jakarta.mail.MessagingException ex) {
			Map<String, Object> params = new HashMap<>();
			params.put("to", to);
			params.put("subject", subject);
			
			loggingService.logError("Failed to send email", null, null, 
								  "AuthServiceImpl", "sendEmail", params, null, to, null, ex);
			throw new EmailSendException("Failed to send email to " + to, ex);
		}
		return "Link sent";
	}

}
