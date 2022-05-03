package com.itmsd.medical.entities;

import java.util.ArrayList;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Experience {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "experience_id")
	private long id;

	@NotBlank(message = "title is mandatory")
	@Column(name = "title")
	private String title;
	
	@Column(name = "start_experience")
	private String startExperience;
	
	@Column(name = "end_experience")
	private String endExperience;
	
	@Column(columnDefinition = "TEXT", name = "description")
	private String description;
	
	@Column(name = "company")
	private String company;
	
	@Column(name = "attachment_url")
	private ArrayList<String> attachmentUrl;
	
	@JsonIgnore
	@ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
	private User user;

	public Experience() {
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

	public String getStartExperience() {
		return startExperience;
	}

	public void setStartExperience(String startExperience) {
		this.startExperience = startExperience;
	}

	public String getEndExperience() {
		return endExperience;
	}

	public void setEndExperience(String endExperience) {
		this.endExperience = endExperience;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ArrayList<String> getAttachmentUrl() {
		return attachmentUrl;
	}

	public void setAttachmentUrl(ArrayList<String> attachmentUrl) {
		this.attachmentUrl = attachmentUrl;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}
	
}
