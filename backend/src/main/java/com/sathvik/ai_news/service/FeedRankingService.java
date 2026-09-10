package com.sathvik.ai_news.service;

import com.sathvik.ai_news.entity.Article;
import com.sathvik.ai_news.entity.Topic;
import com.sathvik.ai_news.repository.LikeRepository;
import com.sathvik.ai_news.repository.SavedArticleRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FeedRankingService {

    private final LikeRepository likeRepository;
    private final SavedArticleRepository savedArticleRepository;

    public FeedRankingService(
            LikeRepository likeRepository,
            SavedArticleRepository savedArticleRepository
    ) {
        this.likeRepository = likeRepository;
        this.savedArticleRepository = savedArticleRepository;
    }

    public List<Article> rankArticles(
            List<Article> articles,
            Long userId
    ) {

        Map<Long, Long> likeCounts =
                getLikeCounts();

        Map<String, Long> userTopicPreferences =
                getUserTopicPreferences(userId);

        return articles.stream()
                .sorted(
                        Comparator.comparingDouble(
                                (Article article) ->
                                        calculateScore(
                                                article,
                                                likeCounts,
                                                userTopicPreferences
                                        )
                        ).reversed()
                )
                .toList();
    }

    private double calculateScore(
            Article article,
            Map<Long, Long> likeCounts,
            Map<String, Long> userTopicPreferences
    ) {

        double recencyScore =
                calculateRecencyScore(
                        article.getPublishedAt()
                );

        long likes =
                likeCounts.getOrDefault(
                        article.getId(),
                        0L
                );

        double popularityScore =
                calculatePopularityScore(likes);

        double sourceQualityScore =
                calculateSourceQualityScore(
                        article.getSource()
                );

        double personalizationScore =
                calculatePersonalizationScore(
                        article,
                        userTopicPreferences
                );

        return
                recencyScore
                        + popularityScore
                        + sourceQualityScore
                        + personalizationScore;
    }

    private double calculateRecencyScore(
            LocalDateTime publishedAt
    ) {

        if (publishedAt == null) {
            return 0;
        }

        long hoursOld =
                Duration.between(
                        publishedAt,
                        LocalDateTime.now()
                ).toHours();

        if (hoursOld <= 6) {
            return 30;
        }

        if (hoursOld <= 24) {
            return 25;
        }

        if (hoursOld <= 72) {
            return 18;
        }

        if (hoursOld <= 168) {
            return 10;
        }

        return 5;
    }

    private double calculatePopularityScore(
            long likes
    ) {

        return Math.min(
                likes * 4,
                20
        );
    }

    private double calculateSourceQualityScore(
            String source
    ) {

        if (source == null) {
            return 0;
        }

        String normalizedSource =
                source.toLowerCase();

        if (
                normalizedSource.contains("openai")
                        || normalizedSource.contains("google")
                        || normalizedSource.contains("microsoft")
                        || normalizedSource.contains("anthropic")
                        || normalizedSource.contains("nvidia")
                        || normalizedSource.contains("meta")
                        || normalizedSource.contains("mit")
                        || normalizedSource.contains("stanford")
                        || normalizedSource.contains("arxiv")
        ) {
            return 10;
        }

        return 5;
    }

    private double calculatePersonalizationScore(
            Article article,
            Map<String, Long> userTopicPreferences
    ) {

        if (
                article.getTopics() == null
                        || article.getTopics().isEmpty()
        ) {
            return 0;
        }

        double score = 0;

        for (Topic topic : article.getTopics()) {

            if (topic == null || topic.getName() == null) {
                continue;
            }

            long preferenceCount =
                    userTopicPreferences.getOrDefault(
                            topic.getName().toLowerCase(),
                            0L
                    );

            score += preferenceCount * 3;
        }

        return Math.min(score, 15);
    }

    private Map<Long, Long> getLikeCounts() {

        List<Object[]> results =
                likeRepository.findLikeCounts();

        Map<Long, Long> counts =
                new HashMap<>();

        for (Object[] result : results) {

            Long articleId =
                    (Long) result[0];

            Long count =
                    (Long) result[1];

            counts.put(
                    articleId,
                    count
            );
        }

        return counts;
    }

    private Map<String, Long> getUserTopicPreferences(
            Long userId
    ) {

        Map<String, Long> preferences =
                new HashMap<>();

        if (userId == null) {
            return preferences;
        }

        // Likes contribute to topic preferences
        List<Object[]> likedTopics =
                likeRepository.findUserTopicLikeCounts(
                        userId
                );

        for (Object[] result : likedTopics) {

            String topicName =
                    (String) result[0];

            Long count =
                    (Long) result[1];

            if (topicName != null) {

                preferences.merge(
                        topicName.toLowerCase(),
                        count,
                        Long::sum
                );
            }
        }

        // Saves also contribute to topic preferences
        List<Object[]> savedTopics =
                savedArticleRepository
                        .findUserTopicSaveCounts(
                                userId
                        );

        for (Object[] result : savedTopics) {

            String topicName =
                    (String) result[0];

            Long count =
                    (Long) result[1];

            if (topicName != null) {

                preferences.merge(
                        topicName.toLowerCase(),
                        count,
                        Long::sum
                );
            }
        }

        return preferences;
    }
}