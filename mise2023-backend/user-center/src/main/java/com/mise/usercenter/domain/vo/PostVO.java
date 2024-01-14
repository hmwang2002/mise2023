package com.mise.usercenter.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PostVO implements Serializable {
    private String userId;
    private String title;
    private String content;
    private List<String> tagList;
    private String photo;
    private String communityId;
    private Boolean isPublic;
}
