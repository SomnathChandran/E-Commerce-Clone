package com.ecommerce.ecc.entity;

import com.ecommerce.ecc.enums.UserRole;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Users")
@Inheritance(strategy = InheritanceType.JOINED) // here we are using is-a-relationship rather than has-a-relationship(old project)
public class User{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int userId;
	private String username;
	private String email;
	private String password;
	@Enumerated(EnumType.STRING)
	private UserRole userRole;
	private boolean isEmailVerified;
	private int isDeleted;
	
	

}
