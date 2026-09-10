package com.sathvik.ai_news.service;

import com.sathvik.ai_news.dto.AiArticleResponse;
import com.sathvik.ai_news.entity.Article;
import com.sathvik.ai_news.repository.ArticleRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AiProcessingService {

    private static final int MAX_CONTENT_LENGTH = 12000;

    private final ArticleRepository articleRepository;
    private final ChatClient chatClient;

    public AiProcessingService(
            ArticleRepository articleRepository,
            ChatClient.Builder chatClientBuilder
    ) {
        this.articleRepository = articleRepository;
        this.chatClient = chatClientBuilder.build();
    }

    @Transactional
    public AiArticleResponse processArticle(Long articleId) {

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Article not found: " + articleId
                        )
                );

        // -----------------------------------------
        // Already processed
        // -----------------------------------------

        if (article.isAiProcessed()) {

            return new AiArticleResponse(
                    article.getSummary(),
                    article.getWhyItMatters(),
                    article.getKeyPoints()
            );
        }

        // -----------------------------------------
        // Validate content
        // -----------------------------------------

        if (article.getContent() == null
                || article.getContent().isBlank()) {

            throw new RuntimeException(
                    "Article has no content to summarize: "
                            + articleId
            );
        }

        // -----------------------------------------
        // Limit content sent to Gemini
        // -----------------------------------------

        String content = article.getContent();

        if (content.length() > MAX_CONTENT_LENGTH) {

            content = content.substring(
                    0,
                    MAX_CONTENT_LENGTH
            );
        }

        // -----------------------------------------
        // Build AI prompt
        // -----------------------------------------

        String prompt = """
                You are an AI news editor for a technology news platform.

                Analyze the following article and create a concise,
                useful briefing for a reader.

                ARTICLE TITLE:
                %s

                ARTICLE CONTENT:
                %s

                Return:

                1. summary:
                   Write a clear 2-4 sentence summary.
                   Explain what happened and the most important facts.

                2. whyItMatters:
                   Explain in 1-3 sentences why this development
                   matters to the AI/technology industry or users.

                3. keyPoints:
                   Provide 3-5 concise bullet points.
                   Each point should contain one important fact.

                Do not invent facts.
                Do not add information that is not supported by the article.
                Keep the writing neutral and factual.
                """.formatted(
                article.getTitle(),
                content
        );

        // -----------------------------------------
        // Generate AI response
        // -----------------------------------------

        try {

            System.out.println(
                    "AI Processing article "
                            + articleId
                            + " - "
                            + article.getTitle()
            );

            AiArticleResponse aiResponse =
                    chatClient.prompt()
                            .user(prompt)
                            .call()
                            .entity(
                                    AiArticleResponse.class,
                                    spec ->
                                            spec.useProviderStructuredOutput()
                            );

            if (aiResponse == null) {

                throw new RuntimeException(
                        "Gemini returned a null response"
                );
            }

            // -----------------------------------------
            // Save AI result
            // -----------------------------------------

            article.setSummary(
                    aiResponse.summary()
            );

            article.setWhyItMatters(
                    aiResponse.whyItMatters()
            );

            article.setKeyPoints(
                    aiResponse.keyPoints()
            );

            article.setAiProcessed(true);

            articleRepository.save(article);

            System.out.println(
                    "AI Processing successful for article "
                            + articleId
            );

            return aiResponse;

        } catch (Exception e) {

            System.err.println(
                    "AI Processing failed for article "
                            + articleId
            );

            System.err.println(
                    "Title: "
                            + article.getTitle()
            );

            System.err.println(
                    "Reason: "
                            + e.getMessage()
            );

            e.printStackTrace();

            throw new RuntimeException(
                    "Failed to generate content for article "
                            + articleId,
                    e
            );
        }
    }
}