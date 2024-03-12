package com.hatchways.blogposts.schema;

public class AuthResponse {
  private final String token;
  private final Long id;
  private final String username;

  public AuthResponse(String token, Long userId, String username) {
    this.token = token;
    this.id = userId;
    this.username = username;
  }

  public String getToken() {
    return token;
  }

  public Long getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }
}
