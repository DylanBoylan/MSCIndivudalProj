package com.tus.individual.controller;

import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import com.tus.individual.dao.UserRepository;
import com.tus.individual.dto.UserDto;
import com.tus.individual.dto.UserRegistrationDto;
import com.tus.individual.dto.UserRegistrationResponse;
import com.tus.individual.model.Role;
import com.tus.individual.model.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.verification.Times;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class UserControllerTest {
	private UserRepository userRepo;
    private UserController userController;
    
    private final Long ID = 12L;
    private final String EMAIL = "test@domain.com";
    private final String PASSWORD = "Password123!";
    private final Role ROLE = Role.CUSTOMER_SERVICE;
    private final User USER = new User(ID, EMAIL, PASSWORD, ROLE);
    private final UserRegistrationDto USER_REG_DTO = new UserRegistrationDto();
    
    {
    	USER_REG_DTO.setEmail(EMAIL);
    	USER_REG_DTO.setPassword(PASSWORD);
    	USER_REG_DTO.setRole(ROLE);
    }
    
    // Response messages
    private final String GET_FAILURE_MESSAGE = "User with this email does not exist.";
    private final String UPDATE_FAILURE_MESSAGE = "User with this email does not exist.";
    private final String UPDATE_SUCCESS_MESSAGE = "User successfully updated.";
    private final String DELETE_FAILURE_MESSAGE = "User with this email does not exist.";
    private final String DELETE_SUCCESS_MESSAGE = "User successfully deleted.";

    @BeforeEach
    void setUp() {
        userRepo = mock(UserRepository.class);
        userController = new UserController(userRepo, new BCryptPasswordEncoder());
    }

    // Create User
    @Test
    void testAccountAlreadyExistsCreation() {
        when(userRepo.existsByEmail(EMAIL)).thenReturn(true);

        ResponseEntity<UserRegistrationResponse> response = userController.createUser(USER_REG_DTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(-1L, response.getBody().getId());
    }
    
    @Test
    void testSuccessfulAccountCreation() {
        when(userRepo.existsByEmail(EMAIL)).thenReturn(false);
        when(userRepo.save(any())).thenAnswer(invocation -> {
        	User user = invocation.getArgument(0);
            user.setId(ID);
            return user;
        });

        ResponseEntity<UserRegistrationResponse> response = userController.createUser(USER_REG_DTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(ID, response.getBody().getId());
        
        verify(userRepo, new Times(1)).save(any());
    }

	
    
	// Get All Users
    @Test
    void testGetsAllUsers() { 	
    	List<User> users = List.of(USER);
        when(userRepo.findAll()).thenReturn(users);

        List<UserDto> userDtos = userController.getAllUsers();

        assertEquals(users.size(), userDtos.size());
        for (int i = 0; i < users.size(); i++) {
            assertEquals(users.get(i).getEmail(), userDtos.get(i).getEmail());
            assertEquals(users.get(i).getRole(), userDtos.get(i).getRole());
        }
    }
	
	// Get specific user
    @Test
	void testGetNonExistentUser() {
		when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.empty());
		
		ResponseEntity<?> response = userController.getUser(EMAIL);
		
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals(GET_FAILURE_MESSAGE, response.getBody());
	}
	
    @Test
	void testGetUserSuccess() {
		when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.of(USER));
		
		ResponseEntity<?> response = userController.getUser(EMAIL);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		UserDto userDto = (UserDto) response.getBody();
		assertEquals(USER.getEmail(), userDto.getEmail());
		assertEquals(USER.getRole(), userDto.getRole());
	}

	
	// Update User
	@Test
	void testUpdateNonExistentUser() {
		UserRegistrationDto userRegDto = new UserRegistrationDto();
		
		when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.empty());
		
		ResponseEntity<String> response = userController.updateUser(EMAIL, userRegDto);
		
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals(UPDATE_FAILURE_MESSAGE, response.getBody());
		verify(userRepo, new Times(0)).save(any());
	}
	
	@Test
	void testUpdateUserSuccess() {
		when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.of(USER));
		
		ResponseEntity<String> response = userController.updateUser(EMAIL, USER_REG_DTO);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(UPDATE_SUCCESS_MESSAGE, response.getBody());
		
		verify(userRepo, new Times(1)).save(any());
		
	}

	// Delete User
	@Test
	void testDeleteNonExistentUser() {
		when(userRepo.existsByEmail(EMAIL)).thenReturn(false);
		
		ResponseEntity<String> response = userController.deleteUser(EMAIL);
		
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals(DELETE_FAILURE_MESSAGE, response.getBody());
		verify(userRepo, new Times(0)).deleteByEmail(anyString());
	}
	
	@Test
	void testDeleteUserSuccess() {
		when(userRepo.existsByEmail(EMAIL)).thenReturn(true);
		
		ResponseEntity<String> response = userController.deleteUser(EMAIL);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(DELETE_SUCCESS_MESSAGE, response.getBody());
		verify(userRepo, new Times(1)).deleteByEmail(EMAIL);
		
	}

}

   