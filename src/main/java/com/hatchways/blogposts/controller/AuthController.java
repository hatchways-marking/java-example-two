package com.hatchways.blogposts.controller;

import com.hatchways.blogposts.model.User;
import com.hatchways.blogposts.schema.AuthRequest;
import com.hatchways.blogposts.schema.AuthResponse;
import com.hatchways.blogposts.service.UserService;
import com.hatchways.blogposts.util.AuthUtil;
import javax.validation.Valid;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {

  private final UserService userService;
  private final AuthenticationManager authenticationManager;
  private final AuthUtil authUtil;

  public AuthController(
      UserService userService,
      Environment env,
      AuthenticationManager authenticationManager,
      AuthUtil authUtil) {
    this.userService = userService;
    this.authenticationManager = authenticationManager;
    this.authUtil = authUtil;
  }

  /** Create a new user in the database. */
  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(@Valid @RequestBody AuthRequest authRequestBody) {
    User user =
        userService.createUser(authRequestBody.getUsername(), authRequestBody.getPassword());
    String token = authUtil.generateToken(user.getUsername());
    AuthResponse response = new AuthResponse(token, user.getId(), user.getUsername());
    return ResponseEntity.ok(response);
  }

  /** Validate a user's login attempt. */
  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequestBody) {
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                authRequestBody.getUsername(), authRequestBody.getPassword()));
    User user = (User) authentication.getPrincipal();
    String token = authUtil.generateToken(user.getUsername());
    AuthResponse response = new AuthResponse(token, user.getId(), user.getUsername());
    return ResponseEntity.ok(response);
  }
}
