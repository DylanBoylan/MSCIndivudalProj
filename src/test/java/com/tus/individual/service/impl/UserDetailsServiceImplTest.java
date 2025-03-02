package com.tus.individual.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.tus.individual.dao.UserRepository;
import com.tus.individual.model.Role;
import com.tus.individual.model.User;


class UserDetailsServiceImplTest {
	private UserRepository userRepo;
	private UserDetailsServiceImpl userDetailsService;
	
	
	@BeforeEach
	void setup() {
		userRepo = mock(UserRepository.class);
		userDetailsService = new UserDetailsServiceImpl(userRepo);
	}
	
	
	@Test
	void testLoadInvalidEmail() {
		final String EMAIL = "testEmail@gmail.com";
		final String ERROR_MESSAGE = "User not found: " + EMAIL;
		Optional<User> nullUser = Optional.empty();
		when(userRepo.findByEmail(EMAIL)).thenReturn(nullUser);
		
		Throwable e = assertThrows(UsernameNotFoundException.class, () -> {
			userDetailsService.loadUserByUsername(EMAIL);
		});
		
		assertEquals(ERROR_MESSAGE, e.getMessage());
		
	}
	
	@Test
	void testLoadValidEmail() {
		final String EMAIL = "testEmail@gmail.com";
		final String PASSWORD = "Password123!";
		final Role ROLE = Role.CUSTOMER_SERVICE;
		final User user = new User();
		user.setEmail(EMAIL);
		user.setPassword(PASSWORD);
		user.setRole(ROLE);
		
		Optional<User> userOptional = Optional.of(user);
		when(userRepo.findByEmail(EMAIL)).thenReturn(userOptional);
		
		UserDetails userDetailsResult = userDetailsService.loadUserByUsername(EMAIL);
		
		assertEquals(EMAIL, userDetailsResult.getUsername());
		assertEquals(PASSWORD, userDetailsResult.getPassword());
		
		Collection<? extends GrantedAuthority> roles = userDetailsResult.getAuthorities();
		assertEquals(roles.size(), 1);
		assertTrue(roles.contains(ROLE));
	}
}
