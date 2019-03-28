package com.zianbam.yourcommunity.Model;

public class Post {
  private String postid;
  private String post_text;
  private String publisher;
  private String username,imageUrl, type, deleted, status;
  private int reported;


  public Post() {
  }

  public Post(String postid, String post_text, String publisher, String username, String imageUrl, String type, String deleted, String status, int reported) {
    this.postid = postid;
    this.post_text = post_text;
    this.publisher = publisher;
    this.username = username;
    this.imageUrl = imageUrl;
    this.type = type;
    this.deleted = deleted;
    this.status = status;
    this.reported = reported;
  }

  public String getPostid() {
    return postid;
  }

  public void setPostid(String postid) {
    this.postid = postid;
  }

  public String getPost_text() {
    return post_text;
  }

  public void setPost_text(String post_text) {
    this.post_text = post_text;
  }

  public String getPublisher() {
    return publisher;
  }

  public void setPublisher(String publisher) {
    this.publisher = publisher;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getDeleted() {
    return deleted;
  }

  public void setDeleted(String deleted) {
    this.deleted = deleted;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public int getReported() {
    return reported;
  }

  public void setReported(int reported) {
    this.reported = reported;
  }
}
