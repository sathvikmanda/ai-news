package com.sathvik.ai_news.controller;

import com.sathvik.ai_news.dto.AiArticleResponse;
import com.sathvik.ai_news.dto.ArticleResponse;
import com.sathvik.ai_news.entity.Article;
import com.sathvik.ai_news.repository.ArticleRepository;
import com.sathvik.ai_news.service.AiProcessingService;
import com.sathvik.ai_news.service.ArticleService;
import com.sathvik.ai_news.service.NewsIngestionService;
import com.sathvik.ai_news.service.TopicService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;
    private final NewsIngestionService newsIngestionService;
    private final AiProcessingService aiProcessingService;
    private final ArticleRepository articleRepository;
    private final TopicService topicService;

    public ArticleController(
            ArticleService articleService,
            NewsIngestionService newsIngestionService,
            AiProcessingService aiProcessingService,
            ArticleRepository articleRepository,
            TopicService topicService
    ) {
        this.articleService = articleService;
        this.newsIngestionService = newsIngestionService;
        this.aiProcessingService = aiProcessingService;
        this.articleRepository = articleRepository;
        this.topicService = topicService;
    }

    @GetMapping
    public List<ArticleResponse> getArticles(
            Authentication authentication
    ) {

        Long userId = getUserId(authentication);

        return articleService.getAllArticles(userId);
    }

    @GetMapping("/{id}")
    public ArticleResponse getArticle(
            @PathVariable Long id,
            Authentication authentication
    ) {

        Long userId = getUserId(authentication);

        return articleService.getArticleById(
                id,
                userId
        );
    }

    @PostMapping("/ingest")
    public String ingestNews() {

        newsIngestionService.fetchAllFeeds();

        return "News ingestion completed";
    }

    @PostMapping("/backfill-topics")
    public String backfillTopics() {

        List<Article> articles =
                articleRepository.findAll();

        for (Article article : articles) {

            topicService.assignTopics(article);

            articleRepository.save(article);
        }

        return "Topic backfill completed for "
                + articles.size()
                + " articles";
    }

    @PostMapping("/{id}/ai-process")
    public AiArticleResponse processArticleWithAi(
            @PathVariable Long id
    ) {

        return aiProcessingService.processArticle(id);
    }

    private Long getUserId(
            Authentication authentication
    ) {

        if (authentication == null
                || authentication.getPrincipal() == null) {

            return null;
        }

        Object principal =
                authentication.getPrincipal();

        if (principal instanceof Long userId) {
            return userId;
        }

        return null;
    }
}