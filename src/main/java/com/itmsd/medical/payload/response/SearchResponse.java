package com.itmsd.medical.payload.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.itmsd.medical.entities.Dentiste;
import com.itmsd.medical.entities.Kine;
import com.itmsd.medical.entities.Medecin;
import com.itmsd.medical.entities.Pharmacie;
import com.itmsd.medical.entities.Psychologue;
import com.itmsd.medical.entities.Veterinaire;

@JsonInclude(Include.NON_NULL)
public class SearchResponse {
	
	private List<Pharmacie> pharmacies;
	private List<Psychologue> psychologues;
	private List<Kine> kines;
	private List<Veterinaire> veterinaires;
	private List<Dentiste> dentistes;
	private List<Medecin> medecins;
	
	public List<Pharmacie> getPharmacies() {
		return pharmacies;
	}
	public void setPharmacies(List<Pharmacie> pharmacies) {
		this.pharmacies = pharmacies;
	}
	public List<Psychologue> getPsychologues() {
		return psychologues;
	}
	public void setPsychologues(List<Psychologue> psychologues) {
		this.psychologues = psychologues;
	}
	public List<Kine> getKines() {
		return kines;
	}
	public void setKines(List<Kine> kines) {
		this.kines = kines;
	}
	public List<Veterinaire> getVeterinaires() {
		return veterinaires;
	}
	public void setVeterinaires(List<Veterinaire> veterinaires) {
		this.veterinaires = veterinaires;
	}
	public List<Dentiste> getDentistes() {
		return dentistes;
	}
	public void setDentistes(List<Dentiste> dentistes) {
		this.dentistes = dentistes;
	}
	public List<Medecin> getMedecins() {
		return medecins;
	}
	public void setMedecins(List<Medecin> medecins) {
		this.medecins = medecins;
	}
	
}
