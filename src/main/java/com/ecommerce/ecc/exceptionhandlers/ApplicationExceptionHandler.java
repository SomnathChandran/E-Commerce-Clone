package com.ecommerce.ecc.exceptionhandlers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ecommerce.ecc.exceptions.AuthFailedException;
import com.ecommerce.ecc.exceptions.InvalidOtpException;
import com.ecommerce.ecc.exceptions.InvalidUserRole;
import com.ecommerce.ecc.exceptions.OtpExpiredException;
import com.ecommerce.ecc.exceptions.SessionExpiredException;
import com.ecommerce.ecc.exceptions.UserAlreadyLoggedINException;
import com.ecommerce.ecc.exceptions.UserNotLoggedInException;
import com.ecommerce.ecc.exceptions.UsernameAlreadyExistException;

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
	@ExceptionHandler(UsernameAlreadyExistException.class)
	public ResponseEntity<Object> usernameAlreadyExistException(UsernameAlreadyExistException ex) {
		return structre(HttpStatus.BAD_REQUEST, ex.getMessage(), "The Given Email Should Be Unique To All User");
	}
	@ExceptionHandler(InvalidOtpException.class)
	public ResponseEntity<Object> invalidOtpException(InvalidOtpException ex) {
		return structre(HttpStatus.BAD_REQUEST, ex.getMessage(), "The Given OTP Should Be Same as The generated Otp");
	}
	@ExceptionHandler(SessionExpiredException.class)
	public ResponseEntity<Object> sessionExpiredException(SessionExpiredException ex) {
		return structre(HttpStatus.BAD_REQUEST, ex.getMessage(), "Need To Verify Very Fast Within 5 Mins");
	}
	@ExceptionHandler(OtpExpiredException.class)
	public ResponseEntity<Object> otpExpiredException(OtpExpiredException ex) {
		return structre(HttpStatus.BAD_REQUEST, ex.getMessage(), "The Given OTP is Expired Because of taking long time Do it Fast");
	}
	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<Object> usernameNotFoundException(UsernameNotFoundException ex) {
		return structre(HttpStatus.BAD_REQUEST, ex.getMessage(), "The Username Or PassWord IS Incorrect please Check IT..");
	}
	@ExceptionHandler(UserNotLoggedInException.class)
	public ResponseEntity<Object> userNotLoggedInException(UserNotLoggedInException ex) {
		return structre(HttpStatus.BAD_REQUEST, ex.getMessage(), "The User Is Not Logged In,User Must Logged In Before Logout ");
	}
	@ExceptionHandler(AuthFailedException.class)
	public ResponseEntity<Object> authFailedException(AuthFailedException ex) {
		return structre(HttpStatus.BAD_REQUEST, ex.getMessage(), "The User Authentication Is Failed ");
	}
	@ExceptionHandler(UserAlreadyLoggedINException.class)
	public ResponseEntity<Object> serAlreadyLoggedINException(UserAlreadyLoggedINException ex) {
		return structre(HttpStatus.BAD_REQUEST, ex.getMessage(), "The User Authentication Is Failed,Because User Already Logged IN..!! ");
	}
}
