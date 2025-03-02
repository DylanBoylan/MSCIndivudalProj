package com.tus.individual.dto;

import com.tus.individual.model.Role;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDto {
	private String email;
	private Role role;
}
