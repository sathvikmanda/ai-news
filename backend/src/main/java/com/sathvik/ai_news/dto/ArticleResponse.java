package com.sathvik.ai_news.dto;

import com.sathvik.ai_news.entity.Article;
import com.sathvik.ai_news.entity.Topic;

import java.time.LocalDateTime;
import java.util.List;

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
        boolean saved,
        List<String> topics
) {

    public static ArticleResponse fromEntity(
            Article article,
            boolean liked,
            boolean saved
    ) {

        List<String> topics =
                article.getTopics()
                        .stream()
                        .map(Topic::getName)
                        .toList();

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
                saved,
                topics
        );
    }
}