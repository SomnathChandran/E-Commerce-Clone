package com.ecommerce.ecc.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.ecc.entity.RefreshToken;
import com.ecommerce.ecc.entity.User;

public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {

	Optional<RefreshToken> findByToken(String rt);

	List<RefreshToken> findByExpirationBefore(LocalDateTime expiry);

	List<RefreshToken> findAllByUserAndIsBlockedAndTokenNot(User user, boolean b, String refreshToken);

	List<RefreshToken> findByTokenAndIsBlocked(User user, boolean b);

}
