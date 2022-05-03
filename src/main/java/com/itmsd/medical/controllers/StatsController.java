package com.itmsd.medical.controllers;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.itmsd.medical.entities.Dentiste;
import com.itmsd.medical.entities.Kine;
import com.itmsd.medical.entities.Medecin;
import com.itmsd.medical.entities.Patient;
import com.itmsd.medical.entities.Pharmacie;
import com.itmsd.medical.entities.Psychologue;
import com.itmsd.medical.entities.RendezVous;
import com.itmsd.medical.entities.User;
import com.itmsd.medical.entities.Veterinaire;
import com.itmsd.medical.payload.request.Personne;
import com.itmsd.medical.repositories.DentisteRepository;
import com.itmsd.medical.repositories.KineRepository;
import com.itmsd.medical.repositories.MedecinRepository;
import com.itmsd.medical.repositories.PatientRepository;
import com.itmsd.medical.repositories.PharmacieRepository;
import com.itmsd.medical.repositories.PsychologueRepository;
import com.itmsd.medical.repositories.RendezVousRepository;
import com.itmsd.medical.repositories.UserRepository;
import com.itmsd.medical.repositories.VeterinaireRepository;
import com.itmsd.medical.services.DateAttribute;


@RestController
@RequestMapping("stats")
public class StatsController {
	
	private UserRepository userRepository;
	private RendezVousRepository rendezVousRepository;
	private PatientRepository patientRepository;	
	private MedecinRepository medecinRepository;	
	private DentisteRepository dentisteRepository;	
	private KineRepository kineRepository;	
	private PharmacieRepository pharmacieRepository;	
	private VeterinaireRepository veterinaireRepository;	
	private PsychologueRepository psychologueRepository;
	
	@Autowired
	public StatsController(UserRepository userRepository, RendezVousRepository rendezVousRepository,
			PatientRepository patientRepository, MedecinRepository medecinRepository,
			DentisteRepository dentisteRepository, KineRepository kineRepository,
			PharmacieRepository pharmacieRepository, VeterinaireRepository veterinaireRepository,
			PsychologueRepository psychologueRepository) {
		super();
		this.userRepository = userRepository;
		this.rendezVousRepository = rendezVousRepository;
		this.patientRepository = patientRepository;
		this.medecinRepository = medecinRepository;
		this.dentisteRepository = dentisteRepository;
		this.kineRepository = kineRepository;
		this.pharmacieRepository = pharmacieRepository;
		this.veterinaireRepository = veterinaireRepository;
		this.psychologueRepository = psychologueRepository;
	}
	
