package com.sathvik.ai_news.service;

import com.sathvik.ai_news.dto.ArticleResponse;
import com.sathvik.ai_news.entity.Article;
import com.sathvik.ai_news.repository.ArticleRepository;
import com.sathvik.ai_news.repository.LikeRepository;
import com.sathvik.ai_news.repository.SavedArticleRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final LikeRepository likeRepository;
    private final SavedArticleRepository savedArticleRepository;
    private final FeedRankingService feedRankingService;

    public ArticleService(
            ArticleRepository articleRepository,
            LikeRepository likeRepository,
            SavedArticleRepository savedArticleRepository,
            FeedRankingService feedRankingService
    ) {
        this.articleRepository = articleRepository;
        this.likeRepository = likeRepository;
        this.savedArticleRepository = savedArticleRepository;
        this.feedRankingService = feedRankingService;
    }

    public List<ArticleResponse> getAllArticles(Long userId) {

        List<Article> articles =
                articleRepository.findAllByOrderByPublishedAtDesc();

        // Day 16:
        // Rank articles using global signals
        // plus the current user's preferences.
        articles =
                feedRankingService.rankArticles(
                        articles,
                        userId
                );

        Set<Long> likedArticleIds =
                new HashSet<>();

        Set<Long> savedArticleIds =
                new HashSet<>();

        if (userId != null) {

            likedArticleIds.addAll(
                    likeRepository.findLikedArticleIds(userId)
            );

            savedArticleIds.addAll(
                    savedArticleRepository.findSavedArticleIds(userId)
            );
        }

        return articles
                .stream()
                .map(article ->
                        ArticleResponse.fromEntity(
                                article,
                                likedArticleIds.contains(
                                        article.getId()
                                ),
                                savedArticleIds.contains(
                                        article.getId()
                                )
                        )
                )
                .toList();
    }

    public ArticleResponse getArticleById(
            Long id,
            Long userId
    ) {

        Article article =
                articleRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Article not found: " + id
                                )
                        );

        boolean liked = false;
        boolean saved = false;

        if (userId != null) {

            liked =
                    likeRepository.existsByUserIdAndArticleId(
                            userId,
                            article.getId()
                    );

            saved =
                    savedArticleRepository
                            .existsByUserIdAndArticleId(
                                    userId,
                                    article.getId()
                            );
        }

        return ArticleResponse.fromEntity(
                article,
                liked,
                saved
        );
    }
}