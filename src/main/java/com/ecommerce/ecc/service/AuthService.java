package com.ecommerce.ecc.service;

import org.springframework.http.ResponseEntity;

import com.ecommerce.ecc.requestdto.UserRequestDto;
import com.ecommerce.ecc.responsedto.UserResponseDto;
import com.ecommerce.ecc.utility.ResponseStructure;

public interface AuthService {

	ResponseEntity<ResponseStructure<UserResponseDto>> addUser(UserRequestDto requestDto);

}
