package com.itmsd.medical.controllers;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itmsd.medical.entities.Admin;
import com.itmsd.medical.entities.Dentiste;
import com.itmsd.medical.entities.Diplome;
import com.itmsd.medical.entities.Experience;
import com.itmsd.medical.entities.Kine;
import com.itmsd.medical.entities.Medecin;
import com.itmsd.medical.entities.Message;
import com.itmsd.medical.entities.Patient;
import com.itmsd.medical.entities.Pharmacie;
import com.itmsd.medical.entities.Psychologue;
import com.itmsd.medical.entities.Session;
import com.itmsd.medical.entities.User;
import com.itmsd.medical.entities.Veterinaire;
import com.itmsd.medical.payload.request.Personne;
import com.itmsd.medical.payload.request.SearchRequest;
import com.itmsd.medical.payload.response.MembersDto;
import com.itmsd.medical.payload.response.PersonnelRdvs;
import com.itmsd.medical.payload.response.SearchResponse;
import com.itmsd.medical.repositories.AdminRepository;
import com.itmsd.medical.repositories.DentisteRepository;
import com.itmsd.medical.repositories.ForumRepository;
import com.itmsd.medical.repositories.KineRepository;
import com.itmsd.medical.repositories.MedecinRepository;
import com.itmsd.medical.repositories.MessageRepository;
import com.itmsd.medical.repositories.PatientRepository;
import com.itmsd.medical.repositories.PharmacieRepository;
import com.itmsd.medical.repositories.PsychologueRepository;
import com.itmsd.medical.repositories.RoleRepository;
import com.itmsd.medical.repositories.UserRepository;
import com.itmsd.medical.repositories.VeterinaireRepository;
import com.itmsd.medical.services.DateAttribute;

@RestController
@RequestMapping("search")
public class SearchController {
	
	private PatientRepository patientRepository;
	private UserRepository userRepository;
	private RoleRepository roleRepository;
	private MedecinRepository medecinRepository;	
	private DentisteRepository dentisteRepository;	
	private KineRepository kineRepository;	
	private PharmacieRepository pharmacieRepository;	
	private VeterinaireRepository veterinaireRepository;	
	private PsychologueRepository psychologueRepository;
	private MessageRepository messageRepository;
	private UserController userController;
	private AdminRepository adminRepository;
	private ForumRepository forumRepository;
	
	@Autowired
	public SearchController(UserRepository userRepository, PatientRepository patientRepository,
			RoleRepository roleRepository, MedecinRepository medecinRepository,
			DentisteRepository dentisteRepository, KineRepository kineRepository,
			PharmacieRepository pharmacieRepository, VeterinaireRepository veterinaireRepository,
			PsychologueRepository psychologueRepository,MessageRepository messageRepository,
			UserController userController, AdminRepository adminRepository,ForumRepository forumRepository) {
		super();
		this.medecinRepository = medecinRepository;
		this.userRepository = userRepository;
		this.patientRepository = patientRepository;
		this.roleRepository = roleRepository;
		this.dentisteRepository = dentisteRepository;
		this.kineRepository = kineRepository;
		this.pharmacieRepository = pharmacieRepository;
		this.veterinaireRepository = veterinaireRepository;
		this.psychologueRepository = psychologueRepository;
		this.messageRepository = messageRepository;
		this.userController = userController;
		this.adminRepository = adminRepository;
		this.forumRepository = forumRepository;
	}
	
	@GetMapping("schedule/{userId}")
	public ResponseEntity<?> getSchedule(@PathVariable Long userId){
			User user = userRepository.findUserById(userId);
				if(!user.getSchedule().stream().findFirst().get().getSession().isEmpty()) {
					List<Session> ss = user.getSchedule().stream().findFirst().get().getSession();
					Collections.sort(ss, new SortSessionByDate() );
					Collections.reverse(ss);
					return ResponseEntity.ok().body(ss);
				} else return ResponseEntity.badRequest().body("This user doesn't have session");
			
	}
	
	@GetMapping("most/viewed")
	public ResponseEntity<?> getMostViewed() {
		List<Personne> viewed = new ArrayList<Personne>();
		
		if (userRepository.findAll()!=null) {
			userRepository.findTopConsulted().forEach(user -> {
				if (user.isDeleted()) {
					userRepository.findTopConsulted().remove(user);
				}
				if (!user.getRoles().stream().findFirst().get().getRole().contains("admin")&&
						!user.getRoles().stream().findFirst().get().getRole().contains("patient")) {
					Personne pt = new Personne();
					pt.id = user.getId();
					pt.phoneNumber = user.getPhoneNumber();
					pt.email = user.getEmail();
					pt.createdAt = user.getCreatedAt();
					pt.role = user.getRoles().stream().findFirst().get().getRole();
					pt.status = user.getOnline();
					pt.active = user.isActive();
					pt.banned = user.isBanned();
					pt.nbViews = user.getNbviews();
					if (user.getRoles().stream().findFirst().get().getRole().contains("medecin")) {
						Medecin med = medecinRepository.findById(user.getId()).get();
						pt.firstName = med.getFirstName();
						pt.lastName = med.getLastName();
						pt.address = med.getAddress();
						pt.postalCode = med.getPostalCode();
					}else if (user.getRoles().stream().findFirst().get().getRole().contains("psychologue")) {
						Psychologue med = psychologueRepository.findById(user.getId()).get();
						pt.firstName = med.getFirstName();
						pt.lastName = med.getLastName();
						pt.address = med.getAddress();
						pt.postalCode = med.getPostalCode();
					}else if (user.getRoles().stream().findFirst().get().getRole().contains("kine")) {
						Kine med = kineRepository.findById(user.getId()).get();
						pt.firstName = med.getFirstName();
						pt.lastName = med.getLastName();
						pt.address = med.getAddress();
						pt.postalCode = med.getPostalCode();
					}else if (user.getRoles().stream().findFirst().get().getRole().contains("dentiste")) {
						Dentiste med = dentisteRepository.findById(user.getId()).get();
						pt.firstName = med.getFirstName();
						pt.lastName = med.getLastName();
						pt.address = med.getAddress();
						pt.postalCode = med.getPostalCode();
					}else if (user.getRoles().stream().findFirst().get().getRole().contains("veterinaire")) {
						Veterinaire med = veterinaireRepository.findById(user.getId()).get();
						pt.firstName = med.getFirstName();
						pt.lastName = med.getLastName();
						pt.address = med.getAddress();
						pt.postalCode = med.getPostalCode();
					}else if (user.getRoles().stream().findFirst().get().getRole().contains("pharmacie")) {
						Pharmacie med = pharmacieRepository.findById(user.getId()).get();
						pt.name = med.getName();
						pt.address = med.getAddress();
						pt.postalCode = med.getPostalCode();
					}
					
					viewed.add(pt);
				}
			});
			Collections.sort(viewed, new SortPersonnesByDate());
			Collections.reverse(viewed);
			return ResponseEntity.ok(viewed);
		}else return ResponseEntity.badRequest().body("There is no viewed personnels !");
		
	}
	
