package com.ecommerce.ecc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.ecc.entity.AccessToken;

public interface AccessTokenRepo extends JpaRepository<AccessToken,Long> {

}
