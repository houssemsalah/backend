package com.itmsd.medical.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itmsd.medical.entities.Diplome;

@Repository
public interface DiplomeRepository extends JpaRepository<Diplome, Long>{

}
