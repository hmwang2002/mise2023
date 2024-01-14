package com.example.android_demo.bean;

/**
 * @author SummCoder
 * @date 2023/11/25 19:00
 */
public class PostBean {
    public Long postId;
    public Long communityId;
    public String title;
    public String content;
    public int photo;
    public PostBean(long id, long communityId, String title, String content,int photo){
        this.postId = id;
        this.communityId = communityId;
        this.title = title;
        this.content = content;
        this.photo=photo;
    }
}
