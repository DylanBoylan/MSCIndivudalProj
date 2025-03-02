package com.tus.individual.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.tus.individual.dto.UserLoginDto;
import com.tus.individual.dto.UserLoginResponse;
import com.tus.individual.exception.InvalidCredentialsException;
import com.tus.individual.service.IAuthService;
import com.tus.individual.service.IJwtService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final IAuthService authService;
    private final IJwtService jwtService;

    public AuthController(IAuthService authService, IJwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> createJwt(@Valid @RequestBody UserLoginDto userLoginDto) {
        try {
            UserDetails userDetails = authService.authenticate(userLoginDto.getEmail().trim().toLowerCase(), userLoginDto.getPassword());
            UserLoginResponse response = new UserLoginResponse(jwtService.generateToken(userDetails));
            return ResponseEntity.ok(response);
        } catch (InvalidCredentialsException e) {
            return ResponseEntity.status(401).body(new UserLoginResponse(null));
        }
    }
    
}
