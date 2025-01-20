package com.hatchways.blogposts.repository;

import com.hatchways.blogposts.model.Post;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
  @Query("SELECT p FROM Post p JOIN p.users user WHERE user.id = :userId")
  List<Post> findAllByUserId(Long userId);

  @Query("SELECT DISTINCT p FROM Post p JOIN p.users user WHERE user.id IN (:userIds)")
  List<Post> findAllByUserIds(@Param("userIds") final List<Long> userIds, final Sort sort);
}
