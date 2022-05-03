package com.itmsd.medical.controllers;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.itmsd.medical.entities.Message;
import com.itmsd.medical.repositories.ForumRepository;
import com.itmsd.medical.repositories.MessageRepository;
import com.itmsd.medical.repositories.UserRepository;
import com.itmsd.medical.services.DateAttribute;

	
@CrossOrigin(origins = "*")
@RestController
public class ForumController {
		@Autowired
		private MessageRepository messageRepository;
		
		@Autowired
		private ForumRepository forumRepository;
		
		@Autowired
		private UserRepository userRepository;
		
		@Autowired
		private SimpMessagingTemplate simpMessagingTemplate;
		
	/*	@MessageMapping("chat/{forumName}")
		@SendTo("/topic/messages/{forumName}")
		public Message sendMessage(@PathVariable(value = "forumName") String forumName, @Payload Message message) {

			String Datedata = DateAttribute.getTime(new Date());
			message.setCreatedAt(Datedata);
			message.setForum(forumRepository.findByForumName(forumName));
			//messageRepository.save(message);
			
			//simpMessagingTemplate.convertAndSend("/topic/messages/" + forumName, message);
			return messageRepository.save(message);
		}*/
		@MessageMapping("/chat/{forumName}")
		public void sendMessage(@DestinationVariable String forumName, Message message) {

			String Datedata = DateAttribute.getTime(new Date());
			message.setCreatedAt(Datedata);
			message.setAvatarUrl(userRepository.findUserByUsername(message.getUsername()).getPhotoUrl());
			message.setForum(forumRepository.findByForumName(forumName));
			messageRepository.save(message);
			
			simpMessagingTemplate.convertAndSend("/topic/messages/" + forumName, message);

		}
		
}