	@GetMapping("patients")
	public ResponseEntity<?> getPatients(HttpServletRequest request) {
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userController.userIdFromToken(jwt);
		User user = userRepository.findUserById(userIdco);
		if (!user.getUsername().equals("superAdmin1")) throw new RuntimeException("Security: Only admin can see this !");
		List<Personne> patients = new ArrayList<Personne>();
		if (patientRepository.findAll()!=null) {
			patientRepository.findAll().forEach(patient -> {
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
				patients.add(pt);
			});
			Collections.sort(patients, new SortPersonnesByDate());
			Collections.reverse(patients);
			return ResponseEntity.ok(patients);
		}else return ResponseEntity.badRequest().body("There is no patients !");
		
	}
	
	@GetMapping("personnels")
	public ResponseEntity<?> getPersonnel(HttpServletRequest request) {
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userController.userIdFromToken(jwt);
		User user = userRepository.findUserById(userIdco);
		if (!user.getUsername().equals("superAdmin1")) throw new RuntimeException("Security: Only admin can see this !");
		List<Personne> personnels = new ArrayList<Personne>();
		
		if (medecinRepository.findAll()!=null) {
		 medecinRepository.findAll().forEach(medecin ->{
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
				personnels.add(pt);
		 });
		}
		 if (psychologueRepository.findAll()!=null) {
			 psychologueRepository.findAll().forEach(medecin ->{
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
				 personnels.add(pt);
			 });
		 }
		 if (veterinaireRepository.findAll()!=null) {
			 veterinaireRepository.findAll().forEach(medecin ->{
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
				 personnels.add(pt);
			 });
		 }
		 if (kineRepository.findAll()!=null) {
			 kineRepository.findAll().forEach(medecin ->{
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
				 
				 personnels.add(pt);
			 });
		 }
		 if (dentisteRepository.findAll()!=null) {
			 dentisteRepository.findAll().forEach(medecin ->{
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
				 personnels.add(pt);
			 });
		 }
		 if (pharmacieRepository.findAll()!=null) {
			 pharmacieRepository.findAll().forEach(medecin ->{
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
				 personnels.add(pt);
			 });
		 }
		 Collections.sort(personnels, new SortPersonnesByDate());
		 Collections.reverse(personnels);
		if (!personnels.isEmpty()) {
			return ResponseEntity.ok().body(personnels);
		} else return ResponseEntity.ok().body("There is no personnels !");
	}
	
	
	@GetMapping("user/{userId}")
	public ResponseEntity<?> getPersonnelOrPatient(@PathVariable Long userId,HttpServletRequest request) {	
		String ipUser = request.getRemoteAddr();
	if(userRepository.findUserById(userId)!=null) {
		User user = userRepository.findUserById(userId);
	
		if (user.getRoles().contains(roleRepository.findByRole("patient"))) {
			Patient pat = patientRepository.findById(userId).get();
			pat.setForum(null);
			pat.setNotifications(null);
			pat.setUsername(null);
			pat.setJwt(null);
			pat.setRendezVous(null);
			return ResponseEntity.ok().body(patientRepository.findById(userId));
		}else if (user.getRoles().contains(roleRepository.findByRole("medecin"))) {
			Medecin med = medecinRepository.findById(userId).get();
			Collections.sort(med.getExperiences(), new SortExperienceByDate());
			Collections.sort(med.getDiplomes(), new SortDiplomeByDate());
			if (med.getIpUsers().isEmpty() || !med.getIpUsers().contains(ipUser)) {
				med.getIpUsers().add(ipUser);
				med.setNbviews(med.getNbviews()+1);
				medecinRepository.save(med);
			}
			med.setForum(null);
			med.setNotifications(null);
			med.setUsername(null);
			med.setJwt(null);
			med.setRendezVous(null);
			return ResponseEntity.ok().body(med);
		}else if (user.getRoles().contains(roleRepository.findByRole("dentiste"))) {
			Dentiste med = dentisteRepository.findById(userId).get();
			Collections.sort(med.getExperiences(), new SortExperienceByDate());
			Collections.sort(med.getDiplomes(), new SortDiplomeByDate());
			if (med.getIpUsers().isEmpty() || !med.getIpUsers().contains(ipUser)) {
				med.getIpUsers().add(ipUser);
				med.setNbviews(med.getNbviews()+1);
				dentisteRepository.save(med);
			}
			med.setForum(null);
			med.setNotifications(null);
			med.setUsername(null);
			med.setJwt(null);
			med.setRendezVous(null);
			return ResponseEntity.ok().body(med);
		
		}else if (user.getRoles().contains(roleRepository.findByRole("kine"))) {
			Kine med = kineRepository.findById(userId).get();
			Collections.sort(med.getExperiences(), new SortExperienceByDate());
			Collections.sort(med.getDiplomes(), new SortDiplomeByDate());
			if (med.getIpUsers().isEmpty() || !med.getIpUsers().contains(ipUser)) {
				med.getIpUsers().add(ipUser);
				med.setNbviews(med.getNbviews()+1);
				kineRepository.save(med);
			}
			med.setForum(null);
			med.setNotifications(null);
			med.setUsername(null);
			med.setJwt(null);
			med.setRendezVous(null);
			return ResponseEntity.ok().body(med);
		
		}else if (user.getRoles().contains(roleRepository.findByRole("pharmacie"))) {
			Pharmacie med = pharmacieRepository.findById(userId).get();
			Collections.sort(med.getExperiences(), new SortExperienceByDate());
			Collections.sort(med.getDiplomes(), new SortDiplomeByDate());
			if (med.getIpUsers().isEmpty() || !med.getIpUsers().contains(ipUser)) {
				med.getIpUsers().add(ipUser);
				med.setNbviews(med.getNbviews()+1);
				pharmacieRepository.save(med);
			}
			med.setForum(null);
			med.setNotifications(null);
			med.setUsername(null);
			med.setJwt(null);
			med.setRendezVous(null);
			return ResponseEntity.ok().body(med);		
		}else if (user.getRoles().contains(roleRepository.findByRole("veterinaire"))) {
			Veterinaire med = veterinaireRepository.findById(userId).get();
			Collections.sort(med.getExperiences(), new SortExperienceByDate());
			Collections.sort(med.getDiplomes(), new SortDiplomeByDate());
			if (med.getIpUsers().isEmpty() || !med.getIpUsers().contains(ipUser)) {
				med.getIpUsers().add(ipUser);
				med.setNbviews(med.getNbviews()+1);
				veterinaireRepository.save(med);
			}
			med.setForum(null);
			med.setNotifications(null);
			med.setUsername(null);
			med.setJwt(null);
			med.setRendezVous(null);
			return ResponseEntity.ok().body(med);		
		}else if (user.getRoles().contains(roleRepository.findByRole("psychologue"))) {
			Psychologue med = psychologueRepository.findById(userId).get();
			Collections.sort(med.getExperiences(), new SortExperienceByDate());
			Collections.sort(med.getDiplomes(), new SortDiplomeByDate());
			if (med.getIpUsers().isEmpty() || !med.getIpUsers().contains(ipUser)) {
				med.getIpUsers().add(ipUser);
				med.setNbviews(med.getNbviews()+1);
				psychologueRepository.save(med);
			}
			med.setForum(null);
			med.setNotifications(null);
			med.setUsername(null);
			med.setJwt(null);
			med.setRendezVous(null);
			return ResponseEntity.ok().body(med);		
		}else if (user.getRoles().contains(roleRepository.findByRole("admin"))) {
			Admin admin = adminRepository.findById(userId).get();
			admin.setJwt(null);
			return ResponseEntity.ok().body(admin);
		
		}
		
		return ResponseEntity.ok("Bad id !");
	}
	return ResponseEntity.ok("No user with this id !");
	}
	

