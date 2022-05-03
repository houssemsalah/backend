package com.itmsd.medical.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itmsd.medical.entities.Schedule;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long>{

}
