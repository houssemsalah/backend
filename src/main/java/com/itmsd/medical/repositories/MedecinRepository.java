package com.itmsd.medical.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.itmsd.medical.entities.Medecin;

@Repository
public interface MedecinRepository extends JpaRepository<Medecin, Long> {
	Medecin findByEmail (String email);
	Medecin findMedecinById (Long id);
	List<Medecin> findAllBySpeciality(String speciality);
	List<Medecin> findAllBySpecialityAndPostalCode(String speciality, Integer postalCode);
	List<Medecin> findAllBySpecialityAndVille(String speciality, String ville);
	List<Medecin> findAllBySpecialityAndVilleAndPostalCode(String speciality, String ville, Integer postalCode);
	List<Medecin> findAllByVille(String ville);
	List<Medecin> findAllByVilleAndPostalCode(String ville, Integer postalCode);
	List<Medecin> findAllByPostalCode(Integer postalCode);
	@Query(value = "Select * from medecin Order by nb_rdv desc LIMIT 1",
			nativeQuery = true)
	List<Medecin> findTopPraticien();
	
}

