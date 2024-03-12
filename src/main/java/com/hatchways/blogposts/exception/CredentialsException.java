package com.hatchways.blogposts.exception;

public class CredentialsException extends UnauthorizedException {

  public CredentialsException() {
    super("Could not validate credentials");
  }

  public CredentialsException(String message) {
    super(message);
  }
}
