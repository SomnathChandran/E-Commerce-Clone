package com.ecommerce.ecc.serviceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecommerce.ecc.entity.Customer;
import com.ecommerce.ecc.entity.Seller;
import com.ecommerce.ecc.entity.User;
import com.ecommerce.ecc.exceptions.InvalidUserRole;
import com.ecommerce.ecc.exceptions.UsernameAlreadyExistException;
import com.ecommerce.ecc.repository.CustomerRepository;
import com.ecommerce.ecc.repository.SellerRepository;
import com.ecommerce.ecc.repository.UserRepository;
import com.ecommerce.ecc.requestdto.UserRequestDto;
import com.ecommerce.ecc.responsedto.UserResponseDto;
import com.ecommerce.ecc.service.AuthService;
import com.ecommerce.ecc.utility.ResponseStructure;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService{
	
	private PasswordEncoder encoder;
	
	private CustomerRepository customerRepository;
	
	private SellerRepository sellerRepository;
	
	private UserRepository userRepository;
	
	private ResponseStructure<UserResponseDto> structure;
	
	public <T extends User>T mapToUser(UserRequestDto request){    // in this the T-> is referring the actual data returning and the things inside<> is referring the DataType of T
	    
		User user =null;
		switch (request.getUserRole()) {
		case SELLER:{
			user = new Seller();
		}
		break;
		case CUSTOMER:{
			user = new Customer();
		}
		break;
		default:
			throw new InvalidUserRole("The Given User Role Is Not Matching");
		}
		user.setUserRole(request.getUserRole());
		user.setEmail(request.getEmail());
		user.setPassword(encoder.encode(request.getPassword()));
		user.setUsername(request.getEmail().split("@")[0]);
		return (T) user;
		
	}
	
	private  <T extends User>T saveUser(User user) {
		User user1 = null;
		if(user instanceof Seller) {
			 user1 = sellerRepository.save((Seller)user);
		}else {
			user1 =customerRepository.save((Customer)user);
		}
		return (T)user1;
		
	}

	private UserResponseDto mapToResponseDto(User user) {
		return UserResponseDto.builder()
		.userId(user.getUserId())
		.username(user.getUsername())
		.email(user.getEmail())
		.isDeleted(user.getIsDeleted())
		.isEmailVerified(user.isEmailVerified())
		.userRole(user.getUserRole())
		.build();
	}
	

	@Override
	public ResponseEntity<ResponseStructure<UserResponseDto>> addUser(UserRequestDto userRequestDto) {
		
		User user = userRepository.findByUsername(userRequestDto.getEmail().split("@")[0]).map(u -> {
			if(u.isEmailVerified()) {
				throw new UsernameAlreadyExistException("Email Should Be Unique !! NO Duplicate");
			}else {
				System.out.println("Otp Generation");
			}
			return u;
		}).orElseGet(saveUser(mapToUser(userRequestDto)));
		return new ResponseEntity<ResponseStructure<UserResponseDto>>(structure.setStatus(HttpStatus.ACCEPTED.value())
				.setMessage("User Successfully Saved and need to verifiy by otp..")
				.setData(mapToResponseDto(user)),HttpStatus.ACCEPTED);
			
	}

}
