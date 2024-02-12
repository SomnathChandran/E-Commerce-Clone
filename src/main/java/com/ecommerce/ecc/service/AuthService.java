package com.ecommerce.ecc.service;

import org.springframework.http.ResponseEntity;

import com.ecommerce.ecc.requestdto.AuthRequest;
import com.ecommerce.ecc.requestdto.OtpModel;
import com.ecommerce.ecc.requestdto.UserRequestDto;
import com.ecommerce.ecc.responsedto.AuthResponse;
import com.ecommerce.ecc.responsedto.UserResponseDto;
import com.ecommerce.ecc.utility.ResponseStructure;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

	ResponseEntity<ResponseStructure<UserResponseDto>> addUser(UserRequestDto requestDto);

	ResponseEntity<String> verifyOTP(OtpModel otpModel);

	ResponseEntity<ResponseStructure<AuthResponse>> login(AuthRequest authRequest,HttpServletResponse response);

	ResponseEntity<ResponseStructure<String>> logout(String refreshToken,String accessToken, HttpServletResponse response);

}
