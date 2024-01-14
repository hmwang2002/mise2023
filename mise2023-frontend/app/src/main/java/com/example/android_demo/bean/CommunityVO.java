package com.example.android_demo.bean;

/**
 * @author SummCoder
 * @date 2023/12/10 16:01
 */
public class CommunityVO {

    /**
     * 社区id
     */
    public long communityID;

    /**
     * 是否公开
     */
    public Boolean isPublic;

    /**
     * 创建时间
     */
    public String createTime;

    /**
     * 社区名称
     */
    public String name;

    public CommunityVO(long communityID, Boolean isPublic, String createTime, String name) {
        this.communityID = communityID;
        this.isPublic = isPublic;
        this.createTime = createTime;
        this.name = name;
    }

    public CommunityVO(){ }
}
