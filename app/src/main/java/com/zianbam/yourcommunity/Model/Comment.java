package com.zianbam.yourcommunity.Model;

public class Comment {
    private String comment, publisherid, commentid, userid, notificationid,postid,pointerid, type;
    private int reported;

    public Comment() {
    }

    public Comment(String comment, String publisherid, String commentid, String userid, String notificationid, String postid, String pointerid, String type, int reported) {
        this.comment = comment;
        this.publisherid = publisherid;
        this.commentid = commentid;
        this.userid = userid;
        this.notificationid = notificationid;
        this.postid = postid;
        this.pointerid = pointerid;
        this.type = type;
        this.reported = reported;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPublisherid() {
        return publisherid;
    }

    public void setPublisherid(String publisherid) {
        this.publisherid = publisherid;
    }

    public String getCommentid() {
        return commentid;
    }

    public void setCommentid(String commentid) {
        this.commentid = commentid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getNotificationid() {
        return notificationid;
    }

    public void setNotificationid(String notificationid) {
        this.notificationid = notificationid;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPointerid() {
        return pointerid;
    }

    public void setPointerid(String pointerid) {
        this.pointerid = pointerid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getReported() {
        return reported;
    }

    public void setReported(int reported) {
        this.reported = reported;
    }
}
