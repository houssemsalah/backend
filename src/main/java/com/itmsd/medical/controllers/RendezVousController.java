package com.itmsd.medical.controllers;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itmsd.medical.entities.Dentiste;
import com.itmsd.medical.entities.Kine;
import com.itmsd.medical.entities.Medecin;
import com.itmsd.medical.entities.Notification;
import com.itmsd.medical.entities.Patient;
import com.itmsd.medical.entities.Pharmacie;
import com.itmsd.medical.entities.Psychologue;
import com.itmsd.medical.entities.RendezVous;
import com.itmsd.medical.entities.User;
import com.itmsd.medical.entities.Veterinaire;
import com.itmsd.medical.payload.request.Personne;
import com.itmsd.medical.payload.response.PersonnelRdvs;
import com.itmsd.medical.repositories.DentisteRepository;
import com.itmsd.medical.repositories.KineRepository;
import com.itmsd.medical.repositories.MedecinRepository;
import com.itmsd.medical.repositories.NotificationRepository;
import com.itmsd.medical.repositories.PatientRepository;
import com.itmsd.medical.repositories.PharmacieRepository;
import com.itmsd.medical.repositories.PsychologueRepository;
import com.itmsd.medical.repositories.RendezVousRepository;
import com.itmsd.medical.repositories.SessionRepository;
import com.itmsd.medical.repositories.UserRepository;
import com.itmsd.medical.repositories.VeterinaireRepository;
import com.itmsd.medical.services.DateAttribute;

@Transactional
@RestController
@RequestMapping("rendezvous")
public class RendezVousController {
	private UserRepository userRepository;
	private MedecinRepository medecinRepository;	
	private PatientRepository patientRepository;	
	private DentisteRepository dentisteRepository;	
	private KineRepository kineRepository;	
	private PharmacieRepository pharmacieRepository;	
	private VeterinaireRepository veterinaireRepository;	
	private PsychologueRepository psychologueRepository;
	private RendezVousRepository rendezVousRepository;
	private AuthController authController;
	private SessionRepository sessionRepository;
    private SimpMessagingTemplate template;
    private NotificationRepository notificationRepository;
    private UserController userController;
	
