package com.itmsd.medical.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itmsd.medical.entities.Forum;

@Repository
public interface ForumRepository extends JpaRepository<Forum, Long> {
	Forum findByForumName(String forumName);
}
