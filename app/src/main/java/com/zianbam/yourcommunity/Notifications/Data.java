package com.zianbam.yourcommunity.Notifications;

public class Data {
  private String user;
  private int icon;
  private  String body;
  private  String title;
  private  String sented, postURL, type, contentID;

  public Data() {
  }

  public Data(String user, int icon, String body, String title, String sented, String postURL, String type, String contentID) {
    this.user = user;
    this.icon = icon;
    this.body = body;
    this.title = title;
    this.sented = sented;
    this.postURL = postURL;
    this.type = type;
    this.contentID = contentID;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public int getIcon() {
    return icon;
  }

  public void setIcon(int icon) {
    this.icon = icon;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getSented() {
    return sented;
  }

  public void setSented(String sented) {
    this.sented = sented;
  }

  public String getPostURL() {
    return postURL;
  }

  public void setPostURL(String postURL) {
    this.postURL = postURL;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getContentID() {
    return contentID;
  }

  public void setContentID(String contentID) {
    this.contentID = contentID;
  }
}
