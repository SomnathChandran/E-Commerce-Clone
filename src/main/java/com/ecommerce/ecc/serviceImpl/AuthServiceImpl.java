package com.ecommerce.ecc.serviceImpl;
import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecommerce.ecc.cache.CacheStore;
import com.ecommerce.ecc.entity.Customer;
import com.ecommerce.ecc.entity.Seller;
import com.ecommerce.ecc.entity.User;
import com.ecommerce.ecc.exceptions.InvalidOtpException;
import com.ecommerce.ecc.exceptions.InvalidUserRole;
import com.ecommerce.ecc.exceptions.OtpExpiredException;
import com.ecommerce.ecc.exceptions.SessionExpiredException;
import com.ecommerce.ecc.repository.CustomerRepository;
import com.ecommerce.ecc.repository.SellerRepository;
import com.ecommerce.ecc.repository.UserRepository;
import com.ecommerce.ecc.requestdto.OtpModel;
import com.ecommerce.ecc.requestdto.UserRequestDto;
import com.ecommerce.ecc.responsedto.UserResponseDto;
import com.ecommerce.ecc.service.AuthService;
import com.ecommerce.ecc.utility.MessageStructure;
import com.ecommerce.ecc.utility.ResponseStructure;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService{

	private PasswordEncoder encoder;

	private CustomerRepository customerRepo;

	private SellerRepository sellerRepo;

	private UserRepository userRepo;

	private ResponseStructure<UserResponseDto> structure;

	private CacheStore<String> otpCacheStore;

	private CacheStore<User> userCacheStore;

	private JavaMailSender javaMailSender;



	@Override
	public ResponseEntity<ResponseStructure<UserResponseDto>> addUser(UserRequestDto userRequestDto) {

		if(userRepo.existsByEmail(userRequestDto.getEmail())) 
			throw new IllegalArgumentException("The Email Already present with the gven Email");
		String OTP = otpGeneration();
		User user = mapToUser(userRequestDto);
		userCacheStore.add(userRequestDto.getEmail(), user);
		otpCacheStore.add(userRequestDto.getEmail(),OTP);
		try {
			sendOtpToMail(user, OTP);
		} catch (MessagingException e) {
			log.error("The Email Address Doesn't Exist");
		}
		return new ResponseEntity<ResponseStructure<UserResponseDto>>(structure.setStatus(HttpStatus.ACCEPTED.value())
				.setMessage("User Successfully Saved and need to verifiy by otp... The OTP is sent on Yore EmailID")
				.setData(mapToResponseDto(user)),HttpStatus.ACCEPTED);

	}

	@Override
	public ResponseEntity<String> verifyOTP(OtpModel otpModel) {

		User user = userCacheStore.get(otpModel.getEmail());
		String otp = otpCacheStore.get(otpModel.getEmail());

		if(otp == null) throw new OtpExpiredException("The OTP is Expired Try Once Again");
		if(user == null) throw new SessionExpiredException("The Registration Session is Expired");
		if(!otp.equals(otpModel.getOtp()))throw new InvalidOtpException("The Given OTP is Invalid");

		user.setEmailVerified(true);
		userRepo.save(user);
		try {
			sendResponseToMail(user);
		} catch (MessagingException e) {
			log.error("Process Incomplete Because Due To Miss Consumption");
		}

		return new  ResponseEntity<String>("User Registration Successfully Completed!!", HttpStatus.CREATED);
	}

	private void sendOtpToMail(User user ,String otp) throws MessagingException{


		sendMail( MessageStructure.builder()
				.to(user.getEmail())
				.subject("Complete Your Registration to E-Commerce Api")
				.sentDate(new Date())
				.text(
						"hey, "+user.getUsername()
						+"<h3>Good To See You Intrested in Our E-Commerce Api,<h3>"
						+"<h3>Complete Your Registration Using the OTP<h3> <br>"
						+"<h1>"+otp+"<h1><br>"
						+"<h3>Note: The Method Is Expired In 5 Minutes<h3>"
						+"<br><br>"
						+"<h3>With Best Regards<h3><br>"
						+"<h1>E-Commerce Api<h1>"
						).build());
	}
	
	private void sendResponseToMail(User user) throws MessagingException {
		
		sendMail( MessageStructure.builder()
				.to(user.getEmail())
				.subject("Welcome to E-Commerce Api!")
				.sentDate(new Date())
				.text(
						"Dear, "+"<h2>"+user.getUsername()+"<h2>"
						+"<h3>Congratulations! 🎉..,Good To See You Intrested in Our E-Commerce Api,<h3>"
						+"<h3>Sucessfully Completed Your Registration to E-Commerce Api<h3> <br>"
						+"<h3>Your email has been successfully verified, and you're now officially registered with E-Commerce Api.<h3>"
						+"<br>"
						+"<h3>Let's get started on your e-commerce journey! 🚀<h3>"
						+"<br>"
						+"<h3>With Best Regards<h3><br>"
						+"<h2>Mr.Somnath<h2>"
						+"<h1>E-Commerce Api<h1>"
						).build());
		
	}

	@Async
	private void sendMail(MessageStructure message) throws MessagingException{
		MimeMessage mimeMessage =javaMailSender.createMimeMessage();
		MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
		messageHelper.setTo(message.getTo());
		messageHelper.setSubject(message.getSubject());
		messageHelper.setSentDate(message.getSentDate());
		messageHelper.setText(message.getText(),true); 
		javaMailSender.send(mimeMessage);
	}



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
			user1 = sellerRepo.save((Seller)user);
		}else {
			user1 =customerRepo.save((Customer)user);
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

	private String otpGeneration() {

		return ""+(int) (100000 + Math.random() * 999999);
		// Another Way => String.valueOf(new Random().nextInt(100000,999999)); 
	}

}
