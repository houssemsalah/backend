package com.itmsd.medical.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.itmsd.medical.entities.Dentiste;

@Repository
public interface DentisteRepository extends JpaRepository<Dentiste, Long>{
	Dentiste  findByEmail (String email);
	List<Dentiste> findAllByVille(String ville);
	List<Dentiste> findAllByVilleAndPostalCode(String ville, Integer postalCode);
	List<Dentiste> findAllByPostalCode(Integer postalCode);
	
	@Query(value = "Select * from dentiste Order by nb_rdv desc LIMIT 1",
			nativeQuery = true)
	List<Dentiste> findTopPraticien();
}
