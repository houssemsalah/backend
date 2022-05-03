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
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Forum {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "forum_id")
	private long id;
	
	@NotBlank(message = "Conversation Name is mandatory")
	@Column(name = "forum_name")
	private String forumName;
	
	@OneToMany(mappedBy = "forum")
	@Column(name = "messages")
	private Set<Message> messages;
	
	@JsonIgnore
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "forum_users",
	joinColumns = { @JoinColumn(name = "forum_id")},
	inverseJoinColumns = { @JoinColumn (name = "user_id")})
	private Set<User> users = new HashSet<>();

	public Forum() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Forum(@NotBlank(message = "Conversation Name is mandatory") String forumName,
			Set<User> users) {
		super();
		this.forumName = forumName;
		this.users = users;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getForumName() {
		return forumName;
	}

	public void setForumName(String forumName) {
		this.forumName = forumName;
	}

	public Set<Message> getMessages() {
		return messages;
	}

	public void setMessages(Set<Message> messages) {
		this.messages = messages;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}


	
}
