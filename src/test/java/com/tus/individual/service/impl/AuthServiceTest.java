package com.tus.individual.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tus.individual.exception.InvalidCredentialsException;

class AuthServiceTest {
	private UserDetailsService userDetailsService;
	private PasswordEncoder passwordEncoder;
	private AuthService authService;

	private final String EMAIL = "testEmail@gmail.com";
	private final String PASSWORD = "Password123!";
	private final String USER_ROLE = "ADMINISTRATOR";


	@BeforeEach
	void setup() {
		userDetailsService = mock(UserDetailsService.class);
		passwordEncoder = mock(PasswordEncoder.class);
		authService = new AuthService(passwordEncoder, userDetailsService);

	}

	@Test
	void testInvalidEmail() {
		final String ERROR_MESSAGE = "Invalid email";
		when(userDetailsService.loadUserByUsername(EMAIL)).thenThrow(UsernameNotFoundException.class);

		Throwable e = assertThrows(InvalidCredentialsException.class, () -> {
			authService.authenticate(EMAIL, PASSWORD);
		});

		assertEquals(ERROR_MESSAGE, e.getMessage());	
	}

	@Test
	void testInvalidPassword() {
		final String DATABASE_PASSWORD = "Different123!";
		final String HASHED_DB_PASSWORD = "hashed(" + DATABASE_PASSWORD + ")";
		final String ERROR_MESSAGE = "Invalid password";
		final UserDetails userDetails = new org.springframework.security.core.userdetails.User(
				EMAIL,
				HASHED_DB_PASSWORD,
				List.of(new SimpleGrantedAuthority(USER_ROLE)));
		
		when(userDetailsService.loadUserByUsername(EMAIL)).thenReturn(userDetails);
		when(passwordEncoder.matches(PASSWORD, HASHED_DB_PASSWORD)).thenReturn(false);

		Throwable e = assertThrows(InvalidCredentialsException.class, () -> {
			authService.authenticate(EMAIL, PASSWORD);
		});

		assertEquals(ERROR_MESSAGE, e.getMessage());	
	}

	@Test
	void testCorrectCredentials() throws InvalidCredentialsException {
		final String DATABASE_PASSWORD = PASSWORD;
		final String HASHED_DB_PASSWORD = "hashed(" + DATABASE_PASSWORD + ")";
		final UserDetails userDetails = new org.springframework.security.core.userdetails.User(
				EMAIL,
				HASHED_DB_PASSWORD,
				List.of(new SimpleGrantedAuthority(USER_ROLE)));
		
		when(userDetailsService.loadUserByUsername(EMAIL)).thenReturn(userDetails);
		when(passwordEncoder.matches(PASSWORD, HASHED_DB_PASSWORD)).thenReturn(true);

		UserDetails result = authService.authenticate(EMAIL, PASSWORD);

		assertEquals(userDetails, result);
	}

}
