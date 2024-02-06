package com.ecommerce.ecc.responsedto;

import com.ecommerce.ecc.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {

	private int userId;
	private String username;
	private String email;
	private UserRole userRole;
	private boolean isEmailVerified;
	private int isDeleted;

}
