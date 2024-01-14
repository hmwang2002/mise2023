package com.example.android_demo.bean;

/**
 * @author SummCoder
 * @date 2023/12/8 15:06
 */
public class CommunityBean {
    // 记录每一个社区的id
    public long id;
    public int cover;

    public String name;
    public String follow;

    public CommunityBean(long id, int cover, String name, String follow){
        this.id = id;
        this.cover = cover;
        this.name = name;
        this.follow = follow;
    }


}
