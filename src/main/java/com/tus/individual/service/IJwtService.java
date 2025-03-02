package com.tus.individual.service;

import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;

public interface IJwtService {
	/**
    * Generates a JWT Token for a User with Role
    */
   public String generateToken(UserDetails userDetails);
   
   
   /**
    * Check if the token is still valid
    */
   public boolean isTokenValid(String token, UserDetails userDetails);
  
   /**
    * Extract all claims from a JWT token
    */
   public Claims extractAllClaims(String token);
}
