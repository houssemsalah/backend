package com.itmsd.medical.controllers;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itmsd.medical.entities.Admin;
import com.itmsd.medical.entities.Dentiste;
import com.itmsd.medical.entities.Kine;
import com.itmsd.medical.entities.Medecin;
import com.itmsd.medical.entities.Patient;
import com.itmsd.medical.entities.Pharmacie;
import com.itmsd.medical.entities.Psychologue;
import com.itmsd.medical.entities.User;
import com.itmsd.medical.entities.Veterinaire;
import com.itmsd.medical.services.UserService;

import net.bytebuddy.utility.RandomString;

import com.itmsd.medical.payload.request.LoginRequest;
import com.itmsd.medical.payload.response.LoginResponse;
import com.itmsd.medical.repositories.AdminRepository;
import com.itmsd.medical.repositories.DentisteRepository;
import com.itmsd.medical.repositories.KineRepository;
import com.itmsd.medical.repositories.MedecinRepository;
import com.itmsd.medical.repositories.PatientRepository;
import com.itmsd.medical.repositories.PharmacieRepository;
import com.itmsd.medical.repositories.PsychologueRepository;
import com.itmsd.medical.repositories.RoleRepository;
import com.itmsd.medical.repositories.UserRepository;
import com.itmsd.medical.repositories.VeterinaireRepository;
import com.itmsd.medical.security.JwtUtils;

@Transactional
@RestController
@RequestMapping("auth")
public class AuthController {
	
