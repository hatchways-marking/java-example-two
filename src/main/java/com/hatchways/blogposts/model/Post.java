package com.hatchways.blogposts.model;

import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "post")
public class Post {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_sequence")
  @SequenceGenerator(name = "post_sequence", sequenceName = "post_sequence", initialValue = 5)
  private Long id;

  @Column private String text;

  @Column private String tags;

  @Column private Long likes;

  @Column private Long reads;

  @Column(precision = 7, scale = 2)
  private Float popularity;

  @ManyToMany
  @JoinTable(
      name = "user_post",
      joinColumns = @JoinColumn(name = "post_id"),
      inverseJoinColumns = @JoinColumn(name = "user_id"))
  private Set<User> users;

  public Post() {}

  public Post(String text, String tags, Long likes, Long reads, Float popularity) {
    this.text = text;
    this.tags = tags;
    this.likes = likes;
    this.reads = reads;
    this.popularity = popularity;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public Long getLikes() {
    return likes;
  }

  public void setLikes(Long likes) {
    this.likes = likes;
  }

  public Long getReads() {
    return reads;
  }

  public void setReads(Long reads) {
    this.reads = reads;
  }

  public Float getPopularity() {
    return popularity;
  }

  public void setPopularity(Float popularity) {
    this.popularity = popularity;
  }

  public String[] getTags() {
    return tags != null ? tags.split(",") : new String[0];
  }

  public void setTags(String[] tags) {
    this.tags = String.join(",", tags);
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Set<User> getUsers() {
    return users;
  }

  public void setUsers(Set<User> users) {
    this.users = users;
  }
}
