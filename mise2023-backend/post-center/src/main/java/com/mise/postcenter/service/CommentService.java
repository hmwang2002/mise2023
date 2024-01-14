package com.mise.postcenter.service;

import com.mise.postcenter.domain.entity.Comment;
import com.mise.postcenter.domain.vo.CommentVO;

import java.util.List;

public interface CommentService {

    Comment createComment(CommentVO commentVO);

    List<Comment> getCommentsByPostId(Long postId);

    List<Long> getCommentIdsByPostId(Long postId);

    Comment getCommentById(Long id);

    void deleteAll();

    void deleteComment(Long id);
}
