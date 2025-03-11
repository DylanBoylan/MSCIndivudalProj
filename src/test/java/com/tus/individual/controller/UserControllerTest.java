package com.tus.individual.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tus.individual.dao.UserRepository;
import com.tus.individual.dto.UserRegistrationDto;
import com.tus.individual.model.Role;
import com.tus.individual.model.User;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserRepository userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserController userController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

   

    // ✅ Test creating a user with an already used email (Conflict)
    @Test
    void testCreateUser_EmailExists() throws Exception {
        UserRegistrationDto newUser = new UserRegistrationDto(
                "admin@example.com",
                "Password@123",
                Role.ADMINISTRATOR
        );

        when(userRepo.existsByEmail("admin@example.com")).thenReturn(true);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isConflict());

        verify(userRepo, never()).save(any(User.class));
    }

    // ✅ Test updating a user (Success)
    @Test
    void testUpdateUser_Success() throws Exception {
        User existingUser = new User(1L, "user@example.com", "oldPassword", Role.COACH);
        UserRegistrationDto updatedUser = new UserRegistrationDto(
                "user@example.com",
                "NewPass@456",  // ✅ Strong password
                Role.COACH
        );

        when(userRepo.findByEmail("user@example.com")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("NewPass@456")).thenReturn("newHashedPassword");
        when(userRepo.save(any(User.class))).thenReturn(existingUser);

        mockMvc.perform(put("/api/users/user@example.com")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(content().string("User successfully updated."));

        verify(userRepo, times(1)).save(any(User.class));
    }

    // ✅ Test updating a non-existing user (Not Found)
    @Test
    void testUpdateUser_NotFound() throws Exception {
        UserRegistrationDto updatedUser = new UserRegistrationDto(
                "unknown@example.com",
                "NewPass@789",
                Role.COACH
        );

        when(userRepo.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/users/unknown@example.com")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User with this email does not exist."));

        verify(userRepo, never()).save(any(User.class));
    }

    // ✅ Test deleting a user (Success)
    @Test
    void testDeleteUser_Success() throws Exception {
        when(userRepo.existsByEmail("user@example.com")).thenReturn(true);

        mockMvc.perform(delete("/api/users/user@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("User successfully deleted."));

        verify(userRepo, times(1)).deleteByEmail("user@example.com");
    }

    // ✅ Test deleting a non-existing user (Not Found)
    @Test
    void testDeleteUser_NotFound() throws Exception {
        when(userRepo.existsByEmail("unknown@example.com")).thenReturn(false);

        mockMvc.perform(delete("/api/users/unknown@example.com"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User with this email does not exist."));

        verify(userRepo, never()).deleteByEmail(anyString());
    }

}
