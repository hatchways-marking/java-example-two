package com.hatchways.blogposts.repository;

import com.hatchways.blogposts.model.Post;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
  @Query("SELECT p FROM Post p JOIN p.users user WHERE user.id = :userId")
  List<Post> findAllByUserId(Long userId);
}
