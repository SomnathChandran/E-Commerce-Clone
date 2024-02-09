package com.ecommerce.ecc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.ecc.entity.RefreshToken;

public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {

}
