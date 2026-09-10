package com.sathvik.ai_news.service;

import com.sathvik.ai_news.entity.Article;
import com.sathvik.ai_news.repository.ArticleRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class AiProcessingScheduler {

    private final ArticleRepository articleRepository;
    private final AiProcessingService aiProcessingService;

    public AiProcessingScheduler(
            ArticleRepository articleRepository,
            AiProcessingService aiProcessingService
    ) {
        this.articleRepository = articleRepository;
        this.aiProcessingService = aiProcessingService;
    }

    // Temporarily disabled while Gemini free-tier quota is exhausted.
    // @Scheduled(
    //         fixedDelay = 5400000,
    //         initialDelay = 5400000
    // )
    public void processPendingArticles() {

        Article article =
                articleRepository
                        .findFirstByAiProcessedFalseOrderByPublishedAtDesc()
                        .orElse(null);

        if (article == null) {
            return;
        }

        try {

            System.out.println(
                    "AI Scheduler: Processing article "
                            + article.getId()
                            + " - "
                            + article.getTitle()
            );

            aiProcessingService.processArticle(
                    article.getId()
            );

            System.out.println(
                    "AI Scheduler: Successfully processed article "
                            + article.getId()
            );

        } catch (Exception e) {

            System.err.println(
                    "AI Scheduler: Failed to process article "
                            + article.getId()
            );

            System.err.println(
                    "Reason: " + e.getMessage()
            );
        }
    }
}