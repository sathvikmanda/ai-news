package com.sathvik.ai_news.repository;

import com.sathvik.ai_news.entity.Article;
import com.sathvik.ai_news.entity.Like;
import com.sathvik.ai_news.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByUserAndArticle(
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
            SELECT l.article.id
            FROM Like l
            WHERE l.user.id = :userId
            """)
    List<Long> findLikedArticleIds(
            @Param("userId") Long userId
    );

    @Query("""
            SELECT l.article.id, COUNT(l.id)
            FROM Like l
            GROUP BY l.article.id
            """)
    List<Object[]> findLikeCounts();

    // Day 16: Get the user's category preferences from liked articles
    @Query("""
            SELECT l.article.category, COUNT(l.id)
            FROM Like l
            WHERE l.user.id = :userId
            GROUP BY l.article.category
            """)
    List<Object[]> findUserCategoryLikeCounts(
            @Param("userId") Long userId
    );

    // Day 16: Get the user's topic preferences from liked articles
    @Query("""
            SELECT t.name, COUNT(l.id)
            FROM Like l
            JOIN l.article a
            JOIN a.topics t
            WHERE l.user.id = :userId
            GROUP BY t.name
            """)
    List<Object[]> findUserTopicLikeCounts(
            @Param("userId") Long userId
    );
}