package com.itmsd.medical.payload.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Personne {
	public long id ;
	public String firstName;
	public String lastName;
	public String name;
	public String email; 
	public String username;
	public String role;
	public String ville;
	public String address;
	public Integer postalCode;
	public long phoneNumber;
	public String createdAt;
	public String status;
	public int nbRdv;
	public Boolean active;
	public Boolean banned;
	public int nbViews;
	public Boolean deleted;
	
	public Personne(long id, String firstName, String lastName, String email, String username, String ville,
			String address, Integer postalCode, Long phoneNumber) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.username = username;
		this.ville = ville;
		this.address = address;
		this.postalCode = postalCode;
		this.phoneNumber = phoneNumber;
	}
	
	public Personne(long id, String name, String email, String username, String ville,
			String address, Integer postalCode, Long phoneNumber) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.username = username;
		this.ville = ville;
		this.address = address;
		this.postalCode = postalCode;
		this.phoneNumber = phoneNumber;
	}
	
	public Personne() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
