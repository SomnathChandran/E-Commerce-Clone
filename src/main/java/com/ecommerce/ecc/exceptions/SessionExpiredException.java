package com.ecommerce.ecc.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SessionExpiredException extends RuntimeException {
	private String message;
}
