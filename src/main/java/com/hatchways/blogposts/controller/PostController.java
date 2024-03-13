package com.hatchways.blogposts.controller;

import com.hatchways.blogposts.exception.NotFoundException;
import com.hatchways.blogposts.model.Post;
import com.hatchways.blogposts.model.User;
import com.hatchways.blogposts.schema.ErrorResponse;
import com.hatchways.blogposts.schema.PostRequest;
import com.hatchways.blogposts.schema.PostResponse;
import com.hatchways.blogposts.schema.PostResponseWrapper;
import com.hatchways.blogposts.schema.PostPatchRequest;
import com.hatchways.blogposts.service.PostService;

import javax.validation.Valid;

import com.hatchways.blogposts.service.UserService;
import org.apache.commons.lang3.math.NumberUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Field;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/posts")
public class PostController {

  private static final Field[] FIELDS = PostResponse.class.getDeclaredFields();

  private final PostService postService;
  private final ModelMapper modelMapper;
  private final UserService userService;

  public static class Errors {

    public static final String INVALID_AUTHOR_IDS = "You have provided 1 or more invalid authorIds";
    public static final String NO_AUTHOR_IDS = "No authorIds were provided";
    public static final String INVALID_SMALLER_THAN_0 = "One or more authorIds are invalid, ids must be greater than 0";
    public static final String EXCEED_RANGE = "One or more authorIds exceed range";
    public static final String INVALID_SORT_BY = "An invalid sortBy field was specified";

  }

  public PostController(PostService postService, ModelMapper modelMapper, UserService userService) {
    this.postService = postService;
    this.modelMapper = modelMapper;
    this.userService = userService;
  }

  @GetMapping
  public ResponseEntity<?> get(@RequestParam(defaultValue = "") final String authorIds,
                               @RequestParam(defaultValue = "id", required = false) final String sortBy,
                               @RequestParam(defaultValue = "asc", required = false) final String direction) {
    if (authorIds.trim().isBlank()) {
      return ResponseEntity.badRequest().body(new ErrorResponse(Errors.NO_AUTHOR_IDS));
    }

    final Supplier<Stream<String>> streamOfAuthorIds = () -> Arrays.stream(authorIds.split(",", -1));
    if (!streamOfAuthorIds.get().allMatch(NumberUtils::isParsable)) {
      return ResponseEntity.badRequest().body(new ErrorResponse(Errors.INVALID_AUTHOR_IDS));
    }

    if (!sortBy.isBlank() && Arrays.stream(FIELDS).map(Field::getName).noneMatch(x -> x.equals(sortBy))) {
      return ResponseEntity.badRequest().body(new ErrorResponse(Errors.INVALID_SORT_BY));
    }

    try {
      final List<Long> authors = streamOfAuthorIds.get()
              .map(Long::parseLong)
              .toList();
      if (authors.stream().anyMatch(x -> x <= 0L)) {
        return ResponseEntity.badRequest().body(new ErrorResponse(Errors.INVALID_SMALLER_THAN_0));
      }
      final Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
      return ResponseEntity.ok(postService.findAllByUserIds(authors, sort));
    } catch (final NumberFormatException e) {
      return ResponseEntity.badRequest().body(new ErrorResponse(Errors.EXCEED_RANGE));
    } catch (final IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }
  }

  @PatchMapping("/{id}")
  public ResponseEntity<?> patch(@PathVariable final long id,
                                 @Valid @RequestBody final PostPatchRequest requestBody,
                                 final Principal principal) {
    final Post post = postService.findById(id)
            .orElseThrow(() -> new NotFoundException("Post with matching ID " + id));
    final User user = userService.findByUsername(principal.getName());
    return ResponseEntity.ok(postService.patch(post, requestBody, user));
  }

  /** Create a new post in the database. */
  @PostMapping
  public ResponseEntity<PostResponseWrapper> createPost(
      @Valid @RequestBody PostRequest postRequestBody, Authentication authentication) {

    Post post = postService.createPost(postRequestBody, authentication.getName());
    PostResponse postResponse = modelMapper.map(post, PostResponse.class);
    PostResponseWrapper response = new PostResponseWrapper(postResponse);
    return ResponseEntity.ok(response);
  }
}
