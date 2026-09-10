package com.sathvik.ai_news.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private String pictureUrl;

    @Column(unique = true)
    private String googleId;

    @Column(nullable = false)
    private String authProvider;

    private LocalDateTime createdAt;

    public User() {
    }

    public User(
            String name,
            String email,
            String pictureUrl,
            String googleId,
            String authProvider
    ) {
        this.name = name;
        this.email = email;
        this.pictureUrl = pictureUrl;
        this.googleId = googleId;
        this.authProvider = authProvider;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public String getGoogleId() {
        return googleId;
    }

    public String getAuthProvider() {
        return authProvider;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public void setAuthProvider(String authProvider) {
        this.authProvider = authProvider;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}