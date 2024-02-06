package com.ecommerce.ecc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.ecc.requestdto.UserRequestDto;
import com.ecommerce.ecc.responsedto.UserResponseDto;
import com.ecommerce.ecc.service.AuthService;
import com.ecommerce.ecc.utility.ResponseStructure;

@RestController
public class AuthController {
	
	@Autowired
	private AuthService authService;

	@PostMapping("/users")
	public ResponseEntity<ResponseStructure<UserResponseDto>>addUser(@RequestBody UserRequestDto requestDto){
		return authService.addUser(requestDto);
	}
}
