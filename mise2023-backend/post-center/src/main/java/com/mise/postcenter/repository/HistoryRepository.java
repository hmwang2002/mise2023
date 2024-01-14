package com.mise.postcenter.repository;

import com.mise.postcenter.domain.entity.History;
import com.mise.postcenter.domain.entity.Post;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface HistoryRepository extends MongoRepository<History, Long> {

    List<History> findAllByUserId(Long userId);

    History findFirstByOrderByVisitTimeDesc();
}
