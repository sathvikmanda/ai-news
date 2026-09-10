package com.sathvik.ai_news.dto;

import com.sathvik.ai_news.entity.Article;

import java.time.LocalDateTime;

public record ArticleResponse(
        Long id,
        String title,
        String description,
        String content,
        String summary,
        String whyItMatters,
        String keyPoints,
        String category,
        String source,
        String url,
        String imageUrl,
        LocalDateTime publishedAt,
        String readTime,
        boolean aiProcessed,
        boolean liked,
        boolean saved
) {

    public static ArticleResponse fromEntity(
            Article article,
            boolean liked,
            boolean saved
    ) {

        return new ArticleResponse(
                article.getId(),
                article.getTitle(),
                article.getDescription(),
                article.getContent(),
                article.getSummary(),
                article.getWhyItMatters(),
                article.getKeyPoints(),
                article.getCategory(),
                article.getSource(),
                article.getUrl(),
                article.getImageUrl(),
                article.getPublishedAt(),
                article.getReadTime(),
                article.isAiProcessed(),
                liked,
                saved
        );
    }
}