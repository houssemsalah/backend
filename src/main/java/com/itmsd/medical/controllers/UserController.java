package com.itmsd.medical.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itmsd.medical.repositories.DentisteRepository;
import com.itmsd.medical.repositories.KineRepository;
import com.itmsd.medical.repositories.MedecinRepository;
import com.itmsd.medical.repositories.MessageRepository;
import com.itmsd.medical.repositories.PatientRepository;
import com.itmsd.medical.repositories.PharmacieRepository;
import com.itmsd.medical.repositories.PsychologueRepository;
import com.itmsd.medical.repositories.UserRepository;
import com.itmsd.medical.repositories.VeterinaireRepository;
import com.itmsd.medical.services.UserService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.itmsd.medical.entities.Dentiste;
import com.itmsd.medical.entities.Kine;
import com.itmsd.medical.entities.Medecin;
import com.itmsd.medical.entities.Message;
import com.itmsd.medical.entities.Patient;
import com.itmsd.medical.entities.Pharmacie;
import com.itmsd.medical.entities.Psychologue;
import com.itmsd.medical.entities.RendezVous;
import com.itmsd.medical.entities.User;
import com.itmsd.medical.entities.Veterinaire;
import com.itmsd.medical.payload.request.EditPhotosCabinet;
import com.itmsd.medical.payload.request.Personne;
import com.itmsd.medical.payload.request.UpdatePassword;
import com.itmsd.medical.payload.response.PersonnelRdvs;

@RestController
@RequestMapping({ "/users" })
@CrossOrigin(origins = "*")
public class UserController {
	
	private UserRepository userRepository;
	private UserService userService;
	private MedecinRepository medecinRepository;
	private PatientRepository patientRepository;
	private DentisteRepository dentisteRepository;	
	private KineRepository kineRepository;	
	private PharmacieRepository pharmacieRepository;	
	private VeterinaireRepository veterinaireRepository;
	private PsychologueRepository psychologueRepository;
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	private MessageRepository messageRepository;
	
	@Autowired
	public UserController(UserRepository userRepository, UserService userService, MedecinRepository medecinRepository,
			PatientRepository patientRepository, DentisteRepository dentisteRepository, KineRepository kineRepository,
			PharmacieRepository pharmacieRepository, VeterinaireRepository veterinaireRepository,
			PsychologueRepository psychologueRepository, BCryptPasswordEncoder bCryptPasswordEncoder,
			MessageRepository messageRepository) {
		super();
		this.userRepository = userRepository;
		this.userService = userService;
		this.medecinRepository = medecinRepository;
		this.patientRepository = patientRepository;
		this.dentisteRepository = dentisteRepository;
		this.kineRepository = kineRepository;
		this.pharmacieRepository = pharmacieRepository;
		this.veterinaireRepository = veterinaireRepository;
		this.psychologueRepository = psychologueRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.messageRepository = messageRepository;
	}

	@DeleteMapping("/delete/message/{messageId}")
	public String deleteMessage(@PathVariable(value="messageId") Long messageId,HttpServletRequest request) {
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userIdFromToken(jwt);
		User user = userRepository.findById(userIdco).get();
		Message msg = messageRepository.findById(messageId).get();
		if (msg.getUsername().contains(user.getUsername()) || user.getRoles().stream().findFirst().get().getRole().contains("admin")) {
			
			messageRepository.delete(msg);
		}else 
		throw new RuntimeException("Security : Bad token !");
		return "Message deleted !";
	}
	
