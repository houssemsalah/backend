package com.itmsd.medical.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itmsd.medical.entities.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long>{
	Patient findByEmail (String email);
}
