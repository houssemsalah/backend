package com.itmsd.medical.entities;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Schedule {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "schedule_id")
	private long id;
	
	@OneToMany(mappedBy = "schedule")
	@Column(name = "session")
	private List<Session> session;
	
	@JsonIgnore
	@ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
	private User medecin;
	
	public Schedule() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	public List<Session> getSession() {
		return session;
	}

	public void setSession(List<Session> session) {
		this.session = session;
	}

	public User getMedecin() {
		return medecin;
	}

	public void setMedecin(User medecin) {
		this.medecin = medecin;
	}

}
