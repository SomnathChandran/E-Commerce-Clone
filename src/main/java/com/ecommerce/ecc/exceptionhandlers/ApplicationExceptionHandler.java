package com.ecommerce.ecc.exceptionhandlers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ecommerce.ecc.exceptions.InvalidUserRole;

@RestControllerAdvice
public class ApplicationExceptionHandler {
	
	public ResponseEntity<Object> structre(HttpStatus status, String message, Object rootCause) {
		return new ResponseEntity<Object>(
				Map.of(
				"status", status.value(),
				"message", message,
				"rootcause", rootCause),HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(InvalidUserRole.class)
	public ResponseEntity<Object> invalidUserRole(InvalidUserRole ex) {
		return structre(HttpStatus.BAD_REQUEST, ex.getMessage(), "Should Have Only 'SELLER' & 'CUSTOMER' Role Only!! ");
	}
}
