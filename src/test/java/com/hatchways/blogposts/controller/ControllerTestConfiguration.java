package com.hatchways.blogposts.controller;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class ControllerTestConfiguration {
  @Bean
  public TestUtil testUtil() {
    return new TestUtil();
  }
}