	@GetMapping("user/message/{userId}")
	public ResponseEntity<?> getMessagePersonnel(@PathVariable Long userId,HttpServletRequest request) {	
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userController.userIdFromToken(jwt);
		User user = userRepository.findById(userIdco).get();
		if(user!=null) {
		if (user.getRoles().contains(roleRepository.findByRole("patient"))) {
			return ResponseEntity.badRequest().body("this is patient");
		}else {
				Set<Message> msgs = messageRepository.findMessages(user.getForum().stream().findFirst().get().getId());
					msgs.forEach((msg)-> {
					msg.setAvatarUrl(userRepository.findUserByUsername(msg.getUsername()).getPhotoUrl());
			});
			return ResponseEntity.ok().body(msgs);
		}
	}
	return ResponseEntity.ok("Bad id !");
	}
	
	@GetMapping("admin/messages/{category}")
	public ResponseEntity<?> getMessageAdmin(@PathVariable(value="category") String category,HttpServletRequest request) {	
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userController.userIdFromToken(jwt);
		User user = userRepository.findById(userIdco).get();
		if(user!=null && !user.getRoles().contains(roleRepository.findByRole("admin"))) {
			return ResponseEntity.badRequest().body("Security you are not admin sorry !");
		}else if (category == null){
			throw new RuntimeException("Invalid category !");
		}else {
				Set<Message> msgs = messageRepository.findMessages((forumRepository.findByForumName(category).getId()));
					msgs.forEach((msg)-> {
					msg.setAvatarUrl(userRepository.findUserByUsername(msg.getUsername()).getPhotoUrl());
			});
			return ResponseEntity.ok().body(msgs);
		}
	}
	
