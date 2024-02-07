package com.ecommerce.ecc.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UsernameAlreadyExistException extends RuntimeException {
	
	private String message;
}
