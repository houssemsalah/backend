package com.itmsd.medical.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itmsd.medical.entities.RendezVous;

@Repository
public interface RendezVousRepository extends JpaRepository<RendezVous, Long>{

}
