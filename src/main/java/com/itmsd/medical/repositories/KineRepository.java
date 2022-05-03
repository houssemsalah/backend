package com.itmsd.medical.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.itmsd.medical.entities.Kine;

@Repository
public interface KineRepository extends JpaRepository<Kine, Long> {
	Kine findByEmail (String email);
	List<Kine> findAllByVille(String ville);
	List<Kine> findAllByVilleAndPostalCode(String ville, Integer postalCode);
	List<Kine> findAllByPostalCode(Integer postalCode);
	
	@Query(value = "Select * from kine Order by nb_rdv desc LIMIT 1",
			nativeQuery = true)
	List<Kine> findTopPraticien();
}
