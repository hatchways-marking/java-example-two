package com.hatchways.blogposts.service;

import com.hatchways.blogposts.model.Post;
import com.hatchways.blogposts.model.User;
import com.hatchways.blogposts.repository.PostRepository;
import com.hatchways.blogposts.repository.UserRepository;
import com.hatchways.blogposts.schema.PostRequest;
import java.util.HashSet;
import java.util.Set;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class PostServiceImpl implements PostService {
  private final PostRepository postRepository;
  private final UserRepository userRepository;
  private final ModelMapper modelMapper;

  public PostServiceImpl(
      PostRepository postRepository, UserRepository userRepository, ModelMapper modelMapper) {
    this.postRepository = postRepository;
    this.userRepository = userRepository;
    this.modelMapper = modelMapper;
  }

  public Post createPost(PostRequest postRequestBody, String username) {
    Post post = modelMapper.map(postRequestBody, Post.class);
    Set<User> users = new HashSet<>();
    User user = userRepository.findByUsername(username);
    users.add(user);
    post.setUsers(users);
    postRepository.save(post);
    postRepository.flush();
    return post;
  }
}
