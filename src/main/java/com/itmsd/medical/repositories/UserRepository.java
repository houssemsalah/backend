package com.itmsd.medical.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.itmsd.medical.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	Optional<User> findByEmail(String email);
	User findUserByEmail(String email);
	User findUserByUsername(String username);
	User findUserById(Long id);
	User findUserByConfirmationToken (String confirmationToken);
	
	@Query(value = "Select * , 0 AS clazz_ from users Order by nb_views desc LIMIT 4",
			nativeQuery = true)
	List<User> findTopConsulted();
}
