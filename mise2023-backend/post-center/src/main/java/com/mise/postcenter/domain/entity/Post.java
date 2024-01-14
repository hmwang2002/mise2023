package com.mise.postcenter.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "posts")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Post {

    @Id
    private Long postId;
    private Long communityId;
    private Boolean isPublic;
    private List<String> tagList;
    private String title;
    private String content;
    private String photo;
    private Integer commentNum;
    private Integer likeNum;
    private Integer dislikeNum;
    private Long userId;
    private Date createTime;
    private Date lastUpdateTime;

}