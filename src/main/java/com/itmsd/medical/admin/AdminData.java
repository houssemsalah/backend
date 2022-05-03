package com.itmsd.medical.admin;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.itmsd.medical.entities.Admin;
import com.itmsd.medical.entities.Role;
import com.itmsd.medical.repositories.AdminRepository;
import com.itmsd.medical.repositories.RoleRepository;
import com.itmsd.medical.services.DateAttribute;

@Component
public class AdminData implements CommandLineRunner{
	
	private RoleRepository roleRepository;
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	private AdminRepository adminRepository;
	@Autowired
	public AdminData(RoleRepository roleRepository,BCryptPasswordEncoder bCryptPasswordEncoder,
			AdminRepository adminRepository) {
		super();
		this.roleRepository = roleRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.adminRepository = adminRepository;
	}

	@Override
	public void run(String... args) throws Exception {
		Role userRole = roleRepository.findByRole("admin");
		if (userRole == null) {
			Role newRole = new Role ("admin") ;
			newRole = roleRepository.save(newRole);		
			
			Admin admin = new Admin();
			admin.setEmail("myhealthnetwork.service@gmail.com");
			admin.setPassword(bCryptPasswordEncoder.encode("adminhealth2022"));
			admin.setPasswordConfirmation(bCryptPasswordEncoder.encode("adminhealth2022"));
			admin.setUsername("superAdmin1");
			admin.setActive(true);
			admin.setCreatedAt(DateAttribute.getTime(new Date()));
			admin.setOnline("offline");
			admin.setRoles(new HashSet<Role>(Arrays.asList(newRole)));
			admin.setFirstName("Admin");
			admin.setLastName("Super");
			admin.setEmail("myhealthnetwork.service@gmail.com");
			Long phoneNumber = (long) 00000000;
			admin.setPhoneNumber(phoneNumber);
			adminRepository.save(admin);
		}
	}
}
