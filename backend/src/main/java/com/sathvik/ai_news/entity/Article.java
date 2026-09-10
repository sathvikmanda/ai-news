package com.sathvik.ai_news.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "articles")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String whyItMatters;

    @Column(columnDefinition = "TEXT")
    private String keyPoints;

    @Column(nullable = false)
    private String normalizedTitle;

    private String category;

    private String source;

    @Column(nullable = false, unique = true)
    private String url;

    private String imageUrl;

    private LocalDateTime publishedAt;

    private String readTime;

    private boolean aiProcessed = false;

    private LocalDateTime createdAt;

    @ManyToMany
    @JoinTable(
            name = "article_topics",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "topic_id")
    )
    private Set<Topic> topics = new HashSet<>();

    public Article() {
    }

    public Article(
            String title,
            String description,
            String content,
            String url,
            String source,
            LocalDateTime publishedAt,
            String imageUrl
    ) {
        this.title = title;
        this.description = description;
        this.content = content;
        this.url = url;
        this.source = source;
        this.publishedAt = publishedAt;
        this.imageUrl = imageUrl;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getContent() {
        return content;
    }

    public String getSummary() {
        return summary;
    }

    public String getWhyItMatters() {
        return whyItMatters;
    }

    public String getKeyPoints() {
        return keyPoints;
    }

    public String getNormalizedTitle() {
        return normalizedTitle;
    }

    public String getCategory() {
        return category;
    }

    public String getSource() {
        return source;
    }

    public String getUrl() {
        return url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public String getReadTime() {
        return readTime;
    }

    public boolean isAiProcessed() {
        return aiProcessed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Set<Topic> getTopics() {
        return topics;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setWhyItMatters(String whyItMatters) {
        this.whyItMatters = whyItMatters;
    }

    public void setKeyPoints(String keyPoints) {
        this.keyPoints = keyPoints;
    }

    public void setNormalizedTitle(String normalizedTitle) {
        this.normalizedTitle = normalizedTitle;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public void setReadTime(String readTime) {
        this.readTime = readTime;
    }

    public void setAiProcessed(boolean aiProcessed) {
        this.aiProcessed = aiProcessed;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setTopics(Set<Topic> topics) {
        this.topics = topics;
    }
}