	@GetMapping("/rendezvous/{userId}")
	public ResponseEntity<?> getAllRendezVous(@PathVariable(value = "userId") Long userId,HttpServletRequest request){
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userIdFromToken(jwt);
		if (userId!=userIdco) throw new RuntimeException("Security: you cannot have access !");
		User user = userRepository.findUserById(userId);

		Set<RendezVous> rends = user.getRendezVous();
		List<PersonnelRdvs> persoRdvs = new ArrayList<>() ;
		if (user.getRoles().stream().findFirst().get().getRole().contains("patient")) {
			
			rends.forEach(rdv -> {
				PersonnelRdvs perso = new PersonnelRdvs();
				perso.setId(rdv.getId());
				perso.setStartDate(rdv.getStartDate());
				perso.setEndDate(rdv.getEndDate());
				perso.setDate(rdv.getDate());
				perso.setCreatedAt(rdv.getCreatedAt());
				perso.setSubject(rdv.getSubject());
				perso.setMessage(rdv.getText());
				perso.setStatus(rdv.getStatus());
				User us = userRepository.findById(rdv.getPersonnelId()).get();
				if (us.getRoles().stream().findFirst().get().getRole().contains("medecin")) {
					Medecin pt = medecinRepository.findById(rdv.getPersonnelId()).get();
					Personne p = new Personne(pt.getId(),pt.getFirstName(),pt.getLastName(),
							pt.getEmail(),pt.getUsername(),pt.getVille(),pt.getAddress(),
							pt.getPostalCode(),pt.getPhoneNumber());
					perso.setDoctor(p);
					persoRdvs.add(perso);
				}else if (us.getRoles().stream().findFirst().get().getRole().contains("psychologue")) {
					Psychologue pt = psychologueRepository.findById(rdv.getPersonnelId()).get();
					Personne p = new Personne(pt.getId(),pt.getFirstName(),pt.getLastName(),
							pt.getEmail(),pt.getUsername(),pt.getVille(),pt.getAddress(),
							pt.getPostalCode(),pt.getPhoneNumber());
					perso.setDoctor(p);
					persoRdvs.add(perso);
				}else if (us.getRoles().stream().findFirst().get().getRole().contains("kine")) {
					Kine pt = kineRepository.findById(rdv.getPersonnelId()).get();
					Personne p = new Personne(pt.getId(),pt.getFirstName(),pt.getLastName(),
							pt.getEmail(),pt.getUsername(),pt.getVille(),pt.getAddress(),
							pt.getPostalCode(),pt.getPhoneNumber());
					perso.setDoctor(p);
					persoRdvs.add(perso);
				}else if (us.getRoles().stream().findFirst().get().getRole().contains("veterinaire")) {
					Veterinaire pt = veterinaireRepository.findById(rdv.getPersonnelId()).get();
					Personne p = new Personne(pt.getId(),pt.getFirstName(),pt.getLastName(),
							pt.getEmail(),pt.getUsername(),pt.getVille(),pt.getAddress(),
							pt.getPostalCode(),pt.getPhoneNumber());
					perso.setDoctor(p);
					persoRdvs.add(perso);
				}else if (us.getRoles().stream().findFirst().get().getRole().contains("dentiste")) {
					Dentiste pt = dentisteRepository.findById(rdv.getPersonnelId()).get();
					Personne p = new Personne(pt.getId(),pt.getFirstName(),pt.getLastName(),
							pt.getEmail(),pt.getUsername(),pt.getVille(),pt.getAddress(),
							pt.getPostalCode(),pt.getPhoneNumber());
					perso.setDoctor(p);
					persoRdvs.add(perso);
				}else if (us.getRoles().stream().findFirst().get().getRole().contains("pharmacie")) {
					Pharmacie pt = pharmacieRepository.findById(rdv.getPersonnelId()).get();
					Personne p = new Personne(pt.getId(),pt.getName(),
							pt.getEmail(),pt.getUsername(),pt.getVille(),pt.getAddress(),
							pt.getPostalCode(),pt.getPhoneNumber());
					perso.setDoctor(p);
					persoRdvs.add(perso);
				}
			});

		}else if (!user.getRoles().stream().findFirst().get().getRole().contains("patient")) {
			rends.forEach(rdv -> {
				PersonnelRdvs perso = new PersonnelRdvs();
				perso.setId(rdv.getId());
				perso.setStartDate(rdv.getStartDate());
				perso.setEndDate(rdv.getEndDate());
				perso.setDate(rdv.getDate());
				perso.setCreatedAt(rdv.getCreatedAt());
				perso.setSubject(rdv.getSubject());
				perso.setMessage(rdv.getText());
				perso.setStatus(rdv.getStatus());
				
					Patient pt = patientRepository.findById(rdv.getPatientId()).get();
					Personne p = new Personne(pt.getId(),pt.getFirstName(),pt.getLastName(),
							pt.getEmail(),pt.getUsername(),pt.getVille(),pt.getAddress(),
							pt.getPostalCode(),pt.getPhoneNumber());
					perso.setPatient(p);
					persoRdvs.add(perso);
				
		});
		}
		Collections.sort(persoRdvs, new SortRdvByDate());
		return ResponseEntity.ok().body(persoRdvs);
	}
	
