package com.sathvik.ai_news.service;

import com.sathvik.ai_news.entity.Article;
import com.sathvik.ai_news.entity.Topic;
import com.sathvik.ai_news.repository.TopicRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.List;

@Service
public class TopicService {

    private final TopicRepository topicRepository;

    public TopicService(
            TopicRepository topicRepository
    ) {
        this.topicRepository = topicRepository;
    }

    public void assignTopics(Article article) {

        Set<Topic> topics = new HashSet<>();

        String text =
                ((article.getTitle() == null ? "" : article.getTitle())
                        + " "
                        + (article.getDescription() == null
                        ? ""
                        : article.getDescription()))
                        .toLowerCase(Locale.ROOT);

        // OpenAI
        if (containsAny(text, "openai", "chatgpt")) {
            topics.add(getOrCreateTopic("OpenAI"));
        }

        // Anthropic
        if (containsAny(text, "anthropic", "claude")) {
            topics.add(getOrCreateTopic("Anthropic"));
        }

        // Google
        if (containsAny(text, "google", "gemini")) {
            topics.add(getOrCreateTopic("Google"));
        }

        // Microsoft
        if (containsAny(text, "microsoft", "copilot")) {
            topics.add(getOrCreateTopic("Microsoft"));
        }

        // Nvidia / AI hardware
        if (containsAny(
                text,
                "nvidia",
                "gpu",
                "chip",
                "chips",
                "semiconductor",
                "data center",
                "datacenter"
        )) {
            topics.add(getOrCreateTopic("AI Hardware"));
        }

        // AI agents
        if (containsAny(
                text,
                "ai agent",
                "ai agents",
                "agentic",
                "agent"
        )) {
            topics.add(getOrCreateTopic("AI Agents"));
        }

        // Robotics
        if (containsAny(
                text,
                "robot",
                "robotics",
                "robotaxi"
        )) {
            topics.add(getOrCreateTopic("Robotics"));
        }

        // Large language models
        if (containsAny(
                text,
                "llm",
                "large language model",
                "language model"
        )) {
            topics.add(getOrCreateTopic("LLMs"));
        }

        // AI safety
        if (containsAny(
                text,
                "ai safety",
                "ai alignment",
                "ai risk",
                "ai risks",
                "doomer",
                "existential risk"
        )) {
            topics.add(getOrCreateTopic("AI Safety"));
        }

        // AI coding
        if (containsAny(
                text,
                "ai coding",
                "coding assistant",
                "code generation",
                "programming assistant"
        )) {
            topics.add(getOrCreateTopic("AI Coding"));
        }

        article.setTopics(topics);
    }

    private Topic getOrCreateTopic(String name) {

        return topicRepository
                .findAll()
                .stream()
                .filter(topic ->
                        topic.getName()
                                .equalsIgnoreCase(name)
                )
                .findFirst()
                .orElseGet(() ->
                        topicRepository.save(
                                new Topic(name)
                        )
                );
    }
    public void assignTopicsToExistingArticles(
            List<Article> articles
    ) {
        for (Article article : articles) {
            assignTopics(article);
        }
    }

    private boolean containsAny(
            String text,
            String... keywords
    ) {

        for (String keyword : keywords) {

            if (text.contains(keyword)) {
                return true;
            }
        }

        return false;
    }
}