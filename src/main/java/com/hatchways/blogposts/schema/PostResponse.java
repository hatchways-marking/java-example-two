package com.hatchways.blogposts.schema;

public class PostResponse {
  private String text;
  private String[] tags;
  private Float popularity;
  private Long reads;
  private Long likes;
  private Long id;

  public PostResponse() {}

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

  public Float getPopularity() {
    return popularity;
  }

  public void setPopularity(Float popularity) {
    this.popularity = popularity;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getReads() {
    return reads;
  }

  public void setReads(Long reads) {
    this.reads = reads;
  }

  public Long getLikes() {
    return likes;
  }

  public void setLikes(Long likes) {
    this.likes = likes;
  }
}
