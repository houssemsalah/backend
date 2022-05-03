package com.itmsd.medical.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itmsd.medical.entities.Admin;



@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
	Admin findByEmail(String email);

}
