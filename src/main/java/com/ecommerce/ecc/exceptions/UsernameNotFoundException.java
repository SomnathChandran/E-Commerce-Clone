package com.ecommerce.ecc.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UsernameNotFoundException extends RuntimeException {
	private String message;

}
