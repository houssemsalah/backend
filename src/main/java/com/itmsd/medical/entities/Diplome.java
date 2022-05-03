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
public class Diplome {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "diplome_id")
	private long id;
	
	@Column(name = "type")
	private String type;
	
	@NotBlank(message = "title is mandatory")
	@Column(name = "title")
	private String title;
	
	@Column(name = "start_diplome")
	private String startDiplome;
	
	@Column(name = "end_diplome")
	private String endDiplome;
	
	@Column(columnDefinition = "TEXT", name = "description")
	private String description;
	
	@Column(name = "attachment_url")
	private ArrayList<String> attachmentUrl;
	
	@Column(name = "university")
	private String university;
	
	@JsonIgnore
	@ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
	private User user;

	public Diplome() {
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

	public String getStartDiplome() {
		return startDiplome;
	}

	public void setStartDiplome(String startDiplome) {
		this.startDiplome = startDiplome;
	}

	public String getEndDiplome() {
		return endDiplome;
	}

	public void setEndDiplome(String endDiplome) {
		this.endDiplome = endDiplome;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUniversity() {
		return university;
	}

	public void setUniversity(String university) {
		this.university = university;
	}
	
	
	
}
