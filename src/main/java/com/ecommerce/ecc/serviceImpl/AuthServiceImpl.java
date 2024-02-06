package com.ecommerce.ecc.serviceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ecommerce.ecc.entity.Seller;
import com.ecommerce.ecc.entity.User;
import com.ecommerce.ecc.repository.AuthRepository;
import com.ecommerce.ecc.repository.CustomerRepository;
import com.ecommerce.ecc.repository.SellerRepository;
import com.ecommerce.ecc.requestdto.UserRequestDto;
import com.ecommerce.ecc.responsedto.UserResponseDto;
import com.ecommerce.ecc.service.AuthService;
import com.ecommerce.ecc.utility.ResponseStructure;

@Service
public class AuthServiceImpl implements AuthService{
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private SellerRepository sellerRepository;
	
	@Autowired
	private AuthRepository authRepository;
	
	public <T extends User>T mapToUser(UserRequestDto request){    // in this the T-> is referring the actual data returning and the things inside<> is referring the DataType of T
	     return  null;
	}
	

	@Override
	public ResponseEntity<ResponseStructure<UserResponseDto>> addUser(UserRequestDto requestDto) {
			return null;
	}

}
