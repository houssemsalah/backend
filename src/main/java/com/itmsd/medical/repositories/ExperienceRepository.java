package com.itmsd.medical.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itmsd.medical.entities.Experience;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, Long>{

}
