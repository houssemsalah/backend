package com.itmsd.medical.services;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itmsd.medical.entities.Forum;
import com.itmsd.medical.entities.Role;
import com.itmsd.medical.entities.User;
import com.itmsd.medical.repositories.ForumRepository;
import com.itmsd.medical.repositories.RoleRepository;
import com.itmsd.medical.repositories.UserRepository;

import net.bytebuddy.utility.RandomString;

@Service("userService")
@Transactional
public class UserService {
	private UserRepository userRepository;
	private RoleRepository roleRepository;
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	private ForumRepository forumRepository;

	@Autowired
	public UserService(UserRepository userRepository, RoleRepository roleRepository,
			BCryptPasswordEncoder bCryptPasswordEncoder,ForumRepository forumRepository) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.forumRepository = forumRepository ;
	}
	public User saveUser(User user, String role) {
		String token = RandomString.make(30);
		user.setActive(false);
		user.setOnline("offline");
		user.setConfirmationToken(token);
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		user.setPasswordConfirmation(bCryptPasswordEncoder.encode(user.getPasswordConfirmation()));
		String Datedata = DateAttribute.getTime(new Date());
		user.setCreatedAt(Datedata);
		Role userRole = roleRepository.findByRole(role);
		if (userRole == null) {
			Role newRole = new Role (role) ;
			newRole = roleRepository.save(newRole);		
			user.setRoles(new HashSet<Role>(Arrays.asList(newRole)));
		}else {			
			user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
		}
		Forum forum = new Forum();
		
		if (role.contains("medecin")) {
			if (forumRepository.findByForumName("medecins") == null ) {
				forum.setForumName("medecins");
				forum.getUsers().add(userRepository.save(user));
				forumRepository.save(forum);
			}else {
				forum = forumRepository.findByForumName("medecins");
				forum.getUsers().add(userRepository.save(user));
				forumRepository.save(forum);
			}
			
		}else if (role.contains("psychologue")) {
			 if (forumRepository.findByForumName("psychologues") == null ) {
					forum.setForumName("psychologues");
					forum.getUsers().add(userRepository.save(user));
					forumRepository.save(forum);
				}else {
					forum = forumRepository.findByForumName("psychologues");
					forum.getUsers().add(userRepository.save(user));
					forumRepository.save(forum);
				}
		}else if (role.contains("veterinaire")) {
			 if (forumRepository.findByForumName("veterinaires") == null ) {
					forum.setForumName("veterinaires");
					forum.getUsers().add(userRepository.save(user));
					forumRepository.save(forum);
				}else {
					forum = forumRepository.findByForumName("veterinaires");
					forum.getUsers().add(userRepository.save(user));
					forumRepository.save(forum);
				}
		}else if (role.contains("dentiste")) {
			 if (forumRepository.findByForumName("dentistes") == null ) {
					forum.setForumName("dentistes");
					forum.getUsers().add(userRepository.save(user));
					forumRepository.save(forum);
				}else {
					forum = forumRepository.findByForumName("dentistes");
					forum.getUsers().add(userRepository.save(user));
					forumRepository.save(forum);
				}
		}else if (role.contains("kine")) {
			 if (forumRepository.findByForumName("kines") == null ) {
					forum.setForumName("kines");
					forum.getUsers().add(userRepository.save(user));
					forumRepository.save(forum);
				}else {
					forum = forumRepository.findByForumName("kines");
					forum.getUsers().add(userRepository.save(user));
					forumRepository.save(forum);
				}
		}else if (role.contains("pharmacie")) {
			 if (forumRepository.findByForumName("pharmacies") == null ) {
					forum.setForumName("pharmacies");
					forum.getUsers().add(userRepository.save(user));
					forumRepository.save(forum);
				}else {
					forum = forumRepository.findByForumName("pharmacies");
					forum.getUsers().add(userRepository.save(user));
					forumRepository.save(forum);
				}
		}
		
		return userRepository.save(user);
	}
	
	public User findUserByEmail(String email) {
		return userRepository.findUserByEmail(email);
	}
	public User findUserByUsername(String username) {
		return userRepository.findUserByUsername(username);
	}
	public User findUserByConfirmationToken(String confirmationToken) {
		return userRepository.findUserByConfirmationToken(confirmationToken);
	}
}
