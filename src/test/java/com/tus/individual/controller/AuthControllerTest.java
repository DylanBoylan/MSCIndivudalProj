package com.tus.individual.controller;

import static org.mockito.Mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import com.tus.individual.dto.UserLoginDto;
import com.tus.individual.dto.UserLoginResponse;
import com.tus.individual.exception.InvalidCredentialsException;
import com.tus.individual.service.IAuthService;
import com.tus.individual.service.IJwtService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.verification.Times;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

class AuthControllerTest {
	private IAuthService authService;
	private IJwtService jwtService;
	
	private AuthController authController;
	
	private static final String USER_EMAIL = "testadmin@networksys.com";
	private static final String HASHED_USER_PASSWORD = "$2a$10$FKmdMrF41bJuVuI57d1JZ.GKvSKYYzypPbMYji3PjgCqRa26Z56Ti";
	private static final String USER_ROLE = "ADMINISTRATOR";
	
	private static final UserDetails USER_DETAILS = new org.springframework.security.core.userdetails.User(
            USER_EMAIL,
            HASHED_USER_PASSWORD,
            List.of(new SimpleGrantedAuthority(USER_ROLE)));
	
	private static final String JWT_TOKEN = "exampletoken.234.234";
	
	
	@BeforeEach
	void setup() {
		// Mock AuthService
        authService = mock(IAuthService.class);
        jwtService = mock(IJwtService.class);
        authController = new AuthController(authService, jwtService);
	}

    @Test
    void testLogin_Failed_InvalidCredentials() throws InvalidCredentialsException {
        // Given an invalid login attempt
        UserLoginDto userLoginDto = new UserLoginDto("wrong@email.com", "wrongpassword");

        // Mock the service to throw InvalidCredentialsException
        when(authService.authenticate(userLoginDto.getEmail(), userLoginDto.getPassword()))
            .thenThrow(new InvalidCredentialsException("Invalid email or password"));

        // Perform the login attempt
        ResponseEntity<UserLoginResponse> response = authController.createJwt(userLoginDto);

        // Assertions
        assertEquals(401, response.getStatusCode().value());
    }
    
    
    @Test
    void testLoginSuccessful() throws InvalidCredentialsException {
    	// Given an invalid login attempt
        UserLoginDto userLoginDto = new UserLoginDto(USER_EMAIL, HASHED_USER_PASSWORD);

        // Mock the services to return user details object and token token
        when(authService.authenticate(userLoginDto.getEmail(), userLoginDto.getPassword()))
            .thenReturn(USER_DETAILS);
        when(jwtService.generateToken(USER_DETAILS)).thenReturn(JWT_TOKEN);

        // Perform the login attempt
        ResponseEntity<UserLoginResponse> response = authController.createJwt(userLoginDto);

        // Assertions
        assertEquals(200, response.getStatusCode().value());
        assertEquals(JWT_TOKEN, response.getBody().getToken());
        
        verify(authService, new Times(1)).authenticate(USER_EMAIL, userLoginDto.getPassword());
        verify(jwtService, new Times(1)).generateToken(USER_DETAILS);
    }
}
