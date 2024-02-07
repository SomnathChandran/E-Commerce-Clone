package com.ecommerce.ecc.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.ecc.entity.User;

public interface UserRepository extends JpaRepository<User,Integer>{



	boolean existsByEmail(String email);

	Optional<User> findByUsername(String string);
	
	List<User> findByIsEmailVerified(boolean b);


}
