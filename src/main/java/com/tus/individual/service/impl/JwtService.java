package com.tus.individual.service.impl;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.security.Key;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.tus.individual.service.IJwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Setter;

@Setter
@Service
public class JwtService implements IJwtService {
	
	@Value("${jwt.secret}")
	private String secretKey;
    
    /**
     * Generates a JWT Token for a User with Role
     */
    public String generateToken(UserDetails userDetails) {
        // Extract the user role correctly
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        String role = authorities.stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("USER"); // Default to USER if no role is found

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role); // Store role in JWT
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1-hour expiry
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    
    /**
     * Check if the token is still valid
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
        	final String username = extractUsername(token);
        	return username.equals(userDetails.getUsername());
        } catch (ExpiredJwtException e) {
        	return false;
        }
    }
   
    /**
     * Extract all claims from a JWT token
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Helper functions
    /**
     * Extract a specific claim using a resolver function
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts the username (email) from the token
     */
    private String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Get the signing key used for signing JWTs
     */
    protected Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
