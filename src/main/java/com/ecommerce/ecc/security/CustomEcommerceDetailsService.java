package com.ecommerce.ecc.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ecommerce.ecc.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CustomEcommerceDetailsService implements UserDetailsService {

	private UserRepository userRepo;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return new CustomEcommerceDetails(userRepo.findByUsername(username)
				.orElseThrow(()-> new UsernameNotFoundException("User Not Found!!")));
	}

}
