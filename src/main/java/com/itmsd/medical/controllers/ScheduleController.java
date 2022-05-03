package com.itmsd.medical.controllers;

import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itmsd.medical.entities.Dentiste;
import com.itmsd.medical.entities.Kine;
import com.itmsd.medical.entities.Medecin;
import com.itmsd.medical.entities.Pharmacie;
import com.itmsd.medical.entities.Psychologue;
import com.itmsd.medical.entities.Schedule;
import com.itmsd.medical.entities.Session;
import com.itmsd.medical.entities.User;
import com.itmsd.medical.entities.Veterinaire;
import com.itmsd.medical.repositories.DentisteRepository;
import com.itmsd.medical.repositories.KineRepository;
import com.itmsd.medical.repositories.MedecinRepository;
import com.itmsd.medical.repositories.PharmacieRepository;
import com.itmsd.medical.repositories.PsychologueRepository;
import com.itmsd.medical.repositories.RoleRepository;
import com.itmsd.medical.repositories.ScheduleRepository;
import com.itmsd.medical.repositories.SessionRepository;
import com.itmsd.medical.repositories.UserRepository;
import com.itmsd.medical.repositories.VeterinaireRepository;
import com.itmsd.medical.services.DateAttribute;

@Transactional
@RestController
@RequestMapping("schedule")
public class ScheduleController {
	private UserRepository userRepository;
	private RoleRepository roleRepository; 
	private MedecinRepository medecinRepository;	
	private DentisteRepository dentisteRepository;	
	private KineRepository kineRepository;	
	private PharmacieRepository pharmacieRepository;	
	private VeterinaireRepository veterinaireRepository;	
	private PsychologueRepository psychologueRepository;
	private ScheduleRepository scheduleRepository;
	private SessionRepository sessionRepository;
	private UserController userController;
	
	@Autowired
	public ScheduleController(UserRepository userRepository, RoleRepository roleRepository,
			MedecinRepository medecinRepository,
			DentisteRepository dentisteRepository, KineRepository kineRepository,
			PharmacieRepository pharmacieRepository, VeterinaireRepository veterinaireRepository,
			PsychologueRepository psychologueRepository, ScheduleRepository scheduleRepository,
			SessionRepository sessionRepository, UserController userController) {
		super();
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.medecinRepository = medecinRepository;
		this.dentisteRepository = dentisteRepository;
		this.kineRepository = kineRepository;
		this.pharmacieRepository = pharmacieRepository;
		this.veterinaireRepository = veterinaireRepository;
		this.psychologueRepository = psychologueRepository;
		this.scheduleRepository = scheduleRepository;
		this.sessionRepository = sessionRepository;
		this.userController = userController;
	}
	
