package com.mise.communitycenter.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class PostVO {

    private long postID;

    private String title;

    private String content;

    private List<String> tagList;

    private String photo;

    private String communityId;

    private Boolean isPublic;
}