	@GetMapping("members/forum/{category}")
	public ResponseEntity<?> getMembersForum(@PathVariable(value="category") String category,HttpServletRequest request) {	
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userController.userIdFromToken(jwt);
		User user = userRepository.findById(userIdco).get();
		if(user!=null) {
			if (category.contains("patient")) {
			return ResponseEntity.badRequest().body("this is patient");
		}else if (category.contains("pharmacie")){
			List<Pharmacie> pharmacie = pharmacieRepository.findAll();	
			List<MembersDto> members = new ArrayList<>();
			
			pharmacie.forEach(ph -> {
				if (!ph.isDeleted()) {
					MembersDto member = new MembersDto();
					member.setId(ph.getId());
					member.setName(ph.getName());
					member.setUsername(ph.getUsername());
					member.setCreatedAt(ph.getCreatedAt());
					member.setImageUrl(ph.getPhotoUrl());
					member.setStatus(ph.getOnline());
					members.add(member);
				}
			});
			Collections.reverse(members);
			return ResponseEntity.ok().body(members);
		}else if (category.contains("medecin")){
			List<Medecin> medecin = medecinRepository.findAll();	
			List<MembersDto> members = new ArrayList<>();
			medecin.forEach(med -> {
				MembersDto member = new MembersDto();
				member.setId(med.getId());
				member.setFirstName(med.getFirstName());
				member.setLastName(med.getLastName());
				member.setUsername(med.getUsername());
				member.setSpeciality(med.getSpeciality());
				member.setCreatedAt(med.getCreatedAt());
				member.setImageUrl(med.getPhotoUrl());
				member.setStatus(med.getOnline());
				members.add(member);
			});
			Collections.reverse(members);
			return ResponseEntity.ok().body(members);
		}else if (category.contains("psychologue")){
			List<Psychologue> medecin = psychologueRepository.findAll();	
			List<MembersDto> members = new ArrayList<>();
			medecin.forEach(med -> {
				MembersDto member = new MembersDto();
				member.setId(med.getId());
				member.setFirstName(med.getFirstName());
				member.setLastName(med.getLastName());
				member.setUsername(med.getUsername());
				member.setCreatedAt(med.getCreatedAt());
				member.setImageUrl(med.getPhotoUrl());
				member.setStatus(med.getOnline());
				members.add(member);
			});
			Collections.reverse(members);
			return ResponseEntity.ok().body(members);
		}else if (category.contains("dentiste")){
			List<Dentiste> medecin = dentisteRepository.findAll();	
			List<MembersDto> members = new ArrayList<>();
			medecin.forEach(med -> {
				MembersDto member = new MembersDto();
				member.setId(med.getId());
				member.setFirstName(med.getFirstName());
				member.setLastName(med.getLastName());
				member.setUsername(med.getUsername());
				member.setCreatedAt(med.getCreatedAt());
				member.setImageUrl(med.getPhotoUrl());
				member.setStatus(med.getOnline());
				members.add(member);
			});
			Collections.reverse(members);
			return ResponseEntity.ok().body(members);
		}else if (category.contains("kine")){
			List<Kine> medecin = kineRepository.findAll();	
			List<MembersDto> members = new ArrayList<>();
			medecin.forEach(med -> {
				MembersDto member = new MembersDto();
				member.setId(med.getId());
				member.setFirstName(med.getFirstName());
				member.setLastName(med.getLastName());
				member.setUsername(med.getUsername());
				member.setCreatedAt(med.getCreatedAt());
				member.setImageUrl(med.getPhotoUrl());
				member.setStatus(med.getOnline());
				members.add(member);
			});
			Collections.reverse(members);
			return ResponseEntity.ok().body(members);
		}else if (category.contains("veterinaire")){
			List<Veterinaire> medecin = veterinaireRepository.findAll();	
			List<MembersDto> members = new ArrayList<>();
			medecin.forEach(med -> {
				if (!med.isDeleted()) {
					MembersDto member = new MembersDto();
				member.setId(med.getId());
				member.setFirstName(med.getFirstName());
				member.setLastName(med.getLastName());
				member.setUsername(med.getUsername());
				member.setCreatedAt(med.getCreatedAt());
				member.setImageUrl(med.getPhotoUrl());
				member.setStatus(med.getOnline());
				members.add(member);
				}
			});
			Collections.reverse(members);
			return ResponseEntity.ok().body(members);
		}
	}
	return ResponseEntity.badRequest().body("Bad id !");
	}
	
