package com.ecommerce.ecc.utility;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ecommerce.ecc.entity.AccessToken;
import com.ecommerce.ecc.entity.User;
import com.ecommerce.ecc.repository.AccessTokenRepo;
import com.ecommerce.ecc.repository.RefreshTokenRepo;
import com.ecommerce.ecc.repository.UserRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ScheduleJobs {
	
	private UserRepository userRepo;
	
	private AccessTokenRepo accessTokenRepo;
	
	private RefreshTokenRepo refreshTokenRepo;
	
	@Scheduled(fixedDelay = 1000l*120)
	public void cleanNonVerifiedUsers() {
		 List<User> dlist = userRepo.findByIsEmailVerified(false);
		 userRepo.deleteAll(dlist);
   }
	@Scheduled(fixedDelay = 1000l*60*60)
	public void clearExpiredTokens() {
		accessTokenRepo.deleteAll(accessTokenRepo.findByExpirationBefore(LocalDateTime.now()));
		refreshTokenRepo.deleteAll(refreshTokenRepo.findByExpirationBefore(LocalDateTime.now()));
	}
}
