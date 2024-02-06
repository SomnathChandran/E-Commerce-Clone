package com.ecommerce.ecc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.ecc.entity.User;

public interface AuthRepository extends JpaRepository<User,Integer>{

}
