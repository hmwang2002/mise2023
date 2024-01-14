package com.example.android_demo.utils;

import java.io.Serializable;
import java.util.List;

public class CommentData {

    private String code;
    private String message;
    private List<Comment> data;

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public List<Comment> getData() {
        return data;
    }

    public static class Comment implements Serializable {
        private String commentId;
        private String userName;
        private String photo;
        private String postId;
        private String content;
        private String createTime;

        public String getCommentId() {
            return commentId;
        }

        public void setCommentId(String commentId) {
            this.commentId = commentId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }

        public String getPostId() {
            return postId;
        }

        public void setPostId(String postId) {
            this.postId = postId;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }
    }

    public static class CommentVO implements Serializable {
        private String userId;
        private String postId;

        private String content;

        public CommentVO(String userId, String postId, String content) {
            this.userId = userId;
            this.postId = postId;
            this.content = content;
        }


        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getPostId() {
            return postId;
        }

        public void setPostId(String postId) {
            this.postId = postId;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
