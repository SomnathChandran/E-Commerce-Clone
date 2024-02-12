package com.ecommerce.ecc.security;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ecommerce.ecc.entity.AccessToken;
import com.ecommerce.ecc.exceptions.AuthFailedException;
import com.ecommerce.ecc.exceptions.UserNotLoggedInException;
import com.ecommerce.ecc.repository.AccessTokenRepo;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
	
	private AccessTokenRepo accessRepo;
	private JwtService jwtService;
	private CustomEcommerceDetailsService detailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)throws ServletException, IOException {
	
		String at = null;
		String rt = null;
		
		Cookie[] cookies = request.getCookies();
		if(cookies != null) {
			for (Cookie cookie : cookies) {
				if(cookie.getName().equals("at")) at = cookie.getValue();
				if(cookie.getName().equals("rt")) rt = cookie.getValue();
			}
			String username = null;
			System.out.println("1");
			 if(at!=null && rt !=null) {
					System.out.println("2");
			 }
			 else{
				 throw new UserNotLoggedInException("User Not Looged IN!!");
			 }
			 Optional<AccessToken> accessToken = accessRepo.findByTokenAndIsBlocked(at,false);
				System.out.println("3");

			 if(accessToken == null)  throw new AuthFailedException("The Token Is NOt Present!!");
//				System.out.println("4");

			 
			 else{
				 log.info("Authenticating The Token!! ");
				 username =jwtService.extractUsername(at);
				if(username == null)        throw new RuntimeException();
				UserDetails userDetails = detailsService.loadUserByUsername(username);
				UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, null, userDetails.getAuthorities());
				 token.setDetails(new WebAuthenticationDetails(request));
				 SecurityContextHolder.getContext().setAuthentication(token);
				log.info("Authenticated Successfully!!");
			 }
		}
		 filterChain.doFilter(request, response);  // After complete the validation of user validation filter this line  will delegate the request to the next inbuilt filters
		
	}

}