	@PutMapping("/changestatus/{userId}/{status}")
	public String changeStatus (@PathVariable(value = "userId") Long userId, @PathVariable(value = "status") String status,HttpServletRequest request) {
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userIdFromToken(jwt);
		User user = userRepository.findById(userIdco).get();
			if(status.contains("online")) {
				user.setOnline("online");
			}else if(status.contains("offline")) {
				user.setOnline("offline");
			}
			userRepository.save(user);
		return "update status with success !";
	}
	
	@PutMapping("/changepassword")
	public String changePassword (@RequestBody UpdatePassword userRequest,HttpServletRequest request) {
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userIdFromToken(jwt);
		User user = userRepository.findById(userIdco).get();
		if (bCryptPasswordEncoder.matches(userRequest.getOldPassword(), user.getPassword())) {
			if (userRequest.getPassword()==null && userRequest.getPasswordConfirmation()==null) {
			}else if (userRequest.getPassword()!=null && userRequest.getPasswordConfirmation()!=null && userRequest.getPassword().equals(userRequest.getPasswordConfirmation()) ) {
				user.setPassword(bCryptPasswordEncoder.encode(userRequest.getPassword()));
			}else throw new RuntimeException("password doesn't match");
			userRepository.save(user);
			return "update password with success !";
		}else throw new RuntimeException("Old password incorrect !");
	}
	
	@PutMapping("/editpatient")
	public Patient updatePatient(@Valid @RequestBody Patient userRequest, HttpServletRequest request) throws Exception{
		
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userIdFromToken(jwt);
		return patientRepository.findById(userIdco).map(user -> {
			
			  if( userRequest.getFirstName()!=null ) user.setFirstName(userRequest.getFirstName());
			  if( userRequest.getLastName()!=null  ) user.setLastName(userRequest.getLastName());
			  if( userRequest.getPhoneNumber()>0   )  user.setPhoneNumber(userRequest.getPhoneNumber());
			  if( userRequest.getVille()!=null ) user.setVille(userRequest.getVille());
			  if( userRequest.getAddress()!=null   ) user.setAddress(userRequest.getAddress());
			  if( userRequest.getPostalCode()>0    ) user.setPostalCode(userRequest.getPostalCode());
			  if( userRequest.getLanguage()!=null  ) user.setLanguage(userRequest.getLanguage());
			  if( userRequest.getPhotoUrl()!=null  ) user.setPhotoUrl(userRequest.getPhotoUrl());
			  
			  
			return patientRepository.save(user);
		
		}).orElseThrow(() -> new IllegalArgumentException("user Id" + userIdco + " not found"));			
	}
	
