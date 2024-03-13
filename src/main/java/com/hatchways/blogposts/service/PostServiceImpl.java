package com.hatchways.blogposts.service;

import com.hatchways.blogposts.exception.UnauthorizedException;
import com.hatchways.blogposts.model.Post;
import com.hatchways.blogposts.model.User;
import com.hatchways.blogposts.repository.PostRepository;
import com.hatchways.blogposts.repository.UserRepository;
import com.hatchways.blogposts.schema.PostListResponse;
import com.hatchways.blogposts.schema.PostPatchResponse;
import com.hatchways.blogposts.schema.PostRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.hatchways.blogposts.schema.PostResponse;
import com.hatchways.blogposts.schema.PostPatchRequest;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
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

  public Optional<Post> findById(final long id) {
    return postRepository.findById(id);
  }

  public PostListResponse findAllByUserIds(final List<Long> userIds, final Sort sort) {
    final List<PostResponse> posts = postRepository.findAllByUserIds(userIds, sort).stream()
            .map(PostResponse::new)
            .toList();
    return new PostListResponse(posts);
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

  @Override
  public PostPatchResponse patch(final Post post, final PostPatchRequest requestBody, final User user) {
    final boolean isPrincipalAnAuthor = post.getUsers().stream()
            .anyMatch(x -> Objects.equals(x.getId(), user.getId()));
    if (!isPrincipalAnAuthor) {
      throw new UnauthorizedException("You are not authorized to update this post");
    }

    boolean updateRequired = false;
    if (null != requestBody.getAuthorIds()) {
      post.setAuthorIds(requestBody.getAuthorIds());
      updateRequired = true;
    }
    if (null != requestBody.getTags()) {
      post.setTags(requestBody.getTags());
      updateRequired = true;
    }
    if (null != requestBody.getText()) {
      post.setText(requestBody.getText());
      updateRequired = true;
    }
    if (updateRequired) {
      postRepository.save(post);
    }
    return new PostPatchResponse(post);
  }

}
