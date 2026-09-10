package com.sathvik.ai_news.repository;

import com.sathvik.ai_news.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Long> {
}