	@PutMapping("/edit/photocabinet")
	public String updatePhotoCabinet(@Valid @RequestBody EditPhotosCabinet userRequest, HttpServletRequest request) throws Exception{
		
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userIdFromToken(jwt);
		User req = userRepository.findById(userIdco).get();
		if (req.getRoles().stream().findFirst().get().getRole().contains("medecin")) {
			return medecinRepository.findById(userIdco).map(user -> {
				if( userRequest.getPhotoCabinet()!=null  ) user.setPhotoCabinet(userRequest.getPhotoCabinet());
				medecinRepository.save(user);
				return "Photos cabinet updated with success !";
				
			}).orElseThrow(() -> new IllegalArgumentException("user Id" + userIdco + " not found"));			
		}else if (req.getRoles().stream().findFirst().get().getRole().contains("pharmacie")) {
			return pharmacieRepository.findById(userIdco).map(user -> {
				if( userRequest.getPhotoCabinet()!=null  ) user.setPhotoPharmacie(userRequest.getPhotoCabinet());
				pharmacieRepository.save(user);
				return "Photos cabinet updated with success !";
				
			}).orElseThrow(() -> new IllegalArgumentException("user Id" + userIdco + " not found"));			
		}else if (req.getRoles().stream().findFirst().get().getRole().contains("psychologue")) {
			return psychologueRepository.findById(userIdco).map(user -> {
				if( userRequest.getPhotoCabinet()!=null  ) user.setPhotoCabinet(userRequest.getPhotoCabinet());
				psychologueRepository.save(user);
				return "Photos cabinet updated with success !";
				
			}).orElseThrow(() -> new IllegalArgumentException("user Id" + userIdco + " not found"));			
		}else if (req.getRoles().stream().findFirst().get().getRole().contains("kine")) {
			return kineRepository.findById(userIdco).map(user -> {
				if( userRequest.getPhotoCabinet()!=null  ) user.setPhotoCabinet(userRequest.getPhotoCabinet());
				kineRepository.save(user);
				return "Photos cabinet updated with success !";
				
			}).orElseThrow(() -> new IllegalArgumentException("user Id" + userIdco + " not found"));			
		}else if (req.getRoles().stream().findFirst().get().getRole().contains("dentiste")) {
			return dentisteRepository.findById(userIdco).map(user -> {
				if( userRequest.getPhotoCabinet()!=null  ) user.setPhotoCabinet(userRequest.getPhotoCabinet());
				dentisteRepository.save(user);
				return "Photos cabinet updated with success !";
				
			}).orElseThrow(() -> new IllegalArgumentException("user Id" + userIdco + " not found"));			
		}else if (req.getRoles().stream().findFirst().get().getRole().contains("veterinaire")) {
			return veterinaireRepository.findById(userIdco).map(user -> {
				if( userRequest.getPhotoCabinet()!=null  ) user.setPhotoCabinet(userRequest.getPhotoCabinet());
				veterinaireRepository.save(user);
				return "Photos cabinet updated with success !";
				
			}).orElseThrow(() -> new IllegalArgumentException("user Id" + userIdco + " not found"));			
		}else 
		return "Bad jwt !";
		}
	
	@PutMapping("/editmedecin")
	public Medecin updateMedecin(@Valid @RequestBody Medecin userRequest, HttpServletRequest request) throws Exception{
		
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userIdFromToken(jwt);
		return medecinRepository.findById(userIdco).map(user -> {
			
			  if( userRequest.getFirstName()!=null ) user.setFirstName(userRequest.getFirstName());
			  if( userRequest.getLastName()!=null  ) user.setLastName(userRequest.getLastName());
			  if( userRequest.getPhoneNumber()>0   )  user.setPhoneNumber(userRequest.getPhoneNumber());
			  if( userRequest.getVille()!=null ) user.setVille(userRequest.getVille());
			  if( userRequest.getAddress()!=null  ) user.setAddress(userRequest.getAddress());
			  if( userRequest.getPostalCode()>0    ) user.setPostalCode(userRequest.getPostalCode());
			  if( userRequest.getSpeciality()!=null ) user.setSpeciality(userRequest.getSpeciality());
			  if( userRequest.getInami()!=null    ) user.setInami(userRequest.getInami());
			  if( userRequest.getLanguage()!=null  ) user.setLanguage(userRequest.getLanguage());
			  if( userRequest.getExperienceNumber()>0) user.setExperienceNumber(userRequest.getExperienceNumber());
			  if( userRequest.getPhotoUrl()!=null  ) user.setPhotoUrl(userRequest.getPhotoUrl());
			  if( userRequest.getPhotoCabinet()!=null  ) user.setPhotoCabinet(userRequest.getPhotoCabinet());
			  
			  
			return userRepository.save(user);
		
		}).orElseThrow(() -> new IllegalArgumentException("user Id" + userIdco + " not found"));			
	}
	
