package com.mise.postcenter.domain.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class CommentVO implements Serializable {
    private String userId;
    private String postId;
    private String content;
}
