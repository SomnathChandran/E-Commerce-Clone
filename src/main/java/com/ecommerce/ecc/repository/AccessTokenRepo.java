package com.ecommerce.ecc.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.ecc.entity.AccessToken;
import com.ecommerce.ecc.entity.User;

public interface AccessTokenRepo extends JpaRepository<AccessToken,Long> {

	Optional<AccessToken> findByToken(String at);

	List<AccessToken> findByTokenAndIsBlocked(User user, boolean b);

//	Optional<AccessToken> findAllByIsExpiration(LocalDateTime now);

	List<AccessToken> findByExpirationBefore(LocalDateTime expiry);

	List<AccessToken> findAllByUserAndIsBlockedAndTokenNot(User user, boolean b, String accessToken);

	Optional<AccessToken> findByTokenAndIsBlocked(String at, boolean b);
	

}