	@PutMapping("/editpsychologue")
	public Psychologue updatePsychologue(@Valid @RequestBody Psychologue userRequest, HttpServletRequest request) throws Exception{
		
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userIdFromToken(jwt);
		return psychologueRepository.findById(userIdco).map(user -> {
			
			  if( userRequest.getFirstName()!=null ) user.setFirstName(userRequest.getFirstName());
			  if( userRequest.getLastName()!=null  ) user.setLastName(userRequest.getLastName());
			  if( userRequest.getPhoneNumber()>0   )  user.setPhoneNumber(userRequest.getPhoneNumber());
			  if( userRequest.getVille()!=null ) user.setVille(userRequest.getVille());
			  if( userRequest.getAddress()!=null  ) user.setAddress(userRequest.getAddress());
			  if( userRequest.getPostalCode()>0    ) user.setPostalCode(userRequest.getPostalCode());
			  if( userRequest.getLanguage()!=null  ) user.setLanguage(userRequest.getLanguage());
			  if( userRequest.getSpeciality()!=null ) user.setSpeciality(userRequest.getSpeciality());
			  if( userRequest.getInami()!=null    ) user.setInami(userRequest.getInami());
			  if( userRequest.getDescription()!=null    ) user.setInami(userRequest.getDescription());
			  if( userRequest.getExperienceNumber()>0) user.setExperienceNumber(userRequest.getExperienceNumber());
			  if( userRequest.getPhotoUrl()!=null  ) user.setPhotoUrl(userRequest.getPhotoUrl());
			  if( userRequest.getPhotoCabinet()!=null  ) user.setPhotoCabinet(userRequest.getPhotoCabinet());
			  
			  
			return psychologueRepository.save(user);
		
		}).orElseThrow(() -> new IllegalArgumentException("user Id" + userIdco + " not found"));			
	}
	
	@PutMapping("/editkine")
	public Kine updateKine(@Valid @RequestBody Psychologue userRequest, HttpServletRequest request) throws Exception{
		
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userIdFromToken(jwt);
		return kineRepository.findById(userIdco).map(user -> {
			
			  if( userRequest.getFirstName()!=null ) user.setFirstName(userRequest.getFirstName());
			  if( userRequest.getLastName()!=null  ) user.setLastName(userRequest.getLastName());
			  if( userRequest.getPhoneNumber()>0   )  user.setPhoneNumber(userRequest.getPhoneNumber());
			  if( userRequest.getVille()!=null ) user.setVille(userRequest.getVille());
			  if( userRequest.getAddress()!=null  ) user.setAddress(userRequest.getAddress());
			  if( userRequest.getPostalCode()>0    ) user.setPostalCode(userRequest.getPostalCode());
			  if( userRequest.getLanguage()!=null  ) user.setLanguage(userRequest.getLanguage());
			  if( userRequest.getSpeciality()!=null ) user.setSpeciality(userRequest.getSpeciality());
			  if( userRequest.getInami()!=null    ) user.setInami(userRequest.getInami());
			  if( userRequest.getDescription()!=null    ) user.setInami(userRequest.getDescription());
			  if( userRequest.getExperienceNumber()>0) user.setExperienceNumber(userRequest.getExperienceNumber());
			  if( userRequest.getPhotoUrl()!=null  ) user.setPhotoUrl(userRequest.getPhotoUrl());
			  if( userRequest.getPhotoCabinet()!=null  ) user.setPhotoCabinet(userRequest.getPhotoCabinet());
			  
			  
			return kineRepository.save(user);
		
		}).orElseThrow(() -> new IllegalArgumentException("user Id" + userIdco + " not found"));			
	}
	
	@PutMapping("/editveterinaire")
	public Veterinaire updateVeterinaire(@Valid @RequestBody Veterinaire userRequest, HttpServletRequest request) throws Exception{
		
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userIdFromToken(jwt);
		return veterinaireRepository.findById(userIdco).map(user -> {
			
			  if( userRequest.getFirstName()!=null ) user.setFirstName(userRequest.getFirstName());
			  if( userRequest.getLastName()!=null  ) user.setLastName(userRequest.getLastName());
			  if( userRequest.getPhoneNumber()>0   )  user.setPhoneNumber(userRequest.getPhoneNumber());
			  if( userRequest.getVille()!=null ) user.setVille(userRequest.getVille());
			  if( userRequest.getAddress()!=null  ) user.setAddress(userRequest.getAddress());
			  if( userRequest.getPostalCode()>0    ) user.setPostalCode(userRequest.getPostalCode());
			  if( userRequest.getLanguage()!=null  ) user.setLanguage(userRequest.getLanguage());
			  if( userRequest.getSpeciality()!=null ) user.setSpeciality(userRequest.getSpeciality());
			  if( userRequest.getInami()!=null    ) user.setInami(userRequest.getInami());
			  if( userRequest.getDescription()!=null    ) user.setInami(userRequest.getDescription());
			  if( userRequest.getExperienceNumber()>0) user.setExperienceNumber(userRequest.getExperienceNumber());
			  if( userRequest.getPhotoUrl()!=null  ) user.setPhotoUrl(userRequest.getPhotoUrl());
			  if( userRequest.getPhotoCabinet()!=null  ) user.setPhotoCabinet(userRequest.getPhotoCabinet());
			  
			  
			return veterinaireRepository.save(user);
		
		}).orElseThrow(() -> new IllegalArgumentException("user Id" + userIdco + " not found"));			
	}

