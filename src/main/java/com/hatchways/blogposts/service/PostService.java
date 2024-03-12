package com.hatchways.blogposts.service;

import com.hatchways.blogposts.model.Post;
import com.hatchways.blogposts.schema.PostRequest;

public interface PostService {
  /** Create a new post in the database. */
  Post createPost(PostRequest postRequestBody, String username);
}
