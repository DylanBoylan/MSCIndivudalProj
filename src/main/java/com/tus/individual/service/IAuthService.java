package com.tus.individual.service;

import org.springframework.security.core.userdetails.UserDetails;

import com.tus.individual.exception.InvalidCredentialsException;

public interface IAuthService {
	UserDetails authenticate(String email, String password) throws InvalidCredentialsException;
}
