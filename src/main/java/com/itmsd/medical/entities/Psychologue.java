package com.itmsd.medical.entities;

import java.util.ArrayList;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotEmpty;

@Entity
public class Psychologue extends User {
	
	@Column(name = "first_name")
	@NotEmpty(message = "*Please provide your firstname")
	private String firstName;
	
	@Column(name = "last_name")
	@NotEmpty(message = "*Please provide your last name")
	private String lastName;
	
	@Column(name = "address")
	private String address ;
	
	@Column(name = "postal_code")
	private Integer postalCode;
	
	@Column(name = "language")
	private ArrayList<String> language = new ArrayList<>();

	@Column(name = "images_cabinet")
	private ArrayList<String> photoCabinet = new ArrayList<>();
	
	@Column(name = "nb_rdv")
	private Integer nbRdv = 0;
	
	public Psychologue() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
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

	public ArrayList<String> getPhotoCabinet() {
		return photoCabinet;
	}

	public void setPhotoCabinet(ArrayList<String> photoCabinet) {
		this.photoCabinet = photoCabinet;
	}

	public Integer getNbRdv() {
		return nbRdv;
	}

	public void setNbRdv(Integer nbRdv) {
		this.nbRdv = nbRdv;
	}
}
