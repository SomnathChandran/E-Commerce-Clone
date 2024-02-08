package com.ecommerce.ecc.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OtpExpiredException extends RuntimeException {
	private String message;
	
}