	@PutMapping("/editpharmacie")
	public Pharmacie updateVeterinaire(@Valid @RequestBody Pharmacie userRequest, HttpServletRequest request) throws Exception{
		
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userIdFromToken(jwt);
		return pharmacieRepository.findById(userIdco).map(user -> {
			
			  if( userRequest.getName()!=null ) user.setName(userRequest.getName());
			  if( userRequest.getPhoneNumber()>0   )  user.setPhoneNumber(userRequest.getPhoneNumber());
			  if( userRequest.getVille()!=null ) user.setVille(userRequest.getVille());
			  if( userRequest.getAddress()!=null  ) user.setAddress(userRequest.getAddress());
			  if( userRequest.getPostalCode()>0    ) user.setPostalCode(userRequest.getPostalCode());
			  if( userRequest.getLanguage()!=null  ) user.setLanguage(userRequest.getLanguage());
			  if( userRequest.getInami()!=null    ) user.setInami(userRequest.getInami());
			  if( userRequest.getDescription()!=null    ) user.setInami(userRequest.getDescription());
			  if( userRequest.getExperienceNumber()>0) user.setExperienceNumber(userRequest.getExperienceNumber());
			  if( userRequest.getPhotoUrl()!=null  ) user.setPhotoUrl(userRequest.getPhotoUrl());
			  if( userRequest.getPhotoPharmacie()!=null  ) user.setPhotoPharmacie(userRequest.getPhotoPharmacie());
			  
			  
			return pharmacieRepository.save(user);
		
		}).orElseThrow(() -> new IllegalArgumentException("user Id" + userIdco + " not found"));			
	}
	
