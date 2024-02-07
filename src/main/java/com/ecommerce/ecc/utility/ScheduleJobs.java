package com.ecommerce.ecc.utility;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ecommerce.ecc.entity.User;
import com.ecommerce.ecc.repository.UserRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ScheduleJobs {
	
	private UserRepository userRepo;
	
	@Scheduled(fixedDelay = 1000l*120)
	public void cleanNonVerifiedUsers() {
		 List<User> dlist = userRepo.findByIsEmailVerified(false);
		 userRepo.deleteAll(dlist);
   }
}
