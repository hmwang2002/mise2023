package com.mise.usercenter.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class CommentResponseVO implements Serializable {
    private String commentId;
    private String postId;
    private String content;
    private String userName;
    private String photo;
    private Date createTime;
}
