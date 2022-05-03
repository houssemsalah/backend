package com.itmsd.medical.controllers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itmsd.medical.entities.Notification;
import com.itmsd.medical.repositories.NotificationRepository;
import com.itmsd.medical.repositories.UserRepository;

@Transactional
@CrossOrigin(origins ="*")
@RestController
@RequestMapping("notifications")
public class NotificationController {
	
	private UserRepository userRepository;
	private NotificationRepository notificationRepository;
	private UserController userController;
	
	@Autowired
	public NotificationController(UserController userController,UserRepository userRepository, NotificationRepository notificationRepository) {
		super();
		this.userRepository = userRepository;
		this.notificationRepository = notificationRepository;
		this.userController = userController;
	}
	
	@GetMapping("/{userId}/{skip}/{nb}")
	public ResponseEntity<?> getAllNotificationUser(@PathVariable (value = "userId") Long userId,@PathVariable (value = "skip") int skip,@PathVariable (value = "nb") int nb,HttpServletRequest request){
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userController.userIdFromToken(jwt);
		if (userIdco != userId) throw new RuntimeException("Security: Bad token !");
		List<Notification> notifications = new ArrayList<>();
		notifications = notificationRepository.findNotifPages(userId,skip, nb);
		notifications.forEach(notif -> {
			notif.setPhotoUrl(userRepository.getById(notif.getIdPatient()).getPhotoUrl());
		});
		//Collections.reverse(notifications);
		return ResponseEntity.ok().body(notifications);
	}
	
	@PostMapping("/seen/{notifId}")
	public ResponseEntity<?> notificationSeen(@PathVariable(value = "notifId") Long notifId, HttpServletRequest request) {
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userController.userIdFromToken(jwt);
		Notification not = notificationRepository.findById(notifId).get();
		if (not.getUser().getId()==userIdco){
			not.setSeen(true);
			notificationRepository.save(not);
		}else throw new RuntimeException("notifId " + notifId + " not found");
			
		return ResponseEntity.ok().body("Notification updated");
	}
}
class SortNotificationByDate implements Comparator<Notification> {
    // Used for sorting in Descending order of
    // roll number
    public int compare(Notification a,Notification b) {
    	
		if (a.getId() < b.getId()) {
	        return -1;
	}
	return 0;	
    }
}