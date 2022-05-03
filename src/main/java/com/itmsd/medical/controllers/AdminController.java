package com.itmsd.medical.controllers;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itmsd.medical.entities.Dentiste;
import com.itmsd.medical.entities.Kine;
import com.itmsd.medical.entities.Medecin;
import com.itmsd.medical.entities.Pharmacie;
import com.itmsd.medical.entities.Psychologue;
import com.itmsd.medical.entities.User;
import com.itmsd.medical.entities.Veterinaire;
import com.itmsd.medical.repositories.DentisteRepository;
import com.itmsd.medical.repositories.DiplomeRepository;
import com.itmsd.medical.repositories.ExperienceRepository;
import com.itmsd.medical.repositories.KineRepository;
import com.itmsd.medical.repositories.MedecinRepository;
import com.itmsd.medical.repositories.NotificationRepository;
import com.itmsd.medical.repositories.PharmacieRepository;
import com.itmsd.medical.repositories.PsychologueRepository;
import com.itmsd.medical.repositories.ScheduleRepository;
import com.itmsd.medical.repositories.SessionRepository;
import com.itmsd.medical.repositories.UserRepository;
import com.itmsd.medical.repositories.VeterinaireRepository;

@Transactional
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/admin")
public class AdminController {
	
	private UserRepository userRepository;
	private ExperienceRepository experienceRepository;
	private DiplomeRepository diplomeRepository;
	private NotificationRepository notificationRepository;
	private SessionRepository sessionRepository;
	private ScheduleRepository scheduleRepository;
	private MedecinRepository medecinRepository;	
	private DentisteRepository dentisteRepository;	
	private KineRepository kineRepository;	
	private PharmacieRepository pharmacieRepository;	
	private VeterinaireRepository veterinaireRepository;	
	private PsychologueRepository psychologueRepository;
	
	@Autowired
	public AdminController(UserRepository userRepository, ExperienceRepository experienceRepository,
			DiplomeRepository diplomeRepository, NotificationRepository notificationRepository,
			SessionRepository sessionRepository, ScheduleRepository scheduleRepository,
			MedecinRepository medecinRepository, DentisteRepository dentisteRepository,
			KineRepository kineRepository, PharmacieRepository pharmacieRepository,
			VeterinaireRepository veterinaireRepository, PsychologueRepository psychologueRepository) {
		super();
		this.userRepository = userRepository;
		this.experienceRepository = experienceRepository;
		this.diplomeRepository = diplomeRepository;
		this.notificationRepository = notificationRepository;
		this.sessionRepository = sessionRepository;
		this.scheduleRepository = scheduleRepository;
		this.medecinRepository = medecinRepository;
		this.dentisteRepository = dentisteRepository;
		this.kineRepository = kineRepository;
		this.pharmacieRepository = pharmacieRepository;
		this.veterinaireRepository = veterinaireRepository;
		this.psychologueRepository = psychologueRepository;
	}
	
	@PostMapping("/ban/{userId}")
	public String banUser(@PathVariable (value = "userId") Long userId) {
			User user = userRepository.findUserById(userId);
			if (user!=null) {
				user.setBanned(true);
				userRepository.save(user);
			}
			
		return "user has been banned";
	}

	@PostMapping("/unban/{userId}")
	public String unbanUser(@PathVariable (value = "userId") Long userId) {
			User user = userRepository.findUserById(userId);
			if (user!=null) {
				user.setBanned(false);
				userRepository.save(user);
			}
			
		return "user has been unbanned";
	}
	
	@PostMapping("/delete/{userId}")
	public ResponseEntity<?> deleteUser(@PathVariable (value = "userId") Long userId) {
		
		return userRepository.findById(userId).map(user -> {
			user.removeForum();
			user.getExperiences().forEach(exp -> {
				experienceRepository.delete(exp);
			});
			user.getDiplomes().forEach(dip->{
				diplomeRepository.delete(dip);
			});
			user.getNotifications().forEach(not->{
				notificationRepository.delete(not);
			});
			user.getSchedule().forEach(sch->{
				sch.getSession().forEach(sess->{
					sessionRepository.delete(sess);
					
				});
				scheduleRepository.delete(sch);
			});
			
			user.setSpeciality(null);
			user.setVille(null);
			user.setPassword(null);
			user.setPasswordConfirmation(null);
			user.setDescription(null);
			user.setInami(null);
			user.setConfirmationToken(null);
			user.setJwt(null);
			user.setOnline("offline");
			user.setDeleted(true);
			user.setPhotoUrl("https://res.cloudinary.com/bilel-moussa/image/upload/v1644533163/user-icon-human-person-sign-vector-10206693_hs5pi9.png");
			userRepository.save(user);
			if (user.getRoles().stream().findFirst().get().getRole().contains("medecin")) {
				Medecin perso = medecinRepository.findById(userId).get();
				perso.setFirstName("indisponible");
				perso.setLastName("Utilisateur");
				perso.setNbRdv(0);
				perso.setLanguage(new ArrayList<String>());
				perso.setPhotoCabinet(new ArrayList<String>());
			}else if (user.getRoles().stream().findFirst().get().getRole().contains("psychologue")) {
				Psychologue perso = psychologueRepository.findById(userId).get();
				perso.setFirstName("indisponible");
				perso.setLastName("Utilisateur");
				perso.setNbRdv(0);
				perso.setLanguage(new ArrayList<String>());
				perso.setPhotoCabinet(new ArrayList<String>());
			}else if (user.getRoles().stream().findFirst().get().getRole().contains("kine")) {
				Kine perso = kineRepository.findById(userId).get();
				perso.setFirstName("indisponible");
				perso.setLastName("Utilisateur");
				perso.setNbRdv(0);
				perso.setLanguage(new ArrayList<String>());
				perso.setPhotoCabinet(new ArrayList<String>());
			}else if (user.getRoles().stream().findFirst().get().getRole().contains("dentiste")) {
				Dentiste perso = dentisteRepository.findById(userId).get();
				perso.setFirstName("indisponible");
				perso.setLastName("Utilisateur");
				perso.setNbRdv(0);
				perso.setLanguage(new ArrayList<String>());
				perso.setPhotoCabinet(new ArrayList<String>());
			}else if (user.getRoles().stream().findFirst().get().getRole().contains("veterinaire")) {
				Veterinaire perso = veterinaireRepository.findById(userId).get();
				perso.setFirstName("indisponible");
				perso.setLastName("Utilisateur");
				perso.setNbRdv(0);
				perso.setLanguage(new ArrayList<String>());
				perso.setPhotoCabinet(new ArrayList<String>());
			}else if (user.getRoles().stream().findFirst().get().getRole().contains("pharmacie")) {
				Pharmacie perso = pharmacieRepository.findById(userId).get();
				perso.setName("Utilisateur indisponible");
				perso.setNbRdv(0);
				perso.setLanguage(new ArrayList<String>());
				perso.setPhotoPharmacie(new ArrayList<String>());
			}
			userRepository.save(user);
			return ResponseEntity.ok().body("User "+userId+" has been deleted");
		}).orElseThrow(() -> new IllegalArgumentException("userId " + userId + " not found"));
	}
}
