
package com.ecommerce.ecc.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.ecc.requestdto.AuthRequest;
import com.ecommerce.ecc.requestdto.OtpModel;
import com.ecommerce.ecc.requestdto.UserRequestDto;
import com.ecommerce.ecc.responsedto.AuthResponse;
import com.ecommerce.ecc.responsedto.UserResponseDto;
import com.ecommerce.ecc.service.AuthService;
import com.ecommerce.ecc.utility.ResponseStructure;

import jakarta.servlet.http.HttpServletResponse;
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
	
	@PostMapping("/login")
	public ResponseEntity<ResponseStructure<AuthResponse>>login( @RequestBody AuthRequest authRequest , HttpServletResponse response){
		return authService.login(authRequest,response);
	}
	
	@PostMapping("/logout")
	public ResponseEntity<ResponseStructure<String>>logout(@CookieValue(name = "rt",required = false) String refreshToken,
			@CookieValue(name = "at",required = false) String accessToken, HttpServletResponse response){
		return authService.logout(refreshToken,accessToken,response);
	}

}
