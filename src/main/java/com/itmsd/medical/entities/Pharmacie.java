package com.itmsd.medical.entities;

import java.util.ArrayList;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties({"speciality"})
public class Pharmacie extends User {
	
	@Column(name = "name")
	@NotEmpty(message = "*Please provide your firstname")
	private String name;

	@Column(name = "address")
	private String address ;
	
	@Column(name = "postal_code")
	private Integer postalCode;
	
	@Column(name = "language")
	private ArrayList<String> language = new ArrayList<>();
	
	@Column(name = "images_pharmacie")
	private ArrayList<String> photoPharmacie = new ArrayList<>();
	
	@Column(name = "nb_rdv")
	private Integer nbRdv = 0;
	
	public Pharmacie() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(Integer postalCode) {
		this.postalCode = postalCode;
	}

	public ArrayList<String> getLanguage() {
		return language;
	}

	public void setLanguage(ArrayList<String> language) {
		this.language = language;
	}

	public ArrayList<String> getPhotoPharmacie() {
		return photoPharmacie;
	}

	public void setPhotoPharmacie(ArrayList<String> photoPharmacie) {
		this.photoPharmacie = photoPharmacie;
	}

	public Integer getNbRdv() {
		return nbRdv;
	}

	public void setNbRdv(Integer nbRdv) {
		this.nbRdv = nbRdv;
	}
}
