
package com.ecommerce.ecc.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.ecc.requestdto.OtpModel;
import com.ecommerce.ecc.requestdto.UserRequestDto;
import com.ecommerce.ecc.responsedto.UserResponseDto;
import com.ecommerce.ecc.service.AuthService;
import com.ecommerce.ecc.utility.ResponseStructure;

import lombok.AllArgsConstructor;
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/ecommerce")
public class AuthController {
	
	
	private AuthService authService;
	
	@PostMapping("/users/register")
	public ResponseEntity<ResponseStructure<UserResponseDto>> addUser(@RequestBody UserRequestDto requestDto){
		return authService.addUser(requestDto);
	}
	
	@PostMapping("/verify-otp")
	public ResponseEntity<String> verifyOTP(@RequestBody OtpModel otpModel){
		return authService.verifyOTP(otpModel);
	}
}
