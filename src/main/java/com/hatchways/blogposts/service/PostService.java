package com.hatchways.blogposts.service;

import com.hatchways.blogposts.model.Post;
import com.hatchways.blogposts.model.User;
import com.hatchways.blogposts.schema.PostListResponse;
import com.hatchways.blogposts.schema.PostPatchResponse;
import com.hatchways.blogposts.schema.PostRequest;
import com.hatchways.blogposts.schema.PostPatchRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface PostService {

  Optional<Post> findById(final long id);

  PostListResponse findAllByUserIds(final List<Long> userIds, final Sort sort);

  /** Create a new post in the database. */
  Post createPost(PostRequest postRequestBody, String username);

  PostPatchResponse patch(final Post post, final PostPatchRequest requestBody, final User user);
}
