package com.sathvik.ai_news.controller;

import com.sathvik.ai_news.dto.AiArticleResponse;
import com.sathvik.ai_news.dto.ArticleResponse;
import com.sathvik.ai_news.service.AiProcessingService;
import com.sathvik.ai_news.service.ArticleService;
import com.sathvik.ai_news.service.NewsIngestionService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;
    private final NewsIngestionService newsIngestionService;
    private final AiProcessingService aiProcessingService;

    public ArticleController(
            ArticleService articleService,
            NewsIngestionService newsIngestionService,
            AiProcessingService aiProcessingService
    ) {
        this.articleService = articleService;
        this.newsIngestionService = newsIngestionService;
        this.aiProcessingService = aiProcessingService;
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