	@PutMapping("/editdentiste")
	public Dentiste updateVeterinaire(@Valid @RequestBody Dentiste userRequest, HttpServletRequest request) throws Exception{
		
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userIdFromToken(jwt);
		return dentisteRepository.findById(userIdco).map(user -> {
			
			  if( userRequest.getFirstName()!=null ) user.setFirstName(userRequest.getFirstName());
			  if( userRequest.getLastName()!=null  ) user.setLastName(userRequest.getLastName());
			  if( userRequest.getPhoneNumber()>0   )  user.setPhoneNumber(userRequest.getPhoneNumber());
			  if( userRequest.getVille()!=null ) user.setVille(userRequest.getVille());
			  if( userRequest.getAddress()!=null  ) user.setAddress(userRequest.getAddress());
			  if( userRequest.getPostalCode()>0    ) user.setPostalCode(userRequest.getPostalCode());
			  if( userRequest.getLanguage()!=null  ) user.setLanguage(userRequest.getLanguage());
			  if( userRequest.getSpeciality()!=null ) user.setSpeciality(userRequest.getSpeciality());
			  if( userRequest.getInami()!=null    ) user.setInami(userRequest.getInami());
			  if( userRequest.getDescription()!=null    ) user.setInami(userRequest.getDescription());
			  if( userRequest.getExperienceNumber()>0) user.setExperienceNumber(userRequest.getExperienceNumber());
			  if( userRequest.getPhotoUrl()!=null  ) user.setPhotoUrl(userRequest.getPhotoUrl());
			  if( userRequest.getPhotoCabinet()!=null  ) user.setPhotoCabinet(userRequest.getPhotoCabinet());
			  
			return dentisteRepository.save(user);
		
		}).orElseThrow(() -> new IllegalArgumentException("user Id" + userIdco + " not found"));			
	}
	@GetMapping("/mypatients")
	public ResponseEntity<?> getPatients(HttpServletRequest request) throws Exception{
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userIdFromToken(jwt);
		 return userRepository.findById(userIdco).map(user -> {
			 if (user.getRoles().stream().findFirst().get().getRole().contains("patient")) 
				 throw new RuntimeException("You are patient !") ;
			 List<Personne> myPatients = new ArrayList<>();
			 	user.getRendezVous().forEach(rdv ->{
			 		if (rdv.getStatus().contains("confirmed")) {
			 			Patient patient = patientRepository.findById(rdv.getPatientId()).get();
			 			
			 			Personne pt = new Personne();
						pt.id = patient.getId();
						pt.firstName = patient.getFirstName();
						pt.lastName = patient.getLastName();
						pt.phoneNumber = patient.getPhoneNumber();
						pt.email = patient.getEmail();
						pt.createdAt = patient.getCreatedAt();
						pt.role = patient.getRoles().stream().findFirst().get().getRole();
						pt.status = patient.getOnline();
						pt.active = patient.isActive();
						pt.banned = patient.isBanned();
						pt.deleted = patient.isDeleted();
						if (!myPatients.stream().anyMatch(pat-> pat.id==pt.id)) {
							myPatients.add(pt);
						}
			 		}
			 	});
			 	Collections.sort(myPatients, new SortPersonnesByDate());
			 	Collections.reverse(myPatients);
			return ResponseEntity.ok().body(myPatients);
		
		}).orElseThrow(() -> new IllegalArgumentException("user Id" + userIdco + " not found"));	
	}
	@GetMapping("/mypersonnels")
	public ResponseEntity<?> getMyPersonnels(HttpServletRequest request) throws Exception{
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userIdFromToken(jwt);
		 return userRepository.findById(userIdco).map(user -> {
			 if (!user.getRoles().stream().findFirst().get().getRole().contains("patient")) 
				 throw new RuntimeException("You are medecin !") ;
			
			 List<Personne> myPersonnels = new ArrayList<>();
			 user.getRendezVous().forEach(rdv ->{
				 if (rdv.getStatus().contains("confirmed")) {
					 
					 if (userRepository.findById(rdv.getPersonnelId()).get()
							 .getRoles().stream().findFirst().get().getRole().contains("medecin")) {
						 Medecin medecin = medecinRepository.findById(rdv.getPersonnelId()).get();
						 Personne pt = new Personne();
						 pt.id = medecin.getId();
						 pt.firstName = medecin.getFirstName();
						 pt.lastName = medecin.getLastName();
						 pt.phoneNumber = medecin.getPhoneNumber();
						 pt.email = medecin.getEmail();
						 pt.createdAt = medecin.getCreatedAt();
						 pt.role = medecin.getRoles().stream().findFirst().get().getRole();
						 pt.status = medecin.getOnline();
						 pt.active = medecin.isActive();
						 pt.banned = medecin.isBanned();
						 pt.deleted = medecin.isDeleted();
						 if (!myPersonnels.stream().anyMatch(per-> per.id==pt.id)) {
							 myPersonnels.add(pt);
						 }
					 }else if (userRepository.findById(rdv.getPersonnelId()).get()
							 .getRoles().stream().findFirst().get().getRole().contains("dentiste")) {
						 Dentiste medecin = dentisteRepository.findById(rdv.getPersonnelId()).get();
						 Personne pt = new Personne();
						 pt.id = medecin.getId();
						 pt.firstName = medecin.getFirstName();
						 pt.lastName = medecin.getLastName();
						 pt.phoneNumber = medecin.getPhoneNumber();
						 pt.email = medecin.getEmail();
						 pt.createdAt = medecin.getCreatedAt();
						 pt.role = medecin.getRoles().stream().findFirst().get().getRole();
						 pt.status = medecin.getOnline();
						 pt.active = medecin.isActive();
						 pt.banned = medecin.isBanned();
						 pt.deleted = medecin.isDeleted();
						 if (!myPersonnels.stream().anyMatch(per-> per.id==pt.id)) {
							 myPersonnels.add(pt);
						 }
					 }else if (userRepository.findById(rdv.getPersonnelId()).get()
							 .getRoles().stream().findFirst().get().getRole().contains("kine")) {
						 Kine medecin = kineRepository.findById(rdv.getPersonnelId()).get();
						 Personne pt = new Personne();
						 pt.id = medecin.getId();
						 pt.firstName = medecin.getFirstName();
						 pt.lastName = medecin.getLastName();
						 pt.phoneNumber = medecin.getPhoneNumber();
						 pt.email = medecin.getEmail();
						 pt.createdAt = medecin.getCreatedAt();
						 pt.role = medecin.getRoles().stream().findFirst().get().getRole();
						 pt.status = medecin.getOnline();
						 pt.active = medecin.isActive();
						 pt.banned = medecin.isBanned();
						 pt.deleted = medecin.isDeleted();
						 if (!myPersonnels.stream().anyMatch(per-> per.id==pt.id)) {
							 myPersonnels.add(pt);
						 }
					 }else if (userRepository.findById(rdv.getPersonnelId()).get()
							 .getRoles().stream().findFirst().get().getRole().contains("veterinaire")) {
						 Veterinaire medecin = veterinaireRepository.findById(rdv.getPersonnelId()).get();
						 Personne pt = new Personne();
						 pt.id = medecin.getId();
						 pt.firstName = medecin.getFirstName();
						 pt.lastName = medecin.getLastName();
						 pt.phoneNumber = medecin.getPhoneNumber();
						 pt.email = medecin.getEmail();
						 pt.createdAt = medecin.getCreatedAt();
						 pt.role = medecin.getRoles().stream().findFirst().get().getRole();
						 pt.status = medecin.getOnline();
						 pt.active = medecin.isActive();
						 pt.banned = medecin.isBanned();
						 pt.deleted = medecin.isDeleted();
						 if (!myPersonnels.stream().anyMatch(per-> per.id==pt.id)) {
							 myPersonnels.add(pt);
						 }
					 }else if (userRepository.findById(rdv.getPersonnelId()).get()
							 .getRoles().stream().findFirst().get().getRole().contains("psychologue")) {
						 Psychologue medecin = psychologueRepository.findById(rdv.getPersonnelId()).get();
						 Personne pt = new Personne();
						 pt.id = medecin.getId();
						 pt.firstName = medecin.getFirstName();
						 pt.lastName = medecin.getLastName();
						 pt.phoneNumber = medecin.getPhoneNumber();
						 pt.email = medecin.getEmail();
						 pt.createdAt = medecin.getCreatedAt();
						 pt.role = medecin.getRoles().stream().findFirst().get().getRole();
						 pt.status = medecin.getOnline();
						 pt.active = medecin.isActive();
						 pt.banned = medecin.isBanned();
						 pt.deleted = medecin.isDeleted();
						 if (!myPersonnels.stream().anyMatch(per-> per.id==pt.id)) {
							 myPersonnels.add(pt);
						 }
					 }else if (userRepository.findById(rdv.getPersonnelId()).get()
							 .getRoles().stream().findFirst().get().getRole().contains("pharmacie")) {
						 Pharmacie medecin = pharmacieRepository.findById(rdv.getPersonnelId()).get();
						 Personne pt = new Personne();
						 pt.id = medecin.getId();
						 pt.name = medecin.getName();
						 pt.phoneNumber = medecin.getPhoneNumber();
						 pt.email = medecin.getEmail();
						 pt.createdAt = medecin.getCreatedAt();
						 pt.role = medecin.getRoles().stream().findFirst().get().getRole();
						 pt.status = medecin.getOnline();
						 pt.active = medecin.isActive();
						 pt.banned = medecin.isBanned();
						 pt.deleted = medecin.isDeleted();
						 if (!myPersonnels.stream().anyMatch(per-> per.id==pt.id)) {
							 myPersonnels.add(pt);
						 }
					 }
					 
				 }
			 	});
			 	Collections.sort(myPersonnels, new SortPersonnesByDate());
			 	Collections.reverse(myPersonnels);
			return ResponseEntity.ok().body(myPersonnels);
		
		}).orElseThrow(() -> new IllegalArgumentException("user Id" + userIdco + " not found"));	
	}
		public Long userIdFromToken (String jwt) {	
		DecodedJWT jwtd = JWT.decode(jwt);
		String usernameFromToken = jwtd.getSubject();
		User user = userService.findUserByUsername(usernameFromToken);
		Long userIdco = null;
		if (user==null) {
			return userIdco;
		}else return user.getId();
	}
		
		
}