	@GetMapping("/info")
	public Stats getStats() {
		Stats stats = new Stats();
		//list praticiens
		//nb praticiens = prat - nb deleted
		List<User> users = userRepository.findAll();
		stats.nbTotalUsers = users.size();
		List<Patient> patients = patientRepository.findAll();
		stats.nbPatients = patients.size();
		
		stats.nbMedecin = medecinRepository.findAll().size();
		stats.nbKine = kineRepository.findAll().size();
		stats.nbPharmacie = pharmacieRepository.findAll().size();
		stats.nbPsychologue = psychologueRepository.findAll().size();
		stats.nbDentiste = dentisteRepository.findAll().size();
		stats.nbVeterinaire = veterinaireRepository.findAll().size();
		stats.nbPraticiens = stats.nbVeterinaire + stats.nbDentiste + stats.nbPsychologue +
				stats.nbPharmacie + stats.nbKine + stats.nbMedecin;
		stats.nbTotalRdv = rendezVousRepository.findAll().size();
		
		rendezVousRepository.findAll().forEach(rdv -> {
			if (rdv.getStatus().equals("en cours")) {
				stats.nbPendingRdv++;
			}else if (rdv.getStatus().equals("confirmed")) {
				stats.nbConfirmedRdv++;
			}else if (rdv.getStatus().equals("refused")) {
				stats.nbRefusedRdv++;
			}
		});
		if (!users.isEmpty()) {
			users.forEach(usr ->{
				if (usr.getOnline().contains("online")) {
					stats.nbOnlineUsers++;
				}
			});
		}
		
		List<Patient> patientsList = patientRepository.findAll();
		
		patientsList.forEach(patient -> {
			Date d2 = null;
			try {
				d2 = DateAttribute.getDate(patient.getCreatedAt());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Calendar cp = Calendar.getInstance();
			cp.setTime(d2);
			Date currentDate = new Date();
			Calendar dd = Calendar.getInstance();
			dd.setTime(currentDate);
		
			if (cp.get(Calendar.YEAR) == dd.get(Calendar.YEAR)) {
				if (cp.get(Calendar.MONTH) == 0) stats.newPatients.set(0, stats.newPatients.get(0)+1);
				if (cp.get(Calendar.MONTH) == 1) stats.newPatients.set(1, stats.newPatients.get(1)+1);
				if (cp.get(Calendar.MONTH) == 2) stats.newPatients.set(2, stats.newPatients.get(2)+1) ;
				if (cp.get(Calendar.MONTH) == 3) stats.newPatients.set(3, stats.newPatients.get(3)+1);
				if (cp.get(Calendar.MONTH) == 4) stats.newPatients.set(4, stats.newPatients.get(4)+1);
				if (cp.get(Calendar.MONTH) == 5) stats.newPatients.set(5, stats.newPatients.get(5)+1);
				if (cp.get(Calendar.MONTH) == 6) stats.newPatients.set(6, stats.newPatients.get(6)+1);
				if (cp.get(Calendar.MONTH) == 7) stats.newPatients.set(7, stats.newPatients.get(7)+1);
				if (cp.get(Calendar.MONTH) == 8) stats.newPatients.set(8, stats.newPatients.get(8)+1);
				if (cp.get(Calendar.MONTH) == 9) stats.newPatients.set(9, stats.newPatients.get(9)+1);
				if (cp.get(Calendar.MONTH) == 10) stats.newPatients.set(10, stats.newPatients.get(10)+1);
				if (cp.get(Calendar.MONTH) == 11) stats.newPatients.set(11, stats.newPatients.get(11)+1);					
			}
		});
		
		List<RendezVous> rdvsList = rendezVousRepository.findAll();
		rdvsList.forEach(rdv -> {
			Date d2 = null;
			try {
				d2 = DateAttribute.getDate(rdv.getCreatedAt());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Calendar cp = Calendar.getInstance();
			cp.setTime(d2);
			Date currentDate = new Date();
			Calendar dd = Calendar.getInstance();
			dd.setTime(currentDate);
		
			if (cp.get(Calendar.YEAR) == dd.get(Calendar.YEAR)) {
				if (cp.get(Calendar.MONTH) == 0) stats.newRdv.set(0, stats.newRdv.get(0)+1);
				if (cp.get(Calendar.MONTH) == 1) stats.newRdv.set(1, stats.newRdv.get(1)+1);
				if (cp.get(Calendar.MONTH) == 2) stats.newRdv.set(2, stats.newRdv.get(2)+1) ;
				if (cp.get(Calendar.MONTH) == 3) stats.newRdv.set(3, stats.newRdv.get(3)+1);
				if (cp.get(Calendar.MONTH) == 4) stats.newRdv.set(4, stats.newRdv.get(4)+1);
				if (cp.get(Calendar.MONTH) == 5) stats.newRdv.set(5, stats.newRdv.get(5)+1);
				if (cp.get(Calendar.MONTH) == 6) stats.newRdv.set(6, stats.newRdv.get(6)+1);
				if (cp.get(Calendar.MONTH) == 7) stats.newRdv.set(7, stats.newRdv.get(7)+1);
				if (cp.get(Calendar.MONTH) == 8) stats.newRdv.set(8, stats.newRdv.get(8)+1);
				if (cp.get(Calendar.MONTH) == 9) stats.newRdv.set(9, stats.newRdv.get(9)+1);
				if (cp.get(Calendar.MONTH) == 10) stats.newRdv.set(10, stats.newRdv.get(10)+1);
				if (cp.get(Calendar.MONTH) == 11) stats.newRdv.set(11, stats.newRdv.get(11)+1);					
			}
		});
		if (medecinRepository.count()>0) {
		List<Medecin> topMedecin = medecinRepository.findTopPraticien();
		stats.topMedecin = new Personne();
		stats.topMedecin.id = topMedecin.get(0).getId();
		stats.topMedecin.firstName = topMedecin.get(0).getFirstName();
		stats.topMedecin.lastName = topMedecin.get(0).getLastName();
		stats.topMedecin.createdAt= topMedecin.get(0).getCreatedAt();
		stats.topMedecin.nbRdv= topMedecin.get(0).getNbRdv();
		stats.topMedecin.status= topMedecin.get(0).getOnline();
		}
		if (psychologueRepository.count()>0) {
		List<Psychologue> topPsychologue= psychologueRepository.findTopPraticien();
			stats.topPsychologue = new Personne();
			stats.topPsychologue.id = topPsychologue.get(0).getId();
			stats.topPsychologue.firstName = topPsychologue.get(0).getFirstName();
			stats.topPsychologue.lastName = topPsychologue.get(0).getLastName();
			stats.topPsychologue.createdAt= topPsychologue.get(0).getCreatedAt();
			stats.topPsychologue.nbRdv= topPsychologue.get(0).getNbRdv();
			stats.topPsychologue.status= topPsychologue.get(0).getOnline();
		}
		if (pharmacieRepository.count()>0) {
		List<Pharmacie> topPharmacie= pharmacieRepository.findTopPraticien();
		stats.topPharmacie = new Personne();
		stats.topPharmacie.id = topPharmacie.get(0).getId();
		stats.topPharmacie.name = topPharmacie.get(0).getName();
		stats.topPharmacie.createdAt= topPharmacie.get(0).getCreatedAt();
		stats.topPharmacie.nbRdv= topPharmacie.get(0).getNbRdv();
		stats.topPharmacie.status= topPharmacie.get(0).getOnline();
		}
		if (kineRepository.count()>0) {
		List<Kine> topKine= kineRepository.findTopPraticien();
		stats.topKine = new Personne();
		stats.topKine.id = topKine.get(0).getId();
		stats.topKine.firstName = topKine.get(0).getFirstName();
		stats.topKine.lastName = topKine.get(0).getLastName();
		stats.topKine.createdAt= topKine.get(0).getCreatedAt();
		stats.topKine.nbRdv= topKine.get(0).getNbRdv();
		stats.topKine.status= topKine.get(0).getOnline();
		}

		if (veterinaireRepository.count()>0) {
		List<Veterinaire> topVeterinaire= veterinaireRepository.findTopPraticien();
		stats.topVeterinaire = new Personne();
		stats.topVeterinaire.id = topVeterinaire.get(0).getId();
		stats.topVeterinaire.firstName = topVeterinaire.get(0).getFirstName();
		stats.topVeterinaire.lastName = topVeterinaire.get(0).getLastName();
		stats.topVeterinaire.createdAt= topVeterinaire.get(0).getCreatedAt();
		stats.topVeterinaire.nbRdv= topVeterinaire.get(0).getNbRdv();
		stats.topVeterinaire.status= topVeterinaire.get(0).getOnline();
		}
		if (dentisteRepository.count()>0) {
		List<Dentiste> topDentiste= dentisteRepository.findTopPraticien();
		stats.topDentiste = new Personne();
		stats.topDentiste.id = topDentiste.get(0).getId();
		stats.topDentiste.firstName = topDentiste.get(0).getFirstName();
		stats.topDentiste.lastName = topDentiste.get(0).getLastName();
		stats.topDentiste.createdAt= topDentiste.get(0).getCreatedAt();
		stats.topDentiste.nbRdv= topDentiste.get(0).getNbRdv();
		stats.topDentiste.status= topDentiste.get(0).getOnline();
		}
		return stats;
	}

	public class Stats {
		public long nbVisitors;
		public int nbTotalUsers;
		public int nbPatients;
		public int nbOnlineUsers;
		
		public int nbPraticiens;
		public int nbMedecin;
		public int nbKine;
		public int nbPsychologue;
		public int nbVeterinaire;
		public int nbDentiste;
		public int nbPharmacie;
		
		public int nbTotalRdv;
		public int nbConfirmedRdv;
		public int nbRefusedRdv;
		public int nbPendingRdv;
		
		@JsonIgnoreProperties(value={"phoneNumber"})
		public Personne topMedecin;
		@JsonIgnoreProperties(value={"phoneNumber"})
		public Personne topPsychologue;
		@JsonIgnoreProperties(value={"phoneNumber"})
		public Personne topKine;
		@JsonIgnoreProperties(value={"phoneNumber"})
		public Personne topPharmacie;
		@JsonIgnoreProperties(value={"phoneNumber"})
		public Personne topVeterinaire;
		@JsonIgnoreProperties(value={"phoneNumber"})
		public Personne topDentiste;
		
		public ArrayList<Integer> newPatients = new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,0,0,0,0));
		public ArrayList<Integer> newRdv = new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,0,0,0,0));

		public Stats(long nbVisitors, int nbTotalUsers, int nbPraticiens, int nbPatients, int nbOnlineUsers,
				int nbTotalRdv, int nbMedecin, int nbKine, int nbPsychologue, int nbVeterinaire, int nbDentiste,
				int nbPharmacie, int nbConfirmedRdv, int nbRefusedRdv, int nbPendingRdv, Personne topMedecin,
				Personne topPsychologue, Personne topKine, Personne topPharmacie, Personne topVeterinaire,
				Personne topDentiste, ArrayList<Integer> newPatients, ArrayList<Integer> newRdv) {
			super();
			this.nbVisitors = nbVisitors;
			this.nbTotalUsers = nbTotalUsers;
			this.nbPraticiens = nbPraticiens;
			this.nbPatients = nbPatients;
			this.nbOnlineUsers = nbOnlineUsers;
			this.nbTotalRdv = nbTotalRdv;
			this.nbMedecin = nbMedecin;
			this.nbKine = nbKine;
			this.nbPsychologue = nbPsychologue;
			this.nbVeterinaire = nbVeterinaire;
			this.nbDentiste = nbDentiste;
			this.nbPharmacie = nbPharmacie;
			this.nbConfirmedRdv = nbConfirmedRdv;
			this.nbRefusedRdv = nbRefusedRdv;
			this.nbPendingRdv = nbPendingRdv;
			this.topMedecin = topMedecin;
			this.topPsychologue = topPsychologue;
			this.topKine = topKine;
			this.topPharmacie = topPharmacie;
			this.topVeterinaire = topVeterinaire;
			this.topDentiste = topDentiste;
			this.newPatients = newPatients;
			this.newRdv = newRdv;
		}

		public Stats() {
			super();
			// TODO Auto-generated constructor stub
		}

	}

}