	@PostMapping("add/{userId}")
	public ResponseEntity<?> addSchedule(@PathVariable(value = "userId") Long userId, @RequestBody Session session,
			HttpServletRequest request){
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userController.userIdFromToken(jwt);
		User user = userRepository.findUserById(userIdco);
		if (userIdco != userId) throw new RuntimeException("Security: bad token !");
		
		if (user.getRoles().contains(roleRepository.findByRole("patient"))) {
			return ResponseEntity.badRequest().body("Patient cannot add schedule !");
			
		}else if (user.getRoles().contains(roleRepository.findByRole("dentiste"))) {
				Dentiste dent = dentisteRepository.findById(userId).get();
				dent.getSchedule().stream().findFirst().get().getSession().forEach(sess ->{
					try {
						if (DateAttribute.getDate(sess.getStartDate()).equals(DateAttribute.getDate(session.getStartDate()))
								|| DateAttribute.getDate(session.getStartDate()).before(DateAttribute.getDate(sess.getEndDate()))
								|| DateAttribute.getDate(session.getEndDate()).before(DateAttribute.getDate(session.getStartDate()))) {
							throw new RuntimeException("You must verify your start time session ! ");
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
				if (dent.getSchedule().isEmpty()) {
					Schedule newSch = new Schedule();
					newSch.setMedecin(dent);
					session.setStatus("open");
					session.setSchedule(scheduleRepository.save(newSch));
					
				}else {
					Schedule old ;
					old = dent.getSchedule().stream().findFirst().get();
					session.setSchedule(old);
					session.setStatus("open");				}
				return ResponseEntity.ok().body(sessionRepository.save(session));
				
		 }else if (user.getRoles().contains(roleRepository.findByRole("medecin"))) {
				Medecin dent = medecinRepository.findById(userId).get();
					dent.getSchedule().stream().findFirst().get().getSession().forEach(sess ->{
						try {
							if (DateAttribute.getDate(sess.getStartDate()).equals(DateAttribute.getDate(session.getStartDate()))
									|| DateAttribute.getDate(session.getStartDate()).before(DateAttribute.getDate(sess.getEndDate()))
									|| DateAttribute.getDate(session.getEndDate()).before(DateAttribute.getDate(session.getStartDate()))) {
								throw new RuntimeException("You must verify your start time session ! ");
							}
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					});
				if (dent.getSchedule().isEmpty()) {
					Schedule newSch = new Schedule();
					newSch.setMedecin(dent);
					session.setStatus("open");
					session.setSchedule(scheduleRepository.save(newSch));
				}else {
					Schedule old ;
					old = dent.getSchedule().stream().findFirst().get();
					session.setSchedule(old);
					session.setStatus("open");
				}
				return ResponseEntity.ok().body(sessionRepository.save(session));
				
		 }else if (user.getRoles().contains(roleRepository.findByRole("psychologue"))) {
				Psychologue dent = psychologueRepository.findById(userId).get();
				dent.getSchedule().stream().findFirst().get().getSession().forEach(sess ->{
					try {
						if (DateAttribute.getDate(sess.getStartDate()).equals(DateAttribute.getDate(session.getStartDate()))
								|| DateAttribute.getDate(session.getStartDate()).before(DateAttribute.getDate(sess.getEndDate()))
								|| DateAttribute.getDate(session.getEndDate()).before(DateAttribute.getDate(session.getStartDate()))) {
							throw new RuntimeException("You must verify your start time session ! ");
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
				if (dent.getSchedule().isEmpty()) {
					Schedule newSch = new Schedule();
					newSch.setMedecin(dent);
					session.setStatus("open");
					session.setSchedule(scheduleRepository.save(newSch));
				}else {
					Schedule old ;
					old = dent.getSchedule().stream().findFirst().get();
					session.setSchedule(old);
					session.setStatus("open");
				}
				return ResponseEntity.ok().body(sessionRepository.save(session));
				
		 }else if (user.getRoles().contains(roleRepository.findByRole("pharmacie"))) {
				Pharmacie dent = pharmacieRepository.findById(userId).get();
				dent.getSchedule().stream().findFirst().get().getSession().forEach(sess ->{
					try {
						if (DateAttribute.getDate(sess.getStartDate()).equals(DateAttribute.getDate(session.getStartDate()))
								|| DateAttribute.getDate(session.getStartDate()).before(DateAttribute.getDate(sess.getEndDate()))
								|| DateAttribute.getDate(session.getEndDate()).before(DateAttribute.getDate(session.getStartDate()))) {
							throw new RuntimeException("You must verify your start time session ! ");
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
				if (dent.getSchedule().isEmpty()) {
					Schedule newSch = new Schedule();
					newSch.setMedecin(dent);
					session.setStatus("open");
					session.setSchedule(scheduleRepository.save(newSch));
				}else {
					Schedule old ;
					old = dent.getSchedule().stream().findFirst().get();
					session.setSchedule(old);
					session.setStatus("open");
				}
				return ResponseEntity.ok().body(sessionRepository.save(session));
				
		 }else if (user.getRoles().contains(roleRepository.findByRole("kine"))) {
				Kine dent = kineRepository.findById(userId).get();
				dent.getSchedule().stream().findFirst().get().getSession().forEach(sess ->{
					try {
						if (DateAttribute.getDate(sess.getStartDate()).equals(DateAttribute.getDate(session.getStartDate()))
								|| DateAttribute.getDate(session.getStartDate()).before(DateAttribute.getDate(sess.getEndDate()))
								|| DateAttribute.getDate(session.getEndDate()).before(DateAttribute.getDate(session.getStartDate()))) {
							throw new RuntimeException("You must verify your start time session ! ");
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
				if (dent.getSchedule().isEmpty()) {
					Schedule newSch = new Schedule();
					newSch.setMedecin(dent);
					session.setStatus("open");
					session.setSchedule(scheduleRepository.save(newSch));
				}else {
					Schedule old ;
					old = dent.getSchedule().stream().findFirst().get();
					session.setSchedule(old);
					session.setStatus("open");
				}
				return ResponseEntity.ok().body(sessionRepository.save(session));
				
		 }else if (user.getRoles().contains(roleRepository.findByRole("veterinaire"))) {
				Veterinaire dent = veterinaireRepository.findById(userId).get();
				dent.getSchedule().stream().findFirst().get().getSession().forEach(sess ->{
					try {
						if (DateAttribute.getDate(sess.getStartDate()).equals(DateAttribute.getDate(session.getStartDate()))
								|| DateAttribute.getDate(session.getStartDate()).before(DateAttribute.getDate(sess.getEndDate()))
								|| DateAttribute.getDate(session.getEndDate()).before(DateAttribute.getDate(session.getStartDate()))) {
							throw new RuntimeException("You must verify your start time session ! ");
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
				if (dent.getSchedule().isEmpty()) {
					Schedule newSch = new Schedule();
					newSch.setMedecin(dent);
					session.setStatus("open");
					session.setSchedule(scheduleRepository.save(newSch));
				}else {
					Schedule old ;
					old = dent.getSchedule().stream().findFirst().get();
					session.setSchedule(old);
					session.setStatus("open");
				}
				return ResponseEntity.ok().body(sessionRepository.save(session));
				
		 }
		
		return ResponseEntity.badRequest().body("Bad user id !");
	}
	
	
	

}
