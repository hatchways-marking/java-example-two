package com.hatchways.blogposts.repository;

import com.hatchways.blogposts.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  @Query("SELECT u from User as u where u.username = :username")
  User findByUsername(String username);
}
