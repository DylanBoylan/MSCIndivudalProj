package com.tus.individual.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Base64;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private String secretKey;
    private Key signingKey;

    @Mock
    private UserDetails mockUserDetails;

    @BeforeEach
    void setUp() {
        // Generate a random secret key for testing
        secretKey = Base64.getEncoder().encodeToString(Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded());
        jwtService.setSecretKey(secretKey);
        signingKey = jwtService.getSigningKey();

        // Mock UserDetails with role
        lenient().when(mockUserDetails.getUsername()).thenReturn("test@example.com");
        lenient().doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
        .when(mockUserDetails)
        .getAuthorities();
}


    // ✅ Test JWT Token Generation
    @Test
    void testGenerateToken() {
        String token = jwtService.generateToken(mockUserDetails);
        assertNotNull(token);
        assertFalse(token.isEmpty());

        Claims claims = jwtService.extractAllClaims(token);
        assertEquals("test@example.com", claims.getSubject());
        assertEquals("ROLE_USER", claims.get("role"));
    }

    // ✅ Test Token Validation (Valid Token)
    @Test
    void testIsTokenValid_ValidToken() {
        String token = jwtService.generateToken(mockUserDetails);
        assertTrue(jwtService.isTokenValid(token, mockUserDetails));
    }

    // ✅ Test Token Validation (Invalid Username)
    @Test
    void testIsTokenValid_InvalidUsername() {
        String token = jwtService.generateToken(mockUserDetails);

        // Mock a different user
        UserDetails anotherUser = mock(UserDetails.class);
        when(anotherUser.getUsername()).thenReturn("wrong@example.com");

        assertFalse(jwtService.isTokenValid(token, anotherUser));
    }

    // ✅ Test Token Validation (Expired Token)
    @Test
    void testIsTokenValid_ExpiredToken() {
        // Manually create an expired token
        String expiredToken = Jwts.builder()
                .setSubject("test@example.com")
                .setExpiration(new Date(System.currentTimeMillis() - 1000)) // Expired already
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();

        assertFalse(jwtService.isTokenValid(expiredToken, mockUserDetails));
    }

    // ✅ Test Extracting Claims
    @Test
    void testExtractAllClaims() {
        String token = jwtService.generateToken(mockUserDetails);
        Claims claims = jwtService.extractAllClaims(token);

        assertNotNull(claims);
        assertEquals("test@example.com", claims.getSubject());
        assertEquals("ROLE_USER", claims.get("role"));
    }

    // ✅ Test Extracting Username
    @Test
    void testExtractUsername() {
        String token = jwtService.generateToken(mockUserDetails);
        String username = jwtService.extractAllClaims(token).getSubject();

        assertEquals("test@example.com", username);
    }
}
