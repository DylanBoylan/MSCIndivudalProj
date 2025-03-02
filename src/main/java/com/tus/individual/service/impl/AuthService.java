package com.tus.individual.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tus.individual.exception.InvalidCredentialsException;
import com.tus.individual.service.IAuthService;

@Service
public class AuthService implements IAuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;

    public AuthService(PasswordEncoder passwordEncoder, UserDetailsService userDetailsService) {
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    public UserDetails authenticate(String email, String password) throws InvalidCredentialsException {
    	try {
    		UserDetails userDetails = userDetailsService.loadUserByUsername(email);
    		if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                throw new InvalidCredentialsException("Invalid password");
            }
    		return userDetails;		
    	} catch (UsernameNotFoundException e) {
    		throw new InvalidCredentialsException("Invalid email");
    	}
    }
}
