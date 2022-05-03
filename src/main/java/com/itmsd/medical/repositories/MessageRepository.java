package com.itmsd.medical.repositories;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.itmsd.medical.entities.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long>{
	@Query(value = "Select * from message m where m.forum_id = :forum Order by id asc",
			nativeQuery = true)
	Set<Message> findMessages (@Param("forum") Long forum);
}
