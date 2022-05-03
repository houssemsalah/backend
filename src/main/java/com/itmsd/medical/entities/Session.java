package com.itmsd.medical.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Session {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "session_id")
	private long id;
	
	@Column(name = "title")
	private String title;

	@Column(name = "start_time")
	private String startDate;
	
	@Column(name = "end_time")
	private String endDate;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "nbrdvs")
	private Integer nbRdvs = 0;
	
	@JsonIgnore
	@ManyToOne
    @JoinColumn(name = "schedule_id", referencedColumnName = "schedule_id")
	private Schedule schedule;

	public Session() {
		super();
		// TODO Auto-generated constructor stub
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	public Integer getNbRdvs() {
		return nbRdvs;
	}

	public void setNbRdvs(Integer nbRdvs) {
		this.nbRdvs = nbRdvs;
	}
}
