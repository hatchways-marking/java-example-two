package com.hatchways.blogposts.exception;

import java.util.Map;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

@Component
public class CustomErrorAttributes extends DefaultErrorAttributes {
  /**
   * Customize the error attributes that will be present in error responses.
   *
   * <p>Remove all unnecessary properties from the error response attributes.
   */
  @Override
  public Map<String, Object> getErrorAttributes(
      WebRequest webRequest, ErrorAttributeOptions options) {
    Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, options);

    errorAttributes.remove("path");
    errorAttributes.remove("status");
    errorAttributes.remove("timestamp");

    return errorAttributes;
  }
}
