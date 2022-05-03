package com.itmsd.medical.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.itmsd.medical.entities.Pharmacie;

@Repository
public interface PharmacieRepository extends JpaRepository<Pharmacie, Long> {
	Pharmacie findByEmail (String email);
	List<Pharmacie> findAllByVille(String ville);
	List<Pharmacie> findAllByVilleAndPostalCode(String ville, Integer postalCode);
	List<Pharmacie> findAllByPostalCode(Integer postalCode);
	
	@Query(value = "Select * from pharmacie Order by nb_rdv desc LIMIT 1",
			nativeQuery = true)
	List<Pharmacie> findTopPraticien();
}
