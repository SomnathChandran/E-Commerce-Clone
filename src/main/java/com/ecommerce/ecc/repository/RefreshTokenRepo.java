package com.ecommerce.ecc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.ecc.entity.RefreshToken;
import com.ecommerce.ecc.entity.User;

public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {

	Optional<RefreshToken> findByToken(String rt);

}
