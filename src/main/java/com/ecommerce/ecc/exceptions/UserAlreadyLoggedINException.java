package com.ecommerce.ecc.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserAlreadyLoggedINException extends RuntimeException {
	private String Message;
	
}
