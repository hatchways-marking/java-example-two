package com.hatchways.blogposts.schema;

public class PostResponseWrapper {
  private PostResponse post;

  public PostResponseWrapper(PostResponse post) {
    this.post = post;
  }

  public PostResponse getPost() {
    return post;
  }

  public void setPost(PostResponse post) {
    this.post = post;
  }
}
