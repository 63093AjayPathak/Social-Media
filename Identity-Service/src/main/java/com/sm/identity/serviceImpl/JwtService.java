package com.sm.identity.serviceImpl;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtService {

//	grab this from application.properties file where it would be provided from config server
	@Value("${jwt.secret}")
	private String SECRET;
	
	public String validateToken(final String token) {
		
		Claims claims=Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
		
		return claims.getSubject();
	}
	
	public String generateToken(String email, Date date, int id) {
//		set the custom details that we want to send with token like id
		Map<String, Object> claims= new HashMap<>();
		claims.put("user_id", id);
		return createToken(claims, email, date);
	}
	
	private String createToken(Map<String,Object> claims, String userName, Date date) {
		
		return Jwts.builder()
				.setClaims(claims)
				.setSubject(userName)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(date)
				.signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
	}
	
	private  Key getSignKey() {
		byte[] keyBytes=Decoders.BASE64.decode(SECRET);
		return Keys.hmacShaKeyFor(keyBytes);
	}
}
