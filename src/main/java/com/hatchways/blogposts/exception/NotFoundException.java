package com.hatchways.blogposts.exception;

public class NotFoundException extends RuntimeException {

  private final String resource;

  public NotFoundException(String resource) {
    super();
    this.resource = resource;
  }

  public String getResource() {
    return resource;
  }
}
