package com.itmsd.medical.entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

@Entity
@Table(name = "users", uniqueConstraints = {
				@UniqueConstraint(columnNames = "username"),
				@UniqueConstraint(columnNames = "email")
})
@Inheritance(strategy = InheritanceType.JOINED)
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "user_id")
	private long id;

	@Column(name = "email")
	@Email(message = "*Please provide a valid Email")
	@NotEmpty(message = "*Please provide an email")
	private String email;
	
	@Column(name = "username")
	private String username;
	
	@Column(name = "password")
	@Size(min = 8, message = "*Your password must have at least 8 characters")
	//@NotEmpty(message = "*Please provide your password")
	private String password;
	
	@Column(name = "password_confirmation")
	@Size(min = 8, message = "*Your password must have at least 8 characters")
	//@NotEmpty(message = "*Please provide your password confirmation")
	private String passwordConfirmation;
	
	@ManyToMany(cascade = {CascadeType.DETACH, CascadeType.REFRESH})
	@JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles;
	
	@Column(name = "createdAt")
	private String createdAt;
	
	@Column(name ="active")
	private boolean active;
	
	@Column(name ="ban_status")
	private boolean banned;
	
	@Column(name ="deleted")
	private boolean deleted;
	
	@Column(name = "nb_views")
	private Integer nbviews = 0;
	
	@Column(name = "ip_users")
	private ArrayList<String> ipUsers = new ArrayList<>();
	
	@Column(name = "confirmation_token")
	private String confirmationToken;
	
	@Column(name = "ville")
	private String ville;
	
	@Column(name = "phone_number")
	private Long phoneNumber;
	
	@Column(name = "image_url")
	private String photoUrl;
	
	@Column(name = "token")
	private String jwt;
	
	@Column(name = "online")
	private String online;
	
	@Column(columnDefinition = "TEXT", name = "description")
	private String description;
	
	@Column(name = "annees_experience")
	private Integer experienceNumber;
	
	@Column(name = "speciality")
	private String speciality;
	
	@Column(name = "num_inami")
	private String inami;
	
	@OneToMany(mappedBy = "user")
	@Column(name = "diplomes")
	private List<Diplome> diplomes = new ArrayList<>();
	
	@OneToMany(mappedBy = "user")
	@Column(name = "experiences")
	private List<Experience> experiences = new ArrayList<>();
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "rendez_vous_users",
	joinColumns = { @JoinColumn(name = "user_id")},
	inverseJoinColumns = { @JoinColumn (name = "rendez_vous_id")})
	private Set<RendezVous> rendezVous = new HashSet<>();
	
	@OneToMany(mappedBy = "medecin")
	@Column(name = "schedule")
	private Set<Schedule> schedule = new HashSet<>();
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "forum_users",
	joinColumns = { @JoinColumn(name = "user_id")},
	inverseJoinColumns = { @JoinColumn (name = "forum_id")})
	private Set<Forum> forum = new HashSet<>();
	
	@OneToMany(mappedBy = "user")
	@Column(name = "notifications")
	private List<Notification> notifications = new ArrayList<>();
	
	public void removeForum() {
		forum = new HashSet<>();
	}
	
	public Set<Forum> getForum() {
		return forum;
	}

	public void setForum(Set<Forum> forum) {
		this.forum = forum;
	}

	public Set<RendezVous> getRendezVous() {
		return rendezVous;
	}

	public void setRendezVous(Set<RendezVous> rendezVous) {
		this.rendezVous = rendezVous;
	}

	public String getVille() {
		return ville;
	}

	public void setVille(String ville) {
		this.ville = ville;
	}

	public String getJwt() {
		return jwt;
	}

	public void setJwt(String jwt) {
		this.jwt = jwt;
	}

	public String getConfirmationToken() {
		return confirmationToken;
	}

	public void setConfirmationToken(String confirmationToken) {
		this.confirmationToken = confirmationToken;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	
	public void resetRoles () {
		this.roles = new HashSet<>();
	}
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	@JsonIgnore
	public String getPassword() {
		return password;
	}
	@JsonSetter
	public void setPassword(String password) {
		this.password = password;
	}
	@JsonIgnore
	public String getPasswordConfirmation() {
		return passwordConfirmation;
	}
	@JsonSetter
	public void setPasswordConfirmation(String passwordConfirmation) {
		this.passwordConfirmation = passwordConfirmation;
	}
	public Set<Role> getRoles() {
		return roles;
	}
	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public Set<Schedule> getSchedule() {
		return schedule;
	}

	public void setSchedule(Set<Schedule> schedule) {
		this.schedule = schedule;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public String getOnline() {
		return online;
	}

	public void setOnline(String online) {
		this.online = online;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getExperienceNumber() {
		return experienceNumber;
	}

	public void setExperienceNumber(Integer experienceNumber) {
		this.experienceNumber = experienceNumber;
	}

	public String getSpeciality() {
		return speciality;
	}

	public void setSpeciality(String speciality) {
		this.speciality = speciality;
	}

	public String getInami() {
		return inami;
	}

	public void setInami(String inami) {
		this.inami = inami;
	}

	public List<Diplome> getDiplomes() {
		return diplomes;
	}

	public void setDiplomes(List<Diplome> diplomes) {
		this.diplomes = diplomes;
	}

	public List<Experience> getExperiences() {
		return experiences;
	}

	public void setExperiences(List<Experience> experiences) {
		this.experiences = experiences;
	}

	public Long getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(Long phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public List<Notification> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<Notification> notifications) {
		this.notifications = notifications;
	}

	public boolean isBanned() {
		return banned;
	}

	public void setBanned(boolean banned) {
		this.banned = banned;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Integer getNbviews() {
		return nbviews;
	}

	public void setNbviews(Integer nbviews) {
		this.nbviews = nbviews;
	}

	public ArrayList<String> getIpUsers() {
		return ipUsers;
	}

	public void setIpUsers(ArrayList<String> ipUsers) {
		this.ipUsers = ipUsers;
	}
}