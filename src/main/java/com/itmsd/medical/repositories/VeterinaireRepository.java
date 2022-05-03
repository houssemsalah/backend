package com.itmsd.medical.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.itmsd.medical.entities.Veterinaire;

@Repository
public interface VeterinaireRepository extends JpaRepository<Veterinaire, Long>{
	Veterinaire findByEmail (String email);
	List<Veterinaire> findAllByVille(String ville);
	List<Veterinaire> findAllByVilleAndPostalCode(String ville, Integer postalCode);
	List<Veterinaire> findAllByPostalCode(Integer postalCode);
	
	@Query(value = "Select * from veterinaire Order by nb_rdv desc LIMIT 1",
			nativeQuery = true)
	List<Veterinaire> findTopPraticien();
}
