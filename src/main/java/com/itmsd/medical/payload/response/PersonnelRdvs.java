package com.itmsd.medical.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.itmsd.medical.payload.request.Personne;

@JsonInclude(Include.NON_NULL)
public class PersonnelRdvs {
	
	private long id;
	
	private String date;
	
	private String startDate;
	
	private String endDate;
	
	private String subject;
	
	private String message;
	
	private Personne patient;

	private Personne doctor;

	private String status;
	
	private String createdAt;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Personne getPatient() {
		return patient;
	}

	public void setPatient(Personne patient) {
		this.patient = patient;
	}

	public Personne getDoctor() {
		return doctor;
	}

	public void setDoctor(Personne doctor) {
		this.doctor = doctor;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	
}


