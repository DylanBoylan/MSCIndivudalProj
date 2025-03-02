package com.tus.individual.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import com.tus.individual.model.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

class JwtServiceTest {
	private final String SECRET = "c848c57b14028a24bac0b5cd9f2ff6345ca19a291ad62bfa7c3acd6a73a8dc97";
	private final Key SECRET_KEY = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
	private UserDetails userDetails;
	private JwtService jwtService;
	
	private final String EMAIL = "textEmail@example.com";
	private final Role ROLE = Role.CUSTOMER_SERVICE;


	@BeforeEach
	void setup() {
		userDetails = mock(UserDetails.class);
		jwtService = new JwtService();
		jwtService.setSecretKey(SECRET);
	}

	@Test
	void testGenerateToken() {
		when(userDetails.getUsername()).thenReturn(EMAIL);
		doReturn(List.of(ROLE)).when(userDetails).getAuthorities();

		String token = jwtService.generateToken(userDetails);
		
		assertNotNull(token);
		
		Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
		assertEquals(EMAIL, claims.getSubject());
		assertEquals(ROLE.toString(), claims.get("role"));
	}
	
	@Test
	void testExtractClaims() {
		Map<String, Object> claims = new HashMap<>();
		claims.put("role", ROLE);
		
		String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(EMAIL)
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
		
		Claims extractedClaims = jwtService.extractAllClaims(token);
		assertEquals(EMAIL, extractedClaims.getSubject());
		assertEquals(ROLE.toString(), extractedClaims.get("role").toString());
	}

	
	@Test
    void testIsTokenValid() {
		String token = Jwts.builder()
                .setSubject(EMAIL)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // Test shouldn't take longer than an hour to run
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
        
		when(userDetails.getUsername()).thenReturn(EMAIL);
		assertTrue(jwtService.isTokenValid(token, userDetails));
    }
	
    @Test
    void testIsTokenValid_InvalidUsername() {
    	final String INVALID_EMAIL = "wrongEmail@gmail.com";
    	String token = Jwts.builder()
                .setSubject(EMAIL)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // Test shouldn't take longer than an hour to run
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
        
		when(userDetails.getUsername()).thenReturn(INVALID_EMAIL);
		assertFalse(jwtService.isTokenValid(token, userDetails));
    }
    
    @Test
    void testExpiredToken() throws InterruptedException {
        String token = Jwts.builder()
                .setSubject(EMAIL)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1)) // Expires 1 millisecond after creation
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();

        // Wait for 10 milliseconds before proceeding (for token to expire)
        Thread.sleep(10); 

        when(userDetails.getUsername()).thenReturn(EMAIL);
        assertFalse(jwtService.isTokenValid(token, userDetails));
    }
    

}
