package com.ecommerce.ecc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.ecc.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer,Integer>{

}
