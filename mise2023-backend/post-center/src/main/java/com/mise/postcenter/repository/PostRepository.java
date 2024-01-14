package com.mise.postcenter.repository;

import com.mise.postcenter.domain.entity.Post;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface PostRepository extends MongoRepository<Post, Long> {

    List<Post> findByTitle(String title); // 根据标题查询

    List<Post> findByUserId(Long userId); // 根据作者查询

    List<Post> findByTitleAndUserId(String title, Long userId); // 根据标题和作者查询

    List<Post> findByTitleContaining(String keyword); // 根据标题模糊查询

    List<Post> findByCreateTimeBetween(Date start, Date end); // 根据创建时间范围查询

    Post findFirstByOrderByCreateTimeDesc();

    List<Post> findPostByCommunityId(Long communityId);

    List<Post> findPostByUserId(Long userId);

    List<Post> findTop20ByOrderByCreateTimeDesc();
}