	@Autowired
	public RendezVousController(UserRepository userRepository,
			MedecinRepository medecinRepository, PatientRepository patientRepository,
			DentisteRepository dentisteRepository, KineRepository kineRepository,
			PharmacieRepository pharmacieRepository, VeterinaireRepository veterinaireRepository,
			PsychologueRepository psychologueRepository, RendezVousRepository rendezVousRepository,
			AuthController authController,SessionRepository sessionRepository,
			SimpMessagingTemplate template,NotificationRepository notificationRepository,
			UserController userController) {
		super();
		this.userRepository = userRepository;
		this.medecinRepository = medecinRepository;
		this.patientRepository = patientRepository;
		this.dentisteRepository = dentisteRepository;
		this.kineRepository = kineRepository;
		this.pharmacieRepository = pharmacieRepository;
		this.veterinaireRepository = veterinaireRepository;
		this.psychologueRepository = psychologueRepository;
		this.rendezVousRepository = rendezVousRepository;
		this.authController = authController;
		this.sessionRepository = sessionRepository; 
		this.template = template; 
		this.notificationRepository = notificationRepository; 
		this.userController = userController; 
	}

	
	@PostMapping("add/{patientId}/{personnelId}")
	public ResponseEntity<?> addRendezVous(@PathVariable(value = "patientId") Long patientId, 
			@PathVariable(value = "personnelId") Long personnelId,@RequestBody RendezVous rdv,HttpServletRequest request) throws Exception{
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userController.userIdFromToken(jwt);
		
		if (rdv.getStartDate()==null || rdv.getEndDate()==null) {
			throw new RuntimeException("Check the date is null !");
		}
		
		User personnel = userRepository.findUserById(personnelId);
		Patient patient = patientRepository.findById(patientId).get();
		if (userIdco != patient.getId() || userIdco != personnel.getId()) throw new RuntimeException("Security: Bad token !");
		if (personnel!=null&&patient!=null) {
			//Dentiste dent = dentisteRepository.findById(personnelId).get(); 
			rdv.getUsers().add(personnel);
			rdv.getUsers().add(patient);
			rdv.setPatientId(patient.getId());
			rdv.setPersonnelId(personnel.getId());
			String Datedata = DateAttribute.getTime(new Date());
			rdv.setCreatedAt(Datedata);
			rdv.setStatus("en cours");
			Long idRdv = rendezVousRepository.save(rdv).getId();
			personnel.getSchedule().stream().findFirst().get().getSession().forEach(sess->{
				if (sess.getStartDate().equals(rdv.getStartDate())) {
					sess.setStatus("pending");
					sess.setNbRdvs(sess.getNbRdvs()+1);
					sessionRepository.save(sess);
				}
			});
			String role = personnel.getRoles().stream().findFirst().get().getRole();
			if (role.contains("medecin") || role.contains("psychologue") || role.contains("veterinaire")
					|| role.contains("dentiste")) {
				String message = "	Bonjour Docteur, 🩺 \n \n"
						+ "Vous avez un nouveau rendez-vous de patient inscrit sur notre plateforme ! \n"
						+ "Rendez-vous le "+rdv.getDate()+" de "+rdv.getStartDate()+" à "+rdv.getEndDate()+" 🕑 \n \n"
								+ "Pour plus de détails et confirmer le rendez-vous vous pous pouvez votre liste de réservation sur votre éspace. "
						+   " \nBon travail à bientôt 🙂 !";
				
				authController.sendmail(personnel.getEmail(), message, "📅 Nouveau rendez-vous !");
			}
			Notification notification = new Notification();
			notification.setIdRdv(idRdv);
			notification.setTitle("Nouveau rendez-vous !");
			notification.setContent("Vous venez de recevoir un rendez-vous de la part de "+patient.getLastName()+" "+patient.getFirstName()+".");
			notification.setCreatedAt(Datedata);
			notification.setIdPatient(patient.getId());
			notification.setPhotoUrl(patient.getPhotoUrl());
			notification.setUser(personnel);
			notificationRepository.save(notification);
			template.convertAndSend("/topic/notifications/"+personnel.getUsername(), notification);
			return ResponseEntity.ok().body("Rendez-vous pris avec succès !");
		}
			
		return ResponseEntity.badRequest().body("Rendez-vous n'est pas pris !");	
	}
	@PostMapping("update/{rdvId}/{status}")
	public ResponseEntity<?> updateStatusRdv(@PathVariable(value = "rdvId") Long rdvId, @PathVariable(value = "status") String status,HttpServletRequest request ){
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userController.userIdFromToken(jwt);
			
			RendezVous rdv = rendezVousRepository.findById(rdvId).get();
			if (rdv.getPersonnelId()!=userIdco) throw new RuntimeException("You cannot update appointment you are not the owner !");
			User user = userRepository.findUserById(rdv.getPersonnelId());
			if (rdv != null && status.contains("confirmed"))  {
				user.getSchedule().stream().findFirst().get().getSession().forEach(sess -> {
					if (sess.getStartDate().equals(rdv.getStartDate())) {
					sess.setStatus("closed");
					sessionRepository.save(sess);
					rdv.setStatus(status);
					rendezVousRepository.save(rdv);
					}
				});
				Patient patient = patientRepository.findById(rdv.getPatientId()).get();
				Notification notification = new Notification();
				String Datedata = DateAttribute.getTime(new Date());
				notification.setIdRdv(rdv.getId());
				notification.setTitle("Nouveau rendez-vous !");
				notification.setContent("Votre rendez-vous a été confirmé de la part de médecin !");
				notification.setCreatedAt(Datedata);
				notification.setIdPatient(rdv.getPatientId());
				notification.setPhotoUrl(patient.getPhotoUrl());
				notification.setUser(userRepository.findUserById(rdv.getPatientId()));
				notificationRepository.save(notification);
				template.convertAndSend("/topic/notifications/"+patient.getUsername(), notification);
				return ResponseEntity.ok().body("Rendez-vous update succes !");
				
			}else if (rdv != null && status.contains("refused"))  {
					user.getSchedule().stream().findFirst().get().getSession().forEach(sess -> {
						if (sess.getStartDate().equals(rdv.getStartDate())) {
						sess.setNbRdvs(sess.getNbRdvs()-1);
						if (sess.getNbRdvs()<1) {
							sess.setStatus("open");
						}else sess.setStatus("en cours");
						sessionRepository.save(sess);
						rdv.setStatus(status);
						rendezVousRepository.save(rdv);
						}
					});
					Patient patient = patientRepository.findById(rdv.getPatientId()).get();
					Notification notification = new Notification();
					String Datedata = DateAttribute.getTime(new Date());
					notification.setIdRdv(rdv.getId());
					notification.setTitle("Nouveau rendez-vous !");
					notification.setContent("Désolé votre rendez-vous a été réfusé de la part de médecin !");
					notification.setCreatedAt(Datedata);
					notification.setIdPatient(rdv.getPatientId());
					notification.setPhotoUrl(patient.getPhotoUrl());
					notification.setUser(userRepository.findUserById(rdv.getPatientId()));
					notificationRepository.save(notification);
					template.convertAndSend("/topic/notifications/"+patient.getUsername(), notification);
					return ResponseEntity.ok().body("Rendez-vous update succes !");	
			}
		return ResponseEntity.badRequest().body("Failed to update !");
	}
	@GetMapping("single/{rdvId}")
	public ResponseEntity<?> getRdv(@PathVariable(value = "rdvId") Long rdvId,HttpServletRequest request ){
			String jwt = request.getHeader("Authorization").substring(7);
			Long userIdco = userController.userIdFromToken(jwt);
			RendezVous rdv = rendezVousRepository.findById(rdvId).get();
			if (rdv.getPersonnelId() == userIdco || userIdco == rdv.getPatientId()) {
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
				}else if (us.getRoles().stream().findFirst().get().getRole().contains("psychologue")) {
					Psychologue pt = psychologueRepository.findById(rdv.getPersonnelId()).get();
					Personne p = new Personne(pt.getId(),pt.getFirstName(),pt.getLastName(),
							pt.getEmail(),pt.getUsername(),pt.getVille(),pt.getAddress(),
							pt.getPostalCode(),pt.getPhoneNumber());
					perso.setDoctor(p);
				}else if (us.getRoles().stream().findFirst().get().getRole().contains("kine")) {
					Kine pt = kineRepository.findById(rdv.getPersonnelId()).get();
					Personne p = new Personne(pt.getId(),pt.getFirstName(),pt.getLastName(),
							pt.getEmail(),pt.getUsername(),pt.getVille(),pt.getAddress(),
							pt.getPostalCode(),pt.getPhoneNumber());
					perso.setDoctor(p);
				}else if (us.getRoles().stream().findFirst().get().getRole().contains("veterinaire")) {
					Veterinaire pt = veterinaireRepository.findById(rdv.getPersonnelId()).get();
					Personne p = new Personne(pt.getId(),pt.getFirstName(),pt.getLastName(),
							pt.getEmail(),pt.getUsername(),pt.getVille(),pt.getAddress(),
							pt.getPostalCode(),pt.getPhoneNumber());
					perso.setDoctor(p);
				}else if (us.getRoles().stream().findFirst().get().getRole().contains("dentiste")) {
					Dentiste pt = dentisteRepository.findById(rdv.getPersonnelId()).get();
					Personne p = new Personne(pt.getId(),pt.getFirstName(),pt.getLastName(),
							pt.getEmail(),pt.getUsername(),pt.getVille(),pt.getAddress(),
							pt.getPostalCode(),pt.getPhoneNumber());
					perso.setDoctor(p);
				}else if (us.getRoles().stream().findFirst().get().getRole().contains("pharmacie")) {
					Pharmacie pt = pharmacieRepository.findById(rdv.getPersonnelId()).get();
					Personne p = new Personne(pt.getId(),pt.getName(),
							pt.getEmail(),pt.getUsername(),pt.getVille(),pt.getAddress(),
							pt.getPostalCode(),pt.getPhoneNumber());
					perso.setDoctor(p);
				}
				
					Patient pt = patientRepository.findById(rdv.getPatientId()).get();
					Personne p = new Personne(pt.getId(),pt.getFirstName(),pt.getLastName(),
							pt.getEmail(),pt.getUsername(),pt.getVille(),pt.getAddress(),
							pt.getPostalCode(),pt.getPhoneNumber());
					perso.setPatient(p);
				
					return ResponseEntity.ok().body(perso);
			}
	
			return ResponseEntity.badRequest().body("Bad token !");
				
	}
	
}
