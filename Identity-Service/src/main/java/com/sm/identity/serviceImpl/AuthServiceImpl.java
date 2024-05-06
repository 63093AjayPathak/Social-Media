package com.sm.identity.serviceImpl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class AuthServiceImpl implements AuthService {

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
	
	@Override
	public String saveUser(AuthRequest user, String gateway) {
		AuthUser authUser= AuthUser.builder().email(user.getEmail()).password(encoder.encode(user.getPassword())).authorized(false).build();
		   AuthUser savedUser =userRepo.save(authUser);
		
		try {
			this.sendEmail(user.getEmail(),
					"Link to verify account ( Valid for 1 hour )",
					"<html>"
					+"<body>"
					+"<a href="+gateway+"/auth/verify/"+this.generateToken(user.getEmail(),new Date(System.currentTimeMillis() +1000*60*60))+">"
					+"Click here to verify"
					+ "</a>"
					+"</body>"
					+"</html>");
		}
		catch(RuntimeException ex) {
			userRepo.deleteById(savedUser.getId());
			throw new RuntimeException(ex);
		}
		
		   return "New User Registered";
		 
	}
	
	@Override
	public AuthUser findUserByEmail(String email) {
		
		return userRepo.findByEmail(email).orElseThrow(()->new RuntimeException("No user found with email: "+email));
	}

	@Override
	public String generateToken(String email, Date date) {
		AuthUser user= userRepo.findByEmail(email).orElseThrow(()->new RuntimeException("No user found with email: "+email));
		return jwtService.generateToken(user.getEmail(), date, user.getId());
	}

	@Override
	public String validateToken(String token) {
	   return jwtService.validateToken(token);
	}

	@Override
	public String authorizeUser(String token, String co_relation_id) {
		String email=this.validateToken(token);
		AuthUser user= userRepo.findByEmail(email).orElseThrow(()->new RuntimeException("No user found with email: "+email));
		user.setAuthorized(true);
		userRepo.save(user);
		
		HttpHeaders headers= new HttpHeaders();
		headers.add("X-Corelation-ID", co_relation_id);
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer "+this.generateToken(email, new Date(System.currentTimeMillis() +1000*60*60*24)));
//		HttpEntity<String> entity = new HttpEntity<>(headers);
		
		Map<String, Object> body = new HashMap<>();
        body.put("userId", user.getId());
        body.put("userEmail", user.getEmail());

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // POST request to User-Service
        try {
        	ResponseEntity<String> resp = restTemplate.postForEntity("http://Api-Gateway/user/", requestEntity, String.class);
            
            if(resp.getStatusCode().isSameCodeAs(HttpStatus.CREATED))
            	return "User verified for email: "+user.getEmail();
            else {
            	user.setAuthorized(false);
        		userRepo.save(user);
            	throw new RuntimeException("User cannot created be verified, try again later");
            }
        }
        catch(RuntimeException ex) {
        	user.setAuthorized(false);
    		userRepo.save(user);
        	throw new RuntimeException(ex);
        }
        	
		
	}

	@Override
	@Transactional
	public String changePassword(String password, String token) {
		// TODO Auto-generated method stub
		String email=this.validateToken(token);
		AuthUser user= userRepo.findByEmail(email).orElseThrow(()->new RuntimeException("No user found with email: "+email));
		
		if(password==null || password.trim().length()==0)
			throw new RuntimeException("Password cannot be empty");
		
		user.setPassword(encoder.encode(password));
		userRepo.save(user);
		return "Password updated for "+email;
	}

	@Override
	public String sendEmail(String to, String subject, String mailBody){
		try {
			MimeMessage mm= jms.createMimeMessage();
			MimeMessageHelper message= new MimeMessageHelper(mm,false);
			message.setTo(to);
			message.setSubject(subject);
			message.setText(mailBody,true);
			jms.send(mm);
		}
		catch(MessagingException ex) {
			throw new RuntimeException(ex);
		}
		return "Link sent";
	}

}
