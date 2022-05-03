package com.itmsd.medical.controllers;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.itmsd.medical.entities.User;
import com.itmsd.medical.repositories.UserRepository;
import com.itmsd.medical.services.UserService;

@Controller
@RequestMapping("/accounts/")
public class AccountController {
	private final UserRepository userRepository;
	private final UserService userService;
	
	@Autowired
	public AccountController(UserRepository userRepository, UserService userService) {
		this.userRepository = userRepository;
		this.userService = userService;
	}

	@RequestMapping("/enable")
	public ResponseEntity<?> enableUserAcount(@Param(value="confirmation") HttpServletRequest request,HttpServletResponse response) throws MessagingException, IOException {
		
		User user = userService.findUserByConfirmationToken(request.getParameter("confirmation"));
		if (user!=null)
		{user.setActive(true);
		user.setConfirmationToken(null);
		userRepository.save(user);
		String redirectUrl = "https://my-health-network.netlify.app";
		response.sendRedirect(redirectUrl);
		return ResponseEntity.ok("Votre compte a été activé avec succès !");
	}else throw new RuntimeException("No user token sent to activate");
	}
}
