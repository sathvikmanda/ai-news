package com.sathvik.ai_news.service;

import com.sathvik.ai_news.entity.Article;
import com.sathvik.ai_news.entity.Like;
import com.sathvik.ai_news.entity.SavedArticle;
import com.sathvik.ai_news.entity.User;
import com.sathvik.ai_news.repository.ArticleRepository;
import com.sathvik.ai_news.repository.LikeRepository;
import com.sathvik.ai_news.repository.SavedArticleRepository;
import com.sathvik.ai_news.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArticleInteractionService {

    private final LikeRepository likeRepository;
    private final SavedArticleRepository savedArticleRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    public ArticleInteractionService(
            LikeRepository likeRepository,
            SavedArticleRepository savedArticleRepository,
            ArticleRepository articleRepository,
            UserRepository userRepository
    ) {
        this.likeRepository = likeRepository;
        this.savedArticleRepository = savedArticleRepository;
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void likeArticle(Long userId, Long articleId) {

        User user = getUser(userId);
        Article article = getArticle(articleId);

        if (likeRepository.existsByUserAndArticle(user, article)) {
            return;
        }

        Like like = new Like(user, article);

        likeRepository.save(like);
    }

    @Transactional
    public void unlikeArticle(Long userId, Long articleId) {

        User user = getUser(userId);
        Article article = getArticle(articleId);

        likeRepository
                .findByUserAndArticle(user, article)
                .ifPresent(likeRepository::delete);
    }

    @Transactional
    public void saveArticle(Long userId, Long articleId) {

        User user = getUser(userId);
        Article article = getArticle(articleId);

        if (savedArticleRepository.existsByUserAndArticle(user, article)) {
            return;
        }

        SavedArticle savedArticle =
                new SavedArticle(user, article);

        savedArticleRepository.save(savedArticle);
    }

    @Transactional
    public void unsaveArticle(Long userId, Long articleId) {

        User user = getUser(userId);
        Article article = getArticle(articleId);

        savedArticleRepository
                .findByUserAndArticle(user, article)
                .ifPresent(savedArticleRepository::delete);
    }

    private User getUser(Long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found: " + userId
                        )
                );
    }

    private Article getArticle(Long articleId) {

        return articleRepository.findById(articleId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Article not found: " + articleId
                        )
                );
    }
}