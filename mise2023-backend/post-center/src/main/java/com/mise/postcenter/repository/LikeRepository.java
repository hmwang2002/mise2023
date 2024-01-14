package com.mise.postcenter.repository;

import com.mise.postcenter.domain.entity.Comment;
import com.mise.postcenter.domain.entity.Like;
import com.mise.postcenter.domain.entity.Post;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LikeRepository extends MongoRepository<Like, Long> {


    Like findFirstByOrderByLikeTimeDesc();


    List<Like> findAllByUserId(Long userId);
}
