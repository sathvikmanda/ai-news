package com.sathvik.ai_news.repository;

import com.sathvik.ai_news.entity.Article;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    Optional<Article> findByUrl(String url);

    Optional<Article> findByNormalizedTitle(String normalizedTitle);

    List<Article> findByAiProcessedFalse();

    Optional<Article> findFirstByAiProcessedFalseOrderByPublishedAtDesc();

    @EntityGraph(attributePaths = "topics")
    List<Article> findAllByOrderByPublishedAtDesc();
}