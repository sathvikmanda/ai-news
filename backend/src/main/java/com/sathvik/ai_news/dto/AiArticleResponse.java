package com.sathvik.ai_news.dto;

public record AiArticleResponse(
        String summary,
        String whyItMatters,
        String keyPoints
) {
}