package com.tus.individual.mapper;

import com.tus.individual.dto.UserDto;
import com.tus.individual.dto.UserRegistrationDto;
import com.tus.individual.model.User;

public class UserMapper {
	private UserMapper() {
		
	}
	
	public static void toUser(UserRegistrationDto userRegDto, User user) {
		user.setEmail(userRegDto.getEmail());
		user.setPassword(userRegDto.getPassword());
		user.setRole(userRegDto.getRole());
	}
	
	public static void toUserDto(User user, UserDto userDto) {
		userDto.setEmail(user.getEmail());
		userDto.setRole(user.getRole());
	}
}
