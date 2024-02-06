package com.ecommerce.ecc.entity;

import com.ecommerce.ecc.enums.UserRole;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
@Entity
@Table(name = "Users")
@Inheritance(strategy = InheritanceType.JOINED) // here we are using is-a-relationship rather than has-a-relationship(old project)
public class User{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int userId;
	private String username;
	private String email;
	private String password;
	private UserRole userRole;
	private boolean isEmailVerified;
	private int isDeleted;

}
