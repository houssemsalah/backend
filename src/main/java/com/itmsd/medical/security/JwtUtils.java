package com.itmsd.medical.security;

import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.itmsd.medical.security.JwtUtils;
import com.itmsd.medical.services.UserDetailsImpl;

@Component
public class JwtUtils {

	private String jwtSecret="medicalSecret";

	private int jwtExpirationMs = 10*24*3600*1000;
	
	public String generateJwtToken(Authentication authentication) {

		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		Algorithm algo = Algorithm.HMAC256(jwtSecret);
		return 
		JWT.create().withSubject(userPrincipal.getUsername())
				 	.withIssuedAt(new Date(System.currentTimeMillis()))
				 	.withClaim("roles",userPrincipal.getAuthorities().stream().map(ga->ga.getAuthority()).collect(Collectors.toList()))
				 	.withExpiresAt(new Date((new Date()).getTime() + jwtExpirationMs))
				 	.sign(algo);
	}
}