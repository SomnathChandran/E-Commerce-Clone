package com.ecommerce.ecc.serviceImpl;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecommerce.ecc.cache.CacheStore;
import com.ecommerce.ecc.entity.AccessToken;
import com.ecommerce.ecc.entity.Customer;
import com.ecommerce.ecc.entity.RefreshToken;
import com.ecommerce.ecc.entity.Seller;
import com.ecommerce.ecc.entity.User;
import com.ecommerce.ecc.exceptions.AuthFailedException;
import com.ecommerce.ecc.exceptions.InvalidOtpException;
import com.ecommerce.ecc.exceptions.InvalidUserRole;
import com.ecommerce.ecc.exceptions.OtpExpiredException;
import com.ecommerce.ecc.exceptions.SessionExpiredException;
import com.ecommerce.ecc.exceptions.UserNotLoggedInException;
import com.ecommerce.ecc.exceptions.UsernameAlreadyExistException;
import com.ecommerce.ecc.repository.AccessTokenRepo;
import com.ecommerce.ecc.repository.CustomerRepository;
import com.ecommerce.ecc.repository.RefreshTokenRepo;
import com.ecommerce.ecc.repository.SellerRepository;
import com.ecommerce.ecc.repository.UserRepository;
import com.ecommerce.ecc.requestdto.AuthRequest;
import com.ecommerce.ecc.requestdto.OtpModel;
import com.ecommerce.ecc.requestdto.UserRequestDto;
import com.ecommerce.ecc.responsedto.AuthResponse;
import com.ecommerce.ecc.responsedto.SimpleResponseStructure;
import com.ecommerce.ecc.responsedto.UserResponseDto;
import com.ecommerce.ecc.security.JwtService;
import com.ecommerce.ecc.service.AuthService;
import com.ecommerce.ecc.utility.CookieManager;
import com.ecommerce.ecc.utility.MessageStructure;
import com.ecommerce.ecc.utility.ResponseStructure;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Service
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
	private AuthenticationManager authenticationManager;
	private CookieManager cookieManager;
	private JwtService jwtService;
	private AccessTokenRepo accessTokenRepo;
	private RefreshTokenRepo refreshTokenRepo;
	private ResponseStructure<AuthResponse> authResponse;
	private ResponseStructure<SimpleResponseStructure> simpleStructure;

	@Value("${myapp.access.expiry}")
	private int accessExpirationInSeconds;

	@Value("${myapp.refresh.expiry}")
	private int refreshExpirationInSeconds;


	public AuthServiceImpl(PasswordEncoder encoder, CustomerRepository customerRepo, SellerRepository sellerRepo,
			UserRepository userRepo, ResponseStructure<UserResponseDto> structure, CacheStore<String> otpCacheStore,
			CacheStore<User> userCacheStore, JavaMailSender javaMailSender, AuthenticationManager authenticationManager,
			CookieManager cookieManager, JwtService jwtService,AccessTokenRepo accessTokenRepo,RefreshTokenRepo refreshTokenRepo
			,ResponseStructure<AuthResponse> authResponse, ResponseStructure<String> rs, ResponseStructure<SimpleResponseStructure> simpleStructure) {
		super();
		this.encoder = encoder;
		this.customerRepo = customerRepo;
		this.sellerRepo = sellerRepo;
		this.userRepo = userRepo;
		this.structure = structure;
		this.otpCacheStore = otpCacheStore;
		this.userCacheStore = userCacheStore;
		this.javaMailSender = javaMailSender;
		this.authenticationManager = authenticationManager;
		this.cookieManager = cookieManager;
		this.jwtService = jwtService;
		this.accessTokenRepo = accessTokenRepo;
		this.refreshTokenRepo = refreshTokenRepo;
		this.authResponse = authResponse;
		this.simpleStructure = simpleStructure;

	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponseDto>> addUser(UserRequestDto userRequestDto) {

		if(userRepo.existsByEmail(userRequestDto.getEmail())) 
			throw new UsernameAlreadyExistException("The Email Already present with the given Email");
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

	@Override
	public ResponseEntity<ResponseStructure<AuthResponse>> login(AuthRequest authRequest,HttpServletResponse response) {
		String username = authRequest.getEmail().split("@")[0];
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, authRequest.getPassword());
		Authentication authenticate = authenticationManager.authenticate(token);
		if(!authenticate.isAuthenticated()) throw new UsernameNotFoundException("Failed To Authenticate The User ");
		else {
			// generating The Cookies AuthResponse And Returning To The Client
			return userRepo.findByUsername(username).map(user ->{
				grantAccess(response, user);
				return ResponseEntity.ok(
						authResponse.setStatus(HttpStatus.OK.value())
						.setData(AuthResponse.builder()
								.userId(user.getUserId())
								.username(user.getUsername())
								.role(user.getUserRole().name())
								.isAuthenticated(true)
								.accessExpiration(LocalDateTime.now().plusSeconds(accessExpirationInSeconds))
								.refreshExpiration(LocalDateTime.now().plusSeconds(refreshExpirationInSeconds))
								.build())
						.setMessage("Your Logged IN Successfully!!"));
			}).get();
		}



	}

	@Override
	public ResponseEntity<ResponseStructure<SimpleResponseStructure>> logout(String rt,String at, HttpServletResponse response) {
		
		if(rt==null && at==null) {throw new UserNotLoggedInException("The User Must And Should Login Before Logout!!");}
		
		accessTokenRepo.findByToken(at).ifPresent(accessToken ->{
			accessToken.setBlocked(true);
			accessTokenRepo.save(accessToken);
		});
		refreshTokenRepo.findByToken(rt).ifPresent(refreshToken ->{
			refreshToken.setBlocked(true);
			refreshTokenRepo.save(refreshToken);
		});
		
		response.addCookie(cookieManager.invalidate(new Cookie("at","")));
		response.addCookie(cookieManager.invalidate(new Cookie("rt", "")));
		
		simpleStructure.setStatus(HttpStatus.OK.value());
		simpleStructure.setMessage("SuccessFully Logged Out!!");
//		simpleStructure.setData(response);
		
			return new ResponseEntity<ResponseStructure<SimpleResponseStructure>>(simpleStructure,HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<SimpleResponseStructure> revokeOther(String accessToken,String refreshToken ,HttpServletResponse httpServletResponse)
		{
		String userName = SecurityContextHolder.getContext().getAuthentication().getName();

		if(userName!=null)
		{
			userRepo.findByUsername(userName).ifPresent(user->{
				blockAccessTokens(accessTokenRepo.findAllByUserAndIsBlockedAndTokenNot(user,false,accessToken));
				blockRefreshTokens(refreshTokenRepo.findAllByUserAndIsBlockedAndTokenNot(user,false,refreshToken));
			});

			SimpleResponseStructure structure=new SimpleResponseStructure();
			structure.setStatus(HttpStatus.OK.value());
			structure.setMessage("Logged out from all other Devices..!!!");

			return new ResponseEntity<SimpleResponseStructure>(structure,HttpStatus.OK);
		}
		throw new AuthFailedException("User Not Authenticated And User Not Present");
			
	}
	
	
	@Override
	public ResponseEntity<SimpleResponseStructure> revokeAll(String accessToken, String refreshToken,HttpServletResponse httpServletResponse) {
		String userName = SecurityContextHolder.getContext().getAuthentication().getName();

		if(userName!=null)
		{
			userRepo.findByUsername(userName).ifPresent(user->{
				blockAccessTokens(accessTokenRepo.findByTokenAndIsBlocked(user,false));
				blockRefreshTokens(refreshTokenRepo.findByTokenAndIsBlocked(user,false));
			});

			SimpleResponseStructure structure=new SimpleResponseStructure();
			structure.setStatus(HttpStatus.OK.value());
			structure.setMessage("Logged out from all  Devices..!!!");

			return new ResponseEntity<SimpleResponseStructure>(structure,HttpStatus.OK);
		}
		throw new AuthFailedException("User Not Authenticated & User Not Present");
	}
		
	
	
	// --------------==================-------================-------------------=======================-------------=============-------------===

	private void blockAccessTokens(List<AccessToken>accessToken) {
		accessToken.forEach(at ->{
			at.setBlocked(true);
			accessTokenRepo.save(at);
		});
	}
	private void blockRefreshTokens(List<RefreshToken>refreshToken) {
		refreshToken.forEach(rt ->{
			rt.setBlocked(true);
			refreshTokenRepo.save(rt);
		});
	}
	
	
	private void grantAccess(HttpServletResponse servletResponse,User user) {
		// generate Access ANd Refresh Tokens
		String accessToken = jwtService.generateAccessToken(user.getUsername());
		String refreshToken = jwtService.generateRefreshToken(user.getUsername());
		//adding the access and refresh tokens cookies to the Response
		servletResponse.addCookie(cookieManager.configure(new Cookie("at",accessToken),accessExpirationInSeconds));
		servletResponse.addCookie(cookieManager.configure(new Cookie("rt",refreshToken),refreshExpirationInSeconds));

		//saving the access and refresh tokens cookie in the Database
		accessTokenRepo.save(AccessToken.builder()
				.token(accessToken)
				.user(user)
				.isBlocked(false)
				.expiration(LocalDateTime.now().plusSeconds(accessExpirationInSeconds))
				.build()
				);

		refreshTokenRepo.save(RefreshToken.builder()
				.token(refreshToken)
				.user(user)
				.expiration(LocalDateTime.now().plusSeconds(refreshExpirationInSeconds))
				.isBlocked(false)
				.build()
				);


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