	@PostMapping("personnel/precise")
	public ResponseEntity<?> searchPersonnel(@RequestBody SearchRequest search){
			 SearchResponse response = new SearchResponse();
			
		if (search.getCategory().contains("medecin")) {
			if (search.getSpeciality() == null && search.getCodePostal() == null 
					&& search.getVille() == null) {
				response.setMedecins(medecinRepository.findAll());
					
				if(!search.getLangue().isEmpty()) {
					response.setMedecins(getMedecinByLanguage(response.getMedecins(), search.getLangue()));
					response.getMedecins().forEach(med->{
						med.setForum(null);
						med.setNotifications(null);
						med.setUsername(null);
						med.setJwt(null);
						med.setRendezVous(null);
						med.setIpUsers(null);
						med.setSchedule(null);
					});
					return ResponseEntity.ok().body(response);
				}
				response.getMedecins().forEach(med->{
					med.setForum(null);
					med.setNotifications(null);
					med.setUsername(null);
					med.setJwt(null);
					med.setRendezVous(null);
					med.setIpUsers(null);
					med.setSchedule(null);
				});
				return ResponseEntity.ok().body(response);
			}else if (search.getSpeciality() == null && search.getCodePostal() != null 
					&& search.getVille() == null) {
				response.setMedecins(medecinRepository.findAllByPostalCode(search.getCodePostal()));
				if(!search.getLangue().isEmpty()) {
					response.setMedecins(getMedecinByLanguage(response.getMedecins(), search.getLangue()));
					response.getMedecins().forEach(med->{
						med.setForum(null);
						med.setNotifications(null);
						med.setUsername(null);
						med.setJwt(null);
						med.setRendezVous(null);
						med.setIpUsers(null);
						med.setSchedule(null);
					});
					return ResponseEntity.ok().body(response);
				}
				response.getMedecins().forEach(med->{
					med.setForum(null);
					med.setNotifications(null);
					med.setUsername(null);
					med.setJwt(null);
					med.setRendezVous(null);
					med.setIpUsers(null);
					med.setSchedule(null);
				});
				return ResponseEntity.ok().body(response);
			}else if (search.getSpeciality() == null && search.getCodePostal() == null 
					&& search.getVille() != null) {
				response.setMedecins(medecinRepository.findAllByVille(search.getVille()));
				if(!search.getLangue().isEmpty()) {
					response.setMedecins(getMedecinByLanguage(response.getMedecins(), search.getLangue()));
					response.getMedecins().forEach(med->{
						med.setForum(null);
						med.setNotifications(null);
						med.setUsername(null);
						med.setJwt(null);
						med.setRendezVous(null);
						med.setIpUsers(null);
						med.setSchedule(null);
					});
					return ResponseEntity.ok().body(response);
				}
				response.getMedecins().forEach(med->{
					med.setForum(null);
					med.setNotifications(null);
					med.setUsername(null);
					med.setJwt(null);
					med.setRendezVous(null);
					med.setIpUsers(null);
					med.setSchedule(null);
				});
				return ResponseEntity.ok().body(response);
			}else if (search.getSpeciality() == null && search.getCodePostal() != null 
					&& search.getVille() != null) {
				response.setMedecins(medecinRepository.findAllByVilleAndPostalCode(search.getVille(),search.getCodePostal()));
				if(!search.getLangue().isEmpty()) {
					response.setMedecins(getMedecinByLanguage(response.getMedecins(), search.getLangue()));
					response.getMedecins().forEach(med->{
						med.setForum(null);
						med.setNotifications(null);
						med.setUsername(null);
						med.setJwt(null);
						med.setRendezVous(null);
						med.setIpUsers(null);
						med.setSchedule(null);
					});
					return ResponseEntity.ok().body(response);
				}
				response.getMedecins().forEach(med->{
					med.setForum(null);
					med.setNotifications(null);
					med.setUsername(null);
					med.setJwt(null);
					med.setRendezVous(null);
					med.setIpUsers(null);
					med.setSchedule(null);
				});
				return ResponseEntity.ok().body(response);
			}else if (search.getSpeciality() != null && search.getCodePostal() != null 
					&& search.getVille() != null) {
				response.setMedecins(medecinRepository.findAllBySpecialityAndVilleAndPostalCode(search.getSpeciality(), search.getVille(),search.getCodePostal()));
				if(!search.getLangue().isEmpty()) {
					response.setMedecins(getMedecinByLanguage(response.getMedecins(), search.getLangue()));
					response.getMedecins().forEach(med->{
						med.setForum(null);
						med.setNotifications(null);
						med.setUsername(null);
						med.setJwt(null);
						med.setRendezVous(null);
						med.setIpUsers(null);
						med.setSchedule(null);
					});
					return ResponseEntity.ok().body(response);
				}
				response.getMedecins().forEach(med->{
					med.setForum(null);
					med.setNotifications(null);
					med.setUsername(null);
					med.setJwt(null);
					med.setRendezVous(null);
					med.setIpUsers(null);
					med.setSchedule(null);
				});
				return ResponseEntity.ok().body(response);
			}else if (search.getSpeciality() != null && search.getCodePostal() == null 
					&& search.getVille() != null) {
				response.setMedecins(medecinRepository.findAllBySpecialityAndVille(search.getSpeciality(), search.getVille()));
				if(!search.getLangue().isEmpty()) {
					response.setMedecins(getMedecinByLanguage(response.getMedecins(), search.getLangue()));
					response.getMedecins().forEach(med->{
						med.setForum(null);
						med.setNotifications(null);
						med.setUsername(null);
						med.setJwt(null);
						med.setRendezVous(null);
						med.setIpUsers(null);
						med.setSchedule(null);
					});
					return ResponseEntity.ok().body(response);
				}
				response.getMedecins().forEach(med->{
					med.setForum(null);
					med.setNotifications(null);
					med.setUsername(null);
					med.setJwt(null);
					med.setRendezVous(null);
					med.setIpUsers(null);
					med.setSchedule(null);
				});
				return ResponseEntity.ok().body(response);
			}else if (search.getSpeciality() != null && search.getCodePostal() != null 
					&& search.getVille() == null) {
				response.setMedecins(medecinRepository.findAllBySpecialityAndPostalCode(search.getSpeciality(), search.getCodePostal()));
				if(!search.getLangue().isEmpty()) {
					response.setMedecins(getMedecinByLanguage(response.getMedecins(), search.getLangue()));
					response.getMedecins().forEach(med->{
						med.setForum(null);
						med.setNotifications(null);
						med.setUsername(null);
						med.setJwt(null);
						med.setRendezVous(null);
						med.setIpUsers(null);
						med.setSchedule(null);
					});
					return ResponseEntity.ok().body(response);
				}
				response.getMedecins().forEach(med->{
					med.setForum(null);
					med.setNotifications(null);
					med.setUsername(null);
					med.setJwt(null);
					med.setRendezVous(null);
					med.setIpUsers(null);
					med.setSchedule(null);
				});
				return ResponseEntity.ok().body(response);
			}else if (search.getSpeciality() != null && search.getCodePostal() == null 
					&& search.getVille() == null) {
				response.setMedecins(medecinRepository.findAllBySpeciality(search.getSpeciality()));
				if(!search.getLangue().isEmpty()) {
					response.setMedecins(getMedecinByLanguage(response.getMedecins(), search.getLangue()));
					response.getMedecins().forEach(med->{
						med.setForum(null);
						med.setNotifications(null);
						med.setUsername(null);
						med.setJwt(null);
						med.setRendezVous(null);
						med.setIpUsers(null);
						med.setSchedule(null);
					});
					return ResponseEntity.ok().body(response);
				}
				response.getMedecins().forEach(med->{
					med.setForum(null);
					med.setNotifications(null);
					med.setUsername(null);
					med.setJwt(null);
					med.setRendezVous(null);
					med.setIpUsers(null);
					med.setSchedule(null);
				});
				return ResponseEntity.ok().body(response);
			}
		}
			 
		if (search.getCategory().contains("pharmacie")) {
				if (search.getVille() == null && search.getCodePostal() == null) {
					/*response.setPharmacies(pharmacieRepository.findAll());	
					response.getPharmacies().forEach(med->{
						if (med.isDeleted()) {
							response.getPharmacies().remove(med);
						}
					});*/
					if(!search.getLangue().isEmpty()) {
						response.setPharmacies(getPharmacieByLanguage(response.getPharmacies(), search.getLangue()));
						response.getPharmacies().forEach(med->{
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
					response.getPharmacies().forEach(med->{
						med.setForum(null);
						med.setNotifications(null);
						med.setUsername(null);
						med.setJwt(null);
						med.setRendezVous(null);
						med.setIpUsers(null);
						med.setSchedule(null);
					});
					return ResponseEntity.ok().body(response);
				}else if (search.getVille() != null && search.getCodePostal() == null) {
					response.setPharmacies(pharmacieRepository.findAllByVille(search.getVille()));				
					if(!search.getLangue().isEmpty()) {
						response.setPharmacies(getPharmacieByLanguage(response.getPharmacies(), search.getLangue()));
						response.getPharmacies().forEach(med->{
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
					response.getPharmacies().forEach(med->{
						med.setForum(null);
						med.setNotifications(null);
						med.setUsername(null);
						med.setJwt(null);
						med.setRendezVous(null);
						med.setIpUsers(null);
						med.setSchedule(null);
					});
					return ResponseEntity.ok().body(response);
				}else if (search.getVille() != null && search.getCodePostal() != null ) {
					response.setPharmacies(pharmacieRepository.findAllByVilleAndPostalCode(search.getVille(),search.getCodePostal()));				
					
					if(!search.getLangue().isEmpty()) {
						response.setPharmacies(getPharmacieByLanguage(response.getPharmacies(), search.getLangue()));
						response.getPharmacies().forEach(med->{
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
					}else if (search.getVille() == null && search.getCodePostal() != null ) {
					response.setPharmacies(pharmacieRepository.findAllByPostalCode(search.getCodePostal()));				
					
					if(!search.getLangue().isEmpty()) {
						response.setPharmacies(getPharmacieByLanguage(response.getPharmacies(), search.getLangue()));
						response.getPharmacies().forEach(med->{
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
				}
			}else 	if (search.getCategory().contains("psychologue")) {
				if (search.getVille() == null && search.getCodePostal() == null) {
					
					if(!search.getLangue().isEmpty()) {
						response.setPsychologues(getPsychologueByLanguage(response.getPsychologues(), search.getLangue()));
						response.getPsychologues().forEach(med->{
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
					response.getPsychologues().forEach(med->{
						med.setForum(null);
						med.setNotifications(null);
						med.setUsername(null);
						med.setJwt(null);
						med.setRendezVous(null);
						med.setIpUsers(null);
						med.setSchedule(null);
					});
					return ResponseEntity.ok().body(response);
				}else if (search.getVille() != null && search.getCodePostal() == null) {
					response.setPsychologues(psychologueRepository.findAllByVille(search.getVille()));				
					if(!search.getLangue().isEmpty()) {
						response.setPsychologues(getPsychologueByLanguage(response.getPsychologues(), search.getLangue()));
						response.getPsychologues().forEach(med->{
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
					response.getPsychologues().forEach(med->{
						med.setForum(null);
						med.setNotifications(null);
						med.setUsername(null);
						med.setJwt(null);
						med.setRendezVous(null);
						med.setIpUsers(null);
						med.setSchedule(null);
					});
					return ResponseEntity.ok().body(response);
				}else if (search.getVille() != null && search.getCodePostal() != null ) {
					response.setPsychologues(psychologueRepository.findAllByVilleAndPostalCode(search.getVille(),search.getCodePostal()));				
					
					if(!search.getLangue().isEmpty()) {
						response.setPsychologues(getPsychologueByLanguage(response.getPsychologues(), search.getLangue()));
						response.getPsychologues().forEach(med->{
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
					}else if (search.getVille() == null && search.getCodePostal() != null ) {
					response.setPsychologues(psychologueRepository.findAllByPostalCode(search.getCodePostal()));				
					
					if(!search.getLangue().isEmpty()) {
						response.setPsychologues(getPsychologueByLanguage(response.getPsychologues(), search.getLangue()));
						response.getPsychologues().forEach(med->{
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
				}
			}else 	if (search.getCategory().contains("kine")) {
				if (search.getVille() == null && search.getCodePostal() == null) {
					response.setKines(kineRepository.findAll());
					
					if(!search.getLangue().isEmpty()) {
						response.setKines(getKineByLanguage(response.getKines(), search.getLangue()));
						response.getKines().forEach(med->{
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
					response.getKines().forEach(med->{
						med.setForum(null);
						med.setNotifications(null);
						med.setUsername(null);
						med.setJwt(null);
						med.setRendezVous(null);
						med.setIpUsers(null);
						med.setSchedule(null);
					});
					return ResponseEntity.ok().body(response);
				}else if (search.getVille() != null && search.getCodePostal() == null) {
					response.setKines(kineRepository.findAllByVille(search.getVille()));				
					if(!search.getLangue().isEmpty()) {
						response.setKines(getKineByLanguage(response.getKines(), search.getLangue()));
						response.getKines().forEach(med->{
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
					response.getKines().forEach(med->{
						med.setForum(null);
						med.setNotifications(null);
						med.setUsername(null);
						med.setJwt(null);
						med.setRendezVous(null);
						med.setIpUsers(null);
						med.setSchedule(null);
					});
					return ResponseEntity.ok().body(response);
				}else if (search.getVille() != null && search.getCodePostal() != null ) {
					response.setKines(kineRepository.findAllByVilleAndPostalCode(search.getVille(),search.getCodePostal()));				
					
					if(!search.getLangue().isEmpty()) {
						response.setKines(getKineByLanguage(response.getKines(), search.getLangue()));
						response.getKines().forEach(med->{
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
					}else if (search.getVille() == null && search.getCodePostal() != null ) {
					response.setKines(kineRepository.findAllByPostalCode(search.getCodePostal()));				
					
					if(!search.getLangue().isEmpty()) {
						response.setKines(getKineByLanguage(response.getKines(), search.getLangue()));
						response.getKines().forEach(med->{
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
				}
			}else 	if (search.getCategory().contains("veterinaire")) {
				if (search.getVille() == null && search.getCodePostal() == null) {
					response.setVeterinaires(veterinaireRepository.findAll());	
				
					if(!search.getLangue().isEmpty()) {
						response.setVeterinaires(getVeterinaireByLanguage(response.getVeterinaires(), search.getLangue()));
						response.getVeterinaires().forEach(med->{
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
					response.getVeterinaires().forEach(med->{
						med.setForum(null);
						med.setNotifications(null);
						med.setUsername(null);
						med.setJwt(null);
						med.setRendezVous(null);
						med.setIpUsers(null);
						med.setSchedule(null);
					});
					return ResponseEntity.ok().body(response);
				}else if (search.getVille() != null && search.getCodePostal() == null) {
					response.setVeterinaires(veterinaireRepository.findAllByVille(search.getVille()));				
					if(!search.getLangue().isEmpty()) {
						response.setVeterinaires(getVeterinaireByLanguage(response.getVeterinaires(), search.getLangue()));
						response.getVeterinaires().forEach(med->{
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
					response.getVeterinaires().forEach(med->{
						med.setForum(null);
						med.setNotifications(null);
						med.setUsername(null);
						med.setJwt(null);
						med.setRendezVous(null);
						med.setIpUsers(null);
						med.setSchedule(null);
					});
					return ResponseEntity.ok().body(response);
				}else if (search.getVille() != null && search.getCodePostal() != null ) {
					response.setVeterinaires(veterinaireRepository.findAllByVilleAndPostalCode(search.getVille(),search.getCodePostal()));				
					
					if(!search.getLangue().isEmpty()) {
						response.setVeterinaires(getVeterinaireByLanguage(response.getVeterinaires(), search.getLangue()));
						response.getVeterinaires().forEach(med->{
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
					}else if (search.getVille() == null && search.getCodePostal() != null ) {
					response.setVeterinaires(veterinaireRepository.findAllByPostalCode(search.getCodePostal()));				
					
					if(!search.getLangue().isEmpty()) {
						response.setVeterinaires(getVeterinaireByLanguage(response.getVeterinaires(), search.getLangue()));
						response.getVeterinaires().forEach(med->{
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
				}
			}else 	if (search.getCategory().contains("dentiste")) {
				if (search.getVille() == null && search.getCodePostal() == null) {
				
					if(!search.getLangue().isEmpty()) {
						response.setDentistes(getDentisteByLanguage(response.getDentistes(), search.getLangue()));
						response.getDentistes().forEach(med->{
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
					response.getDentistes().forEach(med->{
						med.setForum(null);
						med.setNotifications(null);
						med.setUsername(null);
						med.setJwt(null);
						med.setRendezVous(null);
						med.setIpUsers(null);
						med.setSchedule(null);
					});
					return ResponseEntity.ok().body(response);
				}else if (search.getVille() != null && search.getCodePostal() == null) {
					response.setDentistes(dentisteRepository.findAllByVille(search.getVille()));				
					if(!search.getLangue().isEmpty()) {
						response.setDentistes(getDentisteByLanguage(response.getDentistes(), search.getLangue()));
						response.getDentistes().forEach(med->{
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
					response.getDentistes().forEach(med->{
						med.setForum(null);
						med.setNotifications(null);
						med.setUsername(null);
						med.setJwt(null);
						med.setRendezVous(null);
						med.setIpUsers(null);
						med.setSchedule(null);
					});
					return ResponseEntity.ok().body(response);
				}else if (search.getVille() != null && search.getCodePostal() != null ) {
					response.setDentistes(dentisteRepository.findAllByVilleAndPostalCode(search.getVille(),search.getCodePostal()));				
					
					if(!search.getLangue().isEmpty()) {
						response.setDentistes(getDentisteByLanguage(response.getDentistes(), search.getLangue()));
						response.getDentistes().forEach(med->{
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
					}else if (search.getVille() == null && search.getCodePostal() != null ) {
					response.setDentistes(dentisteRepository.findAllByPostalCode(search.getCodePostal()));				
					
					if(!search.getLangue().isEmpty()) {
						response.setDentistes(getDentisteByLanguage(response.getDentistes(), search.getLangue()));
						response.getDentistes().forEach(med->{
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
				}
			}else {
				response.setMedecins(medecinRepository.findAll());
				response.setKines(kineRepository.findAll());
				response.setDentistes(dentisteRepository.findAll());
				response.setPharmacies(pharmacieRepository.findAll());
				response.setPsychologues(psychologueRepository.findAll());
				response.setVeterinaires(veterinaireRepository.findAll());
			}
		response.getMedecins().forEach(med->{
			
			med.setForum(null);
			med.setNotifications(null);
			med.setUsername(null);
			med.setJwt(null);
			med.setRendezVous(null);
			med.setIpUsers(null);
			med.setSchedule(null);
		});
		response.getPharmacies().forEach(med->{
			
			med.setForum(null);
			med.setNotifications(null);
			med.setUsername(null);
			med.setJwt(null);
			med.setRendezVous(null);
			med.setIpUsers(null);
			med.setSchedule(null);
		});
		response.getVeterinaires().forEach(med->{
			
			med.setForum(null);
			med.setNotifications(null);
			med.setUsername(null);
			med.setJwt(null);
			med.setRendezVous(null);
			med.setIpUsers(null);
			med.setSchedule(null);
		});
		response.getPsychologues().forEach(med->{
			
			med.setForum(null);
			med.setNotifications(null);
			med.setUsername(null);
			med.setJwt(null);
			med.setRendezVous(null);
			med.setIpUsers(null);
			med.setSchedule(null);
		});
		response.getDentistes().forEach(med->{
			
			med.setForum(null);
			med.setNotifications(null);
			med.setUsername(null);
			med.setJwt(null);
			med.setRendezVous(null);
			med.setIpUsers(null);
			med.setSchedule(null);
		});
		response.getKines().forEach(med->{
			
			med.setForum(null);
			med.setNotifications(null);
			med.setUsername(null);
			med.setJwt(null);
			med.setRendezVous(null);
			med.setIpUsers(null);
			med.setSchedule(null);
		});
		return ResponseEntity.ok().body(response);
				
	}
	public List<Medecin> getMedecinByLanguage (List<Medecin> listePh, List<String> languageReq){
		List<Medecin> result = new ArrayList<>(); 
		listePh.forEach(ph -> {
			String lg = ph.getLanguage().toString();
			for (int i = 0 ; i < languageReq.size() ; i ++) {
				String test = languageReq.get(i);
				if (lg.matches(".*"+test+".*")) {
					if (!result.stream().anyMatch(rs -> rs.equals(ph))) {						
						result.add(ph);
					}
				}
			}
		});
		return result ;
	}
	
	public List<Pharmacie> getPharmacieByLanguage (List<Pharmacie> listePh, List<String> languageReq){
		List<Pharmacie> result = new ArrayList<>(); 
		listePh.forEach(ph -> {
			String lg = ph.getLanguage().toString();
			for (int i = 0 ; i < languageReq.size() ; i ++) {
				String test = languageReq.get(i);
				if (lg.matches(".*"+test+".*")) {
					if (!result.stream().anyMatch(rs -> rs.equals(ph))) {						
						result.add(ph);
					}
				}
			}
		});
		return result ;
	}
	public List<Kine> getKineByLanguage (List<Kine> listePh, List<String> languageReq){
		List<Kine> result = new ArrayList<>(); 
		listePh.forEach(ph -> {
			String lg = ph.getLanguage().toString();
			for (int i = 0 ; i < languageReq.size() ; i ++) {
				String test = languageReq.get(i);
				if (lg.matches(".*"+test+".*")) {
					if (!result.stream().anyMatch(rs -> rs.equals(ph))) {						
						result.add(ph);
					}
				}
			}
		});
		return result ;
	}
	public List<Psychologue> getPsychologueByLanguage (List<Psychologue> listePh, List<String> languageReq){
		List<Psychologue> result = new ArrayList<>(); 
		listePh.forEach(ph -> {
			String lg = ph.getLanguage().toString();
			for (int i = 0 ; i < languageReq.size() ; i ++) {
				String test = languageReq.get(i);
				if (lg.matches(".*"+test+".*")) {
					if (!result.stream().anyMatch(rs -> rs.equals(ph))) {						
						result.add(ph);
					}
				}
			}
		});
		return result ;
	}
	public List<Dentiste> getDentisteByLanguage (List<Dentiste> listePh, List<String> languageReq){
		List<Dentiste> result = new ArrayList<>(); 
		listePh.forEach(ph -> {
			String lg = ph.getLanguage().toString();
			for (int i = 0 ; i < languageReq.size() ; i ++) {
				String test = languageReq.get(i);
				if (lg.matches(".*"+test+".*")) {
					if (!result.stream().anyMatch(rs -> rs.equals(ph))) {						
						result.add(ph);
					}
				}
			}
		});
		return result ;
	}
	public List<Veterinaire> getVeterinaireByLanguage (List<Veterinaire> listePh, List<String> languageReq){
		List<Veterinaire> result = new ArrayList<>(); 
		listePh.forEach(ph -> {
			String lg = ph.getLanguage().toString();
			for (int i = 0 ; i < languageReq.size() ; i ++) {
				String test = languageReq.get(i);
				if (lg.matches(".*"+test+".*")) {
					if (!result.stream().anyMatch(rs -> rs.equals(ph))) {						
						result.add(ph);
					}
				}
			}
		});
		return result ;
	}
	
}

class SortDiplomeByDate implements Comparator<Diplome> {
	
    // Used for sorting in Descending order of
    // roll number
    public int compare(Diplome a,Diplome b) {
    	
		if (a.getId() < b.getId()) {
	        return -1;
	
	}
	return 0;	
    }
}
class SortExperienceByDate implements Comparator<Experience> {
	
    // Used for sorting in Descending order of
    // roll number
    public int compare(Experience a,Experience b) {
    	
		
		if (a.getId() < b.getId()) {
	        return -1;
	
	}
	return 0;	
    }
}
class SortSessionByDate implements Comparator<Session> {
	
    // Used for sorting in Descending order of
    // roll number
    public int compare(Session a,Session b)
    {	try {
		Date d1 = DateAttribute.getDate(a.getStartDate());
		Date d2 = DateAttribute.getDate(b.getStartDate());
		if (d2.before(d1))
	        return -1;
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return 0;	
    }
}
class SortPersonnesByDate implements Comparator<Personne> {
	
    // Used for sorting in Descending order of
    // roll number
    public int compare(Personne a,Personne b) {
    	
		
		if (a.id < b.id) {
	        return -1;
	
	}
	return 0;	
    }
}
class SortRdvByDate implements Comparator<PersonnelRdvs> {
	
    // Used for sorting in Descending order of
    // roll number
    public int compare(PersonnelRdvs a,PersonnelRdvs b) {
    	
		
		if (a.getId() > b.getId()) {
	        return -1;
	
	}
	return 0;	
    }
}
