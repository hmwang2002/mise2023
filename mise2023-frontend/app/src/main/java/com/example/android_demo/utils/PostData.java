package com.example.android_demo.utils;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

public class PostData {
    private String code;
    private String message;
    private List<Post> data;

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public List<Post> getData() {
        return data;
    }

    public static class Post implements Serializable {
        private String postId;
        private String communityName;
        private boolean isPublic;
        private List<String> tagList;
        private String title;
        private String content;
        private String photo;
        private String commentNum;
        private String likeNum;
        private String dislikeNum;
        private String userName;
        private String createTime;
        private String lastUpdateTime;

        private String postPicture;

        public String getPostId() {
            return postId;
        }

        public String getCommunityName() {
            return communityName;
        }

        public void setCommunityName(String communityName) {
            this.communityName = communityName;
        }

        public boolean isPublic() {
            return isPublic;
        }

        public List<String> getTagList() {
            return tagList;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

        public String getPhoto() {
            return photo;
        }

        public String getCommentNum() {
            return commentNum;
        }

        public String getLikeNum() {
            return likeNum;
        }

        public String getDislikeNum() {
            return dislikeNum;
        }

        public String getUserName() {
            return userName;
        }

        public String getCreateTime() {
            return createTime;
        }

        public String getLastUpdateTime() {
            return lastUpdateTime;
        }

        public void setPostId(String postId) {
            this.postId = postId;
        }


        public void setPublic(boolean aPublic) {
            isPublic = aPublic;
        }

        public void setTagList(List<String> tagList) {
            this.tagList = tagList;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }

        public void setCommentNum(String commentNum) {
            this.commentNum = commentNum;
        }

        public void setLikeNum(String likeNum) {
            this.likeNum = likeNum;
        }

        public void setDislikeNum(String dislikeNum) {
            this.dislikeNum = dislikeNum;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public void setLastUpdateTime(String lastUpdateTime) {
            this.lastUpdateTime = lastUpdateTime;
        }

        public String getPostPicture() {
            return postPicture;
        }

        public void setPostPicture(String postPicture) {
            this.postPicture = postPicture;
        }
    }
}