	private AuthenticationManager authenticationManager;
	private UserService userService;	
	private UserController userController;
	private UserRepository userRepository;
	private RoleRepository roleRepository; 
	private MedecinRepository medecinRepository;	
	private PatientRepository patientRepository;	
	private DentisteRepository dentisteRepository;	
	private KineRepository kineRepository;	
	private PharmacieRepository pharmacieRepository;	
	private VeterinaireRepository veterinaireRepository;	
	private PsychologueRepository psychologueRepository;	
	private AdminRepository adminRepository;
	private JwtUtils jwtUtils;	
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	public AuthController(AuthenticationManager authenticationManager, UserService userService,UserRepository userRepository,
			RoleRepository roleRepository, MedecinRepository medecinRepository, PatientRepository patientRepository,
			DentisteRepository dentisteRepository, KineRepository kineRepository,
			PharmacieRepository pharmacieRepository, VeterinaireRepository veterinaireRepository,
			PsychologueRepository psychologueRepository, JwtUtils jwtUtils, BCryptPasswordEncoder bCryptPasswordEncoder,
			UserController userController,AdminRepository adminRepository) {
		super();
		this.authenticationManager = authenticationManager;
		this.userService = userService;
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.medecinRepository = medecinRepository;
		this.patientRepository = patientRepository;
		this.dentisteRepository = dentisteRepository;
		this.kineRepository = kineRepository;
		this.pharmacieRepository = pharmacieRepository;
		this.veterinaireRepository = veterinaireRepository;
		this.psychologueRepository = psychologueRepository;
		this.jwtUtils = jwtUtils;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.userController = userController;
		this.adminRepository = adminRepository;
		
	}

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response)
			throws Exception {
		String userEmail;
		User userByEmail = userService.findUserByEmail(loginRequest.getEmail().toLowerCase());
		
		if (userByEmail != null) {
			userEmail = userByEmail.getEmail().toLowerCase();
		} else return ResponseEntity.badRequest().body("User doesn't exist ! Enter Valid email !");
			//throw new RuntimeException("User doesn't exist ! Enter Valid email or username");
		if (userByEmail.isBanned()) throw new RuntimeException("Sorry you are banned from our platform !");
		if (!userByEmail.isActive())
			throw new RuntimeException("Your account is not Activated ! check your mail to active your account.");
		if (!bCryptPasswordEncoder.matches(loginRequest.getPassword(), userByEmail.getPassword())) throw new RuntimeException("Incorrect password !");
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(userEmail, loginRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		//response.setHeader("Authorization", jwt);
		userByEmail.setJwt(jwt);
		userByEmail.setOnline("online");
		userRepository.save(userByEmail);
		if (userByEmail.getRoles().contains(roleRepository.findByRole("patient"))) {
			LoginResponse res = new LoginResponse();
			Patient patient = patientRepository.findByEmail(userEmail);
			res.setId(patient.getId());
			res.setFirstName(patient.getFirstName());
			res.setLastName(patient.getLastName());
			res.setUsername(patient.getUsername());
			res.setCreatedAt(patient.getCreatedAt());
			res.setImageUrl(patient.getPhotoUrl());
			res.setStatus(patient.getOnline());
			res.setRoles(patient.getRoles());
			res.setJwt(jwt);
			return ResponseEntity.ok().body(res);
		}else if (userByEmail.getRoles().contains(roleRepository.findByRole("admin"))) {
			LoginResponse res = new LoginResponse();
			Admin admin = adminRepository.findByEmail(userEmail);
			
			res.setId(admin.getId());
			res.setFirstName(admin.getFirstName());
			res.setLastName(admin.getLastName());
			res.setUsername(admin.getUsername());
			res.setCreatedAt(admin.getCreatedAt());
			res.setImageUrl(admin.getPhotoUrl());
			res.setStatus(admin.getOnline());
			res.setRoles(admin.getRoles());
			res.setJwt(jwt);
		
			return ResponseEntity.ok().body(res);
		}else if (userByEmail.getRoles().contains(roleRepository.findByRole("medecin"))) {
			LoginResponse res = new LoginResponse();
			Medecin med = medecinRepository.findByEmail(userEmail);
			res.setId(med.getId());
			res.setFirstName(med.getFirstName());
			res.setLastName(med.getLastName());
			res.setUsername(med.getUsername());
			res.setSpeciality(med.getSpeciality());
			res.setCreatedAt(med.getCreatedAt());
			res.setImageUrl(med.getPhotoUrl());
			res.setStatus(med.getOnline());
			res.setRoles(med.getRoles());
			res.setJwt(jwt);
			return ResponseEntity.ok().body(res);
		}else if (userByEmail.getRoles().contains(roleRepository.findByRole("dentiste"))) {
			LoginResponse res = new LoginResponse();
			Dentiste med = dentisteRepository.findByEmail(userEmail);
			res.setId(med.getId());
			res.setFirstName(med.getFirstName());
			res.setLastName(med.getLastName());
			res.setUsername(med.getUsername());
			res.setSpeciality(med.getSpeciality());
			res.setCreatedAt(med.getCreatedAt());
			res.setImageUrl(med.getPhotoUrl());
			res.setStatus(med.getOnline());
			res.setRoles(med.getRoles());
			res.setJwt(jwt);
			return ResponseEntity.ok().body(res);
		}else if (userByEmail.getRoles().contains(roleRepository.findByRole("kine"))) {
			LoginResponse res = new LoginResponse();
			Kine med = kineRepository.findByEmail(userEmail);
			res.setId(med.getId());
			res.setFirstName(med.getFirstName());
			res.setLastName(med.getLastName());
			res.setUsername(med.getUsername());
			res.setSpeciality(med.getSpeciality());
			res.setCreatedAt(med.getCreatedAt());
			res.setImageUrl(med.getPhotoUrl());
			res.setStatus(med.getOnline());
			res.setRoles(med.getRoles());
			res.setJwt(jwt);
			return ResponseEntity.ok().body(res);
		}else if (userByEmail.getRoles().contains(roleRepository.findByRole("pharmacie"))) {
			LoginResponse res = new LoginResponse();
			Pharmacie med = pharmacieRepository.findByEmail(userEmail);
			res.setId(med.getId());
			res.setName(med.getName());
			res.setUsername(med.getUsername());
			res.setSpeciality(med.getSpeciality());
			res.setCreatedAt(med.getCreatedAt());
			res.setImageUrl(med.getPhotoUrl());
			res.setStatus(med.getOnline());
			res.setRoles(med.getRoles());
			res.setJwt(jwt);
			return ResponseEntity.ok().body(res);
		}else if (userByEmail.getRoles().contains(roleRepository.findByRole("veterinaire"))) {
			LoginResponse res = new LoginResponse();
			Veterinaire med = veterinaireRepository.findByEmail(userEmail);
			res.setId(med.getId());
			res.setFirstName(med.getFirstName());
			res.setLastName(med.getLastName());
			res.setUsername(med.getUsername());
			res.setSpeciality(med.getSpeciality());
			res.setCreatedAt(med.getCreatedAt());
			res.setImageUrl(med.getPhotoUrl());
			res.setStatus(med.getOnline());
			res.setRoles(med.getRoles());
			res.setJwt(jwt);
			return ResponseEntity.ok().body(res);
		}else if (userByEmail.getRoles().contains(roleRepository.findByRole("psychologue"))) {
			LoginResponse res = new LoginResponse();
			Psychologue med = psychologueRepository.findByEmail(userEmail);
			res.setId(med.getId());
			res.setFirstName(med.getFirstName());
			res.setLastName(med.getLastName());
			res.setUsername(med.getUsername());
			res.setSpeciality(med.getSpeciality());
			res.setCreatedAt(med.getCreatedAt());
			res.setImageUrl(med.getPhotoUrl());
			res.setStatus(med.getOnline());
			res.setRoles(med.getRoles());
			res.setJwt(jwt);
			return ResponseEntity.ok().body(res);
		}else return ResponseEntity.badRequest().body("Veuillez entrer des valides cordonnées !");
	
	}
	
	@PostMapping("/signup")
	public Patient createPatient(@Valid @RequestBody Patient user) throws IOException, Exception {
		User userExists = userService.findUserByEmail(user.getEmail().toLowerCase());
		user.setUsername(RandomString.make(7)+"1");
		if (!user.getPasswordConfirmation().equals(user.getPassword()))
			throw new RuntimeException("Password doesn't match");
		while (userService.findUserByUsername(user.getUsername().toLowerCase()) != null)
			{
			user.setUsername(RandomString.make(7)+"1");
			}
		if (userExists != null) {
			throw new RuntimeException("User exist !");
		} else {
			user.setEmail(user.getEmail().toLowerCase());
			user.setUsername(user.getUsername().toLowerCase());
			userService.saveUser(user,"patient");
			String message = "Hello, Can you activate your account with the following url "
					+ "You can log in : https://api.my-health-network.be/accounts/enable?confirmation="
					+ user.getConfirmationToken() + " \n Best Regards!";
			sendmail(user.getEmail(), message, "Activation account");
			return patientRepository.save(user);
		}
	}
	
	@PostMapping("/signupmedecin")
	public Medecin createMedecin(@Valid @RequestBody Medecin user) throws IOException, Exception {
		User userExists = userService.findUserByEmail(user.getEmail().toLowerCase());
		user.setUsername(RandomString.make(7)+"1");
		if (!user.getPasswordConfirmation().equals(user.getPassword()))
			throw new RuntimeException("Password dosn't match");
		while (userService.findUserByUsername(user.getUsername().toLowerCase()) != null)
		{
		user.setUsername(RandomString.make(7)+"1");
		}
		if (userExists != null) {
			throw new RuntimeException("User exist !");
		} else {
			user.setEmail(user.getEmail().toLowerCase());
			user.setUsername(user.getUsername().toLowerCase());
			userService.saveUser(user,"medecin");
			String message = "Hello, Can you activate your account with the following url "
					+ "You can log in : https://api.my-health-network.be/accounts/enable?confirmation="
					+ user.getConfirmationToken() + " \n Best Regards!";
			sendmail(user.getEmail(), message, "Activation account");
		return medecinRepository.save(user);
		}
	}
	@PostMapping("/signuppsychologue")
	public Psychologue createPsychologue(@Valid @RequestBody Psychologue user) throws IOException, Exception {
		User userExists = userService.findUserByEmail(user.getEmail().toLowerCase());
		user.setUsername(RandomString.make(7)+"1");
		if (!user.getPasswordConfirmation().equals(user.getPassword()))
			throw new RuntimeException("Password dosn't match");
		while (userService.findUserByUsername(user.getUsername().toLowerCase()) != null)
		{
		user.setUsername(RandomString.make(7)+"1");
		}
		if (userExists != null) {
			throw new RuntimeException("User exist !");
		} else {
			user.setEmail(user.getEmail().toLowerCase());
			user.setUsername(user.getUsername().toLowerCase());
			userService.saveUser(user,"psychologue");
			String message = "Hello, Can you activate your account with the following url "
					+ "You can log in : https://api.my-health-network.be/accounts/enable?confirmation="
					+ user.getConfirmationToken() + " \n Best Regards!";
			sendmail(user.getEmail(), message, "Activation account");
		return psychologueRepository.save(user);
		}
	}
	@PostMapping("/signupkine")
	public Kine createKine(@Valid @RequestBody Kine user) throws IOException, Exception {
		User userExists = userService.findUserByEmail(user.getEmail().toLowerCase());
		user.setUsername(RandomString.make(7)+"1");
		if (!user.getPasswordConfirmation().equals(user.getPassword()))
			throw new RuntimeException("Password dosn't match");
		while (userService.findUserByUsername(user.getUsername().toLowerCase()) != null)
		{
		user.setUsername(RandomString.make(7)+"1");
		}
		if (userExists != null) {
			throw new RuntimeException("User exist !");
		} else {
			user.setEmail(user.getEmail().toLowerCase());
			user.setUsername(user.getUsername().toLowerCase());
			userService.saveUser(user,"kine");
			String message = "Hello, Can you activate your account with the following url "
					+ "You can log in : https://api.my-health-network.be/accounts/enable?confirmation="
					+ user.getConfirmationToken() + " \n Best Regards!";
			sendmail(user.getEmail(), message, "Activation account");
		return kineRepository.save(user);
		}
	}
	
		@PostMapping("/signuppharmacie")
		public Pharmacie createPharmacie(@Valid @RequestBody Pharmacie user) throws IOException, Exception {
			User userExists = userService.findUserByEmail(user.getEmail().toLowerCase());
			user.setUsername(RandomString.make(7)+"1");
			if (!user.getPasswordConfirmation().equals(user.getPassword()))
				throw new RuntimeException("Password dosn't match");
			while (userService.findUserByUsername(user.getUsername().toLowerCase()) != null)
			{
			user.setUsername(RandomString.make(7)+"1");
			}
			if (userExists != null) {
				throw new RuntimeException("User exist !");
			} else {
				user.setEmail(user.getEmail().toLowerCase());
				user.setUsername(user.getUsername().toLowerCase());
				userService.saveUser(user,"pharmacie");
				String message = "Hello, Can you activate your account with the following url "
						+ "You can log in : https://api.my-health-network.be/accounts/enable?confirmation="
						+ user.getConfirmationToken() + " \n Best Regards!";
				sendmail(user.getEmail(), message, "Activation account");
			return pharmacieRepository.save(user);
			}
		}
	
		@PostMapping("/signupveterinaire")
		@Transactional
		public Veterinaire createPharmacie(@Valid @RequestBody Veterinaire user) throws IOException, Exception {
			User userExists = userService.findUserByEmail(user.getEmail().toLowerCase());
			user.setUsername(RandomString.make(7)+"1");
			if (!user.getPasswordConfirmation().equals(user.getPassword()))
				throw new RuntimeException("Password dosn't match");
			while (userService.findUserByUsername(user.getUsername().toLowerCase()) != null)
			{
			user.setUsername(RandomString.make(7)+"1");
			}
			if (userExists != null) {
				throw new RuntimeException("User exist !");
			} else {
				user.setEmail(user.getEmail().toLowerCase());
				user.setUsername(user.getUsername().toLowerCase());
				userService.saveUser(user,"veterinaire");
				String message = "Hello, Can you activate your account with the following url "
						+ "You can log in : https://api.my-health-network.be/accounts/enable?confirmation="
						+ user.getConfirmationToken() + " \n Best Regards!";
				sendmail(user.getEmail(), message, "Activation account");
			return veterinaireRepository.save(user);
			}
		}
	
	@PostMapping("/signupdentiste")
	public Dentiste createDentiste(@Valid @RequestBody Dentiste user) throws IOException, Exception {
		User userExists = userService.findUserByEmail(user.getEmail().toLowerCase());
		user.setUsername(RandomString.make(7)+"1");
		if (!user.getPasswordConfirmation().equals(user.getPassword()))
			throw new RuntimeException("Password dosn't match");
		while (userService.findUserByUsername(user.getUsername().toLowerCase()) != null)
		{
		user.setUsername(RandomString.make(7)+"1");
		}
		if (userExists != null) {
			throw new RuntimeException("User exist !");
		} else {
			user.setEmail(user.getEmail().toLowerCase());
			user.setUsername(user.getUsername().toLowerCase());
			userService.saveUser(user,"dentiste");
			String message = "Hello, Can you activate your account with the following url "
					+ "You can log in : https://api.my-health-network.be/accounts/enable?confirmation="
					+ user.getConfirmationToken() + " \n Best Regards!";
			sendmail(user.getEmail(), message, "Activation account");
		return dentisteRepository.save(user);
		}
	}
	@PutMapping("/logout")
	public String logOut(HttpServletRequest request) {
			String jwt = request.getHeader("Authorization").substring(7);
			Long userIdco = userController.userIdFromToken(jwt);
			User user = userRepository.findById(userIdco).get();
					user.setOnline("offline");
					userRepository.save(user);
		return ("User has been logged out !");
	}
	
	
	/*** Sending mail configuration ***/
	public void sendmail(String to, String message, String subject)
			throws MessagingException, IOException, javax.mail.MessagingException {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
				return new javax.mail.PasswordAuthentication("myhealthnetwork.service@gmail.com", "health2022");
			}
		});
		Message msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(to, false));
		msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
		msg.setSubject(subject);
		msg.setSentDate(new Date());
		msg.setText(message);
		Transport.send(msg);
	}
}
