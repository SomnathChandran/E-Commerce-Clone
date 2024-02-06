package com.ecommerce.ecc.requestdto;

import org.springframework.stereotype.Component;

import com.ecommerce.ecc.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {
	
	private String email;
	private String password;
	private UserRole userRole;
	

}
