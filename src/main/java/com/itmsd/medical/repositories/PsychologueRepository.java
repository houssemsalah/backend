package com.itmsd.medical.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.itmsd.medical.entities.Psychologue;

@Repository
public interface PsychologueRepository extends JpaRepository<Psychologue, Long> {
	Psychologue findByEmail (String email);
	List<Psychologue> findAllByVille(String ville);
	List<Psychologue> findAllByVilleAndPostalCode(String ville, Integer postalCode);
	List<Psychologue> findAllByPostalCode(Integer postalCode);
	
	@Query(value = "Select * from psychologue Order by nb_rdv desc LIMIT 1",
			nativeQuery = true)
	List<Psychologue> findTopPraticien();
}
