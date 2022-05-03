package com.itmsd.medical.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value={"patientId","personnelId"})
@Entity
public class RendezVous {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "rendez_vous_id")
	private long id;
	
	@Column(name = "date_rendezvous")
	private String date;
	
	@Column(name = "start_time")
	private String startDate;
	
	@Column(name = "end_time")
	private String endDate;
	
	@Column(name = "subject")
	private String subject;
	
	@Column(name = "text_rv")
	private String text;
	
	@Column(name = "patient_rdv")
	private long patientId;
	
	@Column(name = "personnel_rdv")
	private long personnelId;
	
	@Column(name = "status")
	private String status;
	
	@JsonIgnore
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "rendez_vous_users",
	joinColumns = { @JoinColumn(name = "rendez_vous_id")},
	inverseJoinColumns = { @JoinColumn (name = "user_id")})
	private Set<User> users = new HashSet<>();
	
	@Column(name="created_at")
	private String createdAt;

	public RendezVous() {
		super();
		// TODO Auto-generated constructor stub
	}

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

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
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

	public long getPatientId() {
		return patientId;
	}

	public void setPatientId(long patientId) {
		this.patientId = patientId;
	}

	public long getPersonnelId() {
		return personnelId;
	}

	public void setPersonnelId(long personnelId) {
		this.personnelId = personnelId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	
}
