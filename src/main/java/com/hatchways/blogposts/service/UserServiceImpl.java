package com.hatchways.blogposts.service;

import com.hatchways.blogposts.exception.UserExistsException;
import com.hatchways.blogposts.model.User;
import com.hatchways.blogposts.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User createUser(String username, String password) {
    User existingUser = userRepository.findByUsername(username);
    if (existingUser != null) {
      throw new UserExistsException("User already exists");
    }

    User user = new User(username, BCrypt.hashpw(password, BCrypt.gensalt()));
    this.userRepository.save(user);
    this.userRepository.flush();
    return user;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = this.userRepository.findByUsername(username);
    if (user == null) {
      throw new UsernameNotFoundException("User not found");
    }
    return user;
  }
}
