package com.hatchways.blogposts.controller;

import com.hatchways.blogposts.model.Post;
import com.hatchways.blogposts.schema.PostRequest;
import com.hatchways.blogposts.schema.PostResponse;
import com.hatchways.blogposts.schema.PostResponseWrapper;
import com.hatchways.blogposts.service.PostService;
import javax.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PostController {

  private final PostService postService;
  private final ModelMapper modelMapper;

  public PostController(PostService postService, ModelMapper modelMapper) {
    this.postService = postService;
    this.modelMapper = modelMapper;
  }

  /** Create a new post in the database. */
  @PostMapping("/posts")
  public ResponseEntity<PostResponseWrapper> createPost(
      @Valid @RequestBody PostRequest postRequestBody, Authentication authentication) {

    Post post = postService.createPost(postRequestBody, authentication.getName());
    PostResponse postResponse = modelMapper.map(post, PostResponse.class);
    PostResponseWrapper response = new PostResponseWrapper(postResponse);
    return ResponseEntity.ok(response);
  }
}
