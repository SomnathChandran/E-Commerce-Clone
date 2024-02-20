
package com.ecommerce.ecc.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.ecc.requestdto.AuthRequest;
import com.ecommerce.ecc.requestdto.OtpModel;
import com.ecommerce.ecc.requestdto.UserRequestDto;
import com.ecommerce.ecc.responsedto.AuthResponse;
import com.ecommerce.ecc.responsedto.SimpleResponseStructure;
import com.ecommerce.ecc.responsedto.UserResponseDto;
import com.ecommerce.ecc.service.AuthService;
import com.ecommerce.ecc.utility.ResponseStructure;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
@RestController
@AllArgsConstructor
@CrossOrigin(allowCredentials = "true",origins = "http://localhost:5173/")  //CORS stands for Cross-Origin Resource Sharing.
@RequestMapping("/api/v1/ecommerce")
public class AuthController {
	
	
	private AuthService authService;
	
	@PostMapping("/users/register")
	public ResponseEntity<ResponseStructure<UserResponseDto>> registerUser(@RequestBody UserRequestDto requestDto){
		return authService.registerUser(requestDto);
	}
	
	@PostMapping("/verify-otp")
//	@PreAuthorize("hasAuthority('SELLER') OR hasAuthority('CUSTOMER')")
	public ResponseEntity<String> verifyOTP(@RequestBody OtpModel otpModel){
		return authService.verifyOTP(otpModel);
	}
	
	@PostMapping("/login")
//	@PreAuthorize("hasAuthority('SELLER') OR hasAuthority('CUSTOMER')")
	public ResponseEntity<ResponseStructure<AuthResponse>>login( @CookieValue(name = "rt",required = false) String rt,
			@CookieValue(name = "at",required = false) String at,@RequestBody AuthRequest authRequest , HttpServletResponse response){
		System.out.println("helo");
		return authService.login(rt,at,authRequest,response);
	}
	
	@PostMapping("/logout")
//	@PreAuthorize("hasAuthority('SELLER') OR hasAuthority('CUSTOMER')")
	public ResponseEntity<SimpleResponseStructure>logout(@CookieValue(name = "rt",required = false) String refreshToken,
			@CookieValue(name = "at",required = false) String accessToken, HttpServletResponse response){
		System.out.println("helo");
		return authService.logout(refreshToken,accessToken,response);
	}
	
	@PostMapping("/revoke-other-devices")
//	@PreAuthorize("hasAuthority('SELLER') OR hasAuthority('CUSTOMER')")
	public ResponseEntity<SimpleResponseStructure> revokeOther(@CookieValue(name = "at",required = false)String accessToken,@CookieValue(name="rt",required = false)String refreshToken){
		return authService.revokeOther(accessToken,refreshToken);
	}
	
	@PostMapping("/revoke-all-devices")
//	@PreAuthorize("hasAuthority('SELLER') OR hasAuthority('CUSTOMER')")
	public ResponseEntity<SimpleResponseStructure> revokeAll(@CookieValue(name = "at",required = false)String accessToken,@CookieValue(name="rt",required = false)String refreshToken ){
		return authService.revokeAll(accessToken,refreshToken);
	}
	
	@PostMapping("/refresh-login-token")
	public ResponseEntity<ResponseStructure<AuthResponse>>refreshLoginAndToken(@CookieValue(name = "at",required = false)String accessToken,@CookieValue(name="rt",required = false)String refreshToken,
			 HttpServletResponse response)
	{
		return authService.refreshLoginAndToken(accessToken,refreshToken,response);
	}

}
