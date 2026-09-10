package com.sathvik.ai_news.repository;

import com.sathvik.ai_news.entity.Article;
import com.sathvik.ai_news.entity.SavedArticle;
import com.sathvik.ai_news.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SavedArticleRepository
        extends JpaRepository<SavedArticle, Long> {

    Optional<SavedArticle> findByUserAndArticle(
            User user,
            Article article
    );

    boolean existsByUserAndArticle(
            User user,
            Article article
    );

    boolean existsByUserIdAndArticleId(
            Long userId,
            Long articleId
    );

    @Query("""
            SELECT s.article.id
            FROM SavedArticle s
            WHERE s.user.id = :userId
            """)
    List<Long> findSavedArticleIds(
            @Param("userId") Long userId
    );

    // Day 16: Get the user's category preferences from saved articles
    @Query("""
            SELECT s.article.category, COUNT(s.id)
            FROM SavedArticle s
            WHERE s.user.id = :userId
            GROUP BY s.article.category
            """)
    List<Object[]> findUserCategorySaveCounts(
            @Param("userId") Long userId
    );

    // Day 16: Get the user's topic preferences from saved articles
    @Query("""
            SELECT t.name, COUNT(s.id)
            FROM SavedArticle s
            JOIN s.article a
            JOIN a.topics t
            WHERE s.user.id = :userId
            GROUP BY t.name
            """)
    List<Object[]> findUserTopicSaveCounts(
            @Param("userId") Long userId
    );
}