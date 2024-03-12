package com.hatchways.blogposts.service;

import com.hatchways.blogposts.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

  /** Create a new user in the database. */
  User createUser(String username, String password);
}
