package com.tus.individual.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tus.group_project.dao.UserRepository;
import com.tus.group_project.dto.UserDto;
import com.tus.group_project.dto.UserRegistrationDto;
import com.tus.group_project.dto.UserRegistrationResponse;
import com.tus.group_project.mapper.UserMapper;
import com.tus.group_project.model.User;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/users")
public class UserController {
	private UserRepository userRepo;
	private PasswordEncoder passwordEncoder;
	
	private static final String USER_NOT_FOUND_ERROR = "User with this email does not exist.";
	private static final String USER_UPDATED_MESSAGE = "User successfully updated.";
	private static final String USER_DELETED_MESSAGE = "User successfully deleted.";
	
	public UserController(UserRepository userRepo, PasswordEncoder passwordEncoder) {
		this.userRepo = userRepo;
		this.passwordEncoder = passwordEncoder;
	}
	
	@GetMapping
	@PreAuthorize("hasRole('ADMINISTRATOR')")
	public List<UserDto> getAllUsers() {
	    return StreamSupport.stream(userRepo.findAll().spliterator(), false)
	                        .map(user -> {
	                        	UserDto userDto = new UserDto();
	                        	UserMapper.toUserDto(user, userDto);
	                        	return userDto;
	                        })
	                        .toList();
	}
	
	@PostMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')") // Only admins can register users
	@Transactional
    public ResponseEntity<UserRegistrationResponse> createUser(@Valid @RequestBody UserRegistrationDto userRegDto) {
		String email = userRegDto.getEmail().trim().toLowerCase(); // Clear edge whitespace
		if (userRepo.existsByEmail(email)) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new UserRegistrationResponse(-1L));
		}
		
        User user = new User();
        UserMapper.toUser(userRegDto, user);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(userRegDto.getPassword())); // Encrypt password
        userRepo.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(new UserRegistrationResponse(user.getId()));
    }
	
	@GetMapping("/{email}")
	@PreAuthorize("hasRole('ADMINISTRATOR')")
	@Transactional
	public ResponseEntity<?> getUser(@Valid @PathVariable String email) {
	    email = email.trim().toLowerCase(); // Normalize email
	    
	    Optional<User> userOptional = userRepo.findByEmail(email);

	    if (userOptional.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                .body(USER_NOT_FOUND_ERROR);
	    }
	    
	    UserDto userDto = new UserDto();
	    UserMapper.toUserDto(userOptional.get(), userDto);
	    return ResponseEntity.ok(userDto);
	}

	
	@PutMapping("/{email}")
	@PreAuthorize("hasRole('ADMINISTRATOR')")
	@Transactional
	public ResponseEntity<String> updateUser(@PathVariable String email, @Valid @RequestBody UserRegistrationDto userRegDto) {
	    email = email.trim().toLowerCase(); // Normalize email

	    Optional<User> userOptional = userRepo.findByEmail(email);
	    if (userOptional.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(USER_NOT_FOUND_ERROR);
	    }

	    User user = userOptional.get();

	    // Prevent admin email from being modified, but allow password change
	    if ("admin@networksys.com".equalsIgnoreCase(email)) {
	        if (userRegDto.getPassword() == null || userRegDto.getPassword().isBlank()) {
	            return ResponseEntity.badRequest().body("Password cannot be empty for admin.");
	        }
	        user.setPassword(passwordEncoder.encode(userRegDto.getPassword())); // Only update password
	    } else {
	        // Allow full update for all other users
	        UserMapper.toUser(userRegDto, user);

	        // Only update password if it's provided
	        if (userRegDto.getPassword() != null && !userRegDto.getPassword().isBlank()) {
	            user.setPassword(passwordEncoder.encode(userRegDto.getPassword()));
	        }
	    }

	    userRepo.save(user);
	    return ResponseEntity.ok(USER_UPDATED_MESSAGE);
	}


	@DeleteMapping("/{email}")
	@PreAuthorize("hasRole('ADMINISTRATOR')")
	@Transactional
	public ResponseEntity<String> deleteUser(@Valid @PathVariable String email) {
	    email = email.trim().toLowerCase(); // Normalize email

	    if (!userRepo.existsByEmail(email)) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                .body(USER_NOT_FOUND_ERROR);
	    }

	    userRepo.deleteByEmail(email);
	    return ResponseEntity.ok(USER_DELETED_MESSAGE);
	}

}
