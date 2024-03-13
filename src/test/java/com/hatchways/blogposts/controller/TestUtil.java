package com.hatchways.blogposts.controller;

import com.hatchways.blogposts.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;

public class TestUtil {

  @Autowired private AuthUtil authUtil;

  public String getUserToken(String userName) {
    return authUtil.generateToken(userName);
  }
}
