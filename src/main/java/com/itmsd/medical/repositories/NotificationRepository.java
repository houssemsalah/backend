package com.itmsd.medical.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.itmsd.medical.entities.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>{
	@Query(value = "SELECT * FROM notification WHERE user_id = :userId "
			+ "ORDER BY notification_id desc "
			+ "OFFSET :skip ROWS FETCH NEXT :nb ROWS ONLY"
			,nativeQuery = true)
	List<Notification> findNotifPages(@Param("userId") Long userId,
			@Param("skip") int skip, @Param("nb") int nb);
}
