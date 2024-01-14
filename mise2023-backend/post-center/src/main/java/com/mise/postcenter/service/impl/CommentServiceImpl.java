package com.mise.postcenter.service.impl;

import com.mise.postcenter.domain.entity.Comment;
import com.mise.postcenter.domain.entity.Post;
import com.mise.postcenter.repository.CommentRepository;
import com.mise.postcenter.repository.PostRepository;
import com.mise.postcenter.service.CommentService;
import com.mise.postcenter.domain.vo.CommentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    static final Long defaultFirstCommentId = 0L;

    @Override
    public Comment createComment(CommentVO commentVO) {
        System.out.println(commentVO.getContent());
        Comment comment = new Comment();
        comment.setCommentId(getLastCommentId() + 1);
        comment.setUserId(Long.valueOf(commentVO.getUserId()));
        Long postId = Long.valueOf(commentVO.getPostId());
        comment.setPostId(postId);
        comment.setContent(commentVO.getContent());
        comment.setCreateTime(new Date());
        // 对应的post评论数+1
        Post post = postRepository.findById(postId).orElse(null);
        if (post != null) {
            post.setCommentNum(post.getCommentNum() + 1);
            postRepository.save(post);
        }
        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findCommentsByPostId(postId);
    }

    public Long getLastCommentId() {
        Comment lastComment = commentRepository.findFirstByOrderByCreateTimeDesc();
        if (lastComment != null) {
            return lastComment.getCommentId();
        }
        return defaultFirstCommentId;
    }

    @Override
    public List<Long> getCommentIdsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findCommentsByPostId(postId);
        List<Long> list = new ArrayList<>();
        for (Comment comment : comments) {
            list.add(comment.getCommentId());
        }
        return list;
    }

    @Override
    public Comment getCommentById(Long id) {
        return commentRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteAll() {
        commentRepository.deleteAll();
    }

    @Override
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }
}
