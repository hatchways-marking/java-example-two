package com.hatchways.blogposts.schema;

public class PostRequest {
  private String text;
  private String[] tags;

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String[] getTags() {
    return tags;
  }

  public void setTags(String[] tags) {
    this.tags = tags;
  }
}
