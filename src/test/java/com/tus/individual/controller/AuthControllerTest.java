package com.tus.individual.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tus.individual.dto.UserLoginDto;
import com.tus.individual.dto.UserLoginResponse;
import com.tus.individual.exception.InvalidCredentialsException;
import com.tus.individual.service.IAuthService;
import com.tus.individual.service.IJwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IAuthService authService;

    @Mock
    private IJwtService jwtService;

    @InjectMocks
    private AuthController authController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    // ✅ Test Successful Login
    @Test
    void testCreateJwt_Success() throws Exception {
        UserLoginDto loginDto = new UserLoginDto("User@Example.com", "password123");
        UserDetails mockUserDetails = mock(UserDetails.class);
        when(authService.authenticate("user@example.com", "password123")).thenReturn(mockUserDetails);
        when(jwtService.generateToken(mockUserDetails)).thenReturn("mockJwtToken");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mockJwtToken"));

        verify(authService, times(1)).authenticate("user@example.com", "password123");
        verify(jwtService, times(1)).generateToken(mockUserDetails);
    }

    // ✅ Test Failed Login (Invalid Credentials)
    @Test
    void testCreateJwt_InvalidCredentials() throws Exception {
        UserLoginDto loginDto = new UserLoginDto("wrong@example.com", "wrongPassword");

        when(authService.authenticate("wrong@example.com", "wrongPassword"))
                .thenThrow(new InvalidCredentialsException("Invalid credentials"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized())  // ✅ Expect 401 instead of 200
                .andExpect(jsonPath("$.jwtToken").doesNotExist());  // ✅ Ensure no token is returned

        verify(authService, times(1)).authenticate("wrong@example.com", "wrongPassword");
        verify(jwtService, never()).generateToken(any());
    }

}
