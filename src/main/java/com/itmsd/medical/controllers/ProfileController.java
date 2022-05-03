package com.itmsd.medical.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itmsd.medical.entities.Diplome;
import com.itmsd.medical.entities.Experience;
import com.itmsd.medical.entities.User;
import com.itmsd.medical.payload.request.Update;
import com.itmsd.medical.repositories.DiplomeRepository;
import com.itmsd.medical.repositories.ExperienceRepository;
import com.itmsd.medical.repositories.UserRepository;

@Transactional
@RestController
@RequestMapping("profile")
@CrossOrigin(origins = "*")
public class ProfileController {
	
	private UserRepository userRepository;
	private ExperienceRepository experienceRepository;
	private DiplomeRepository diplomeRepository;
	private UserController userController;
	@Autowired
	public ProfileController(UserRepository userRepository, ExperienceRepository experienceRepository,
			DiplomeRepository diplomeRepository, UserController userController) {
		super();
		this.userRepository = userRepository;
		this.experienceRepository = experienceRepository;
		this.diplomeRepository = diplomeRepository;
		this.userController = userController;
	}
	
	@PutMapping("/update/description")
	public String updateDescription (@RequestBody Update description, HttpServletRequest request) {
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userController.userIdFromToken(jwt);
		User user = userRepository.findById(userIdco).get();
		if (user!=null) {
			user.setDescription(description.getDescription());
			userRepository.save(user);
			return "Description updated";
		} 
		return "Description does not updated !";
	}	
	
				/********** Diplome api's **********/
	
	@PostMapping("/add/diplome")
	public String addDiplome (@RequestBody Diplome diplome, HttpServletRequest request) {
		String jwt = request.getHeader("Authorization").substring(7);
		if (jwt == null) throw new RuntimeException("Bad token !");
		Long userIdco = userController.userIdFromToken(jwt);
		User user = userRepository.findById(userIdco).get();
		if (user!=null) {
			diplome.setUser(user);
			diplomeRepository.save(diplome);
		}else return "bad token !";
		
		return "Diplome added with success !";
	}
	@PutMapping("/update/diplome/{diplomeId}")
	public String updateDiplome (@PathVariable("diplomeId") long diplomeId,@RequestBody Diplome diplome, HttpServletRequest request) {
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userController.userIdFromToken(jwt);
		User user = userRepository.findById(userIdco).get();
		Diplome dip = diplomeRepository.findById(diplomeId).get();
		if (user!=null && user.getId() == userIdco) {
			if (diplome.getTitle()!=null) dip.setTitle(diplome.getTitle());
			if (diplome.getDescription()!=null) dip.setDescription(diplome.getDescription());
			if (diplome.getStartDiplome()!=null) dip.setStartDiplome(diplome.getStartDiplome());
			if (diplome.getEndDiplome()!=null) dip.setEndDiplome(diplome.getEndDiplome());
			if (diplome.getAttachmentUrl()!=null) dip.setAttachmentUrl(diplome.getAttachmentUrl());
			if (diplome.getUniversity()!=null) dip.setUniversity(diplome.getUniversity());
			diplomeRepository.save(dip);
		}else return "bad token !";
		
		return "update diplome with success !";
	}
	@DeleteMapping("delete/diplome/{diplomeId}")
	public String deleteDiplome(@PathVariable("diplomeId") long diplomeId,HttpServletRequest request ) {
		String jwt = request.getHeader("Authorization").substring(7);
		Long uid = userController.userIdFromToken(jwt);
		Diplome diplome = diplomeRepository.findById(diplomeId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid portfolio Id:" + diplomeId));
		if (diplome.getUser().getId()!=uid) throw new RuntimeException("SECURITY : This user is not for the profile owner");
		
		diplomeRepository.delete(diplome);
	 return "Diplome "+diplomeId+" has been deleted";	
	}
	
	
			    /********** Experience api's **********/
	
	@PostMapping("/add/experience")
	public String addExperience (@RequestBody Experience experience, HttpServletRequest request) {
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userController.userIdFromToken(jwt);
		User user = userRepository.findById(userIdco).get();
		if (user!=null) {
			experience.setUser(user);
			experienceRepository.save(experience);
		}else return "bad token !";
		
		return "Experience added with success !";
	}
	
	@PutMapping("/update/experience/{experienceId}")
	public String updateExperience (@PathVariable("experienceId") long experienceId,@RequestBody Experience experience, HttpServletRequest request) {
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userController.userIdFromToken(jwt);
		User user = userRepository.findById(userIdco).get();
		Experience exp = experienceRepository.findById(experienceId).get();
		if (user!=null && user.getId() == userIdco) {
			if (experience.getTitle()!=null) exp.setTitle(experience.getTitle());
			if (experience.getDescription()!=null) exp.setDescription(experience.getDescription());
			if (experience.getStartExperience()!=null) exp.setStartExperience(experience.getStartExperience());
			if (experience.getEndExperience()!=null) exp.setEndExperience(experience.getEndExperience());
			if (experience.getAttachmentUrl()!=null) exp.setAttachmentUrl(experience.getAttachmentUrl());
			if (experience.getCompany()!=null) exp.setCompany(experience.getCompany());
			experienceRepository.save(exp);
		}else return "bad token !";
		
		return "update experience with success !";
	}
	
	@DeleteMapping("delete/experience/{experienceId}")
	public String deleteExperience(@PathVariable("experienceId") long experienceId,HttpServletRequest request ) {
		String jwt = request.getHeader("Authorization").substring(7);
		Long uid = userController.userIdFromToken(jwt);
		Experience experience = experienceRepository.findById(experienceId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid portfolio Id:" + experienceId));
		if (experience.getUser().getId()!=uid) throw new RuntimeException("SECURITY : This user is not for the profile owner");
		
		experienceRepository.delete(experience);
	 return "Experience "+experienceId+" has been deleted";	
	}
	
}
