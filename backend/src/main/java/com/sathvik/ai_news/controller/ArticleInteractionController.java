package com.sathvik.ai_news.controller;

import com.sathvik.ai_news.service.ArticleInteractionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/articles")
public class ArticleInteractionController {

    private final ArticleInteractionService interactionService;

    public ArticleInteractionController(
            ArticleInteractionService interactionService
    ) {
        this.interactionService = interactionService;
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<String> likeArticle(
            @PathVariable Long id,
            Authentication authentication
    ) {

        Long userId = getUserId(authentication);

        interactionService.likeArticle(userId, id);

        return ResponseEntity.ok("Article liked");
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<String> unlikeArticle(
            @PathVariable Long id,
            Authentication authentication
    ) {

        Long userId = getUserId(authentication);

        interactionService.unlikeArticle(userId, id);

        return ResponseEntity.ok("Article unliked");
    }

    @PostMapping("/{id}/save")
    public ResponseEntity<String> saveArticle(
            @PathVariable Long id,
            Authentication authentication
    ) {

        Long userId = getUserId(authentication);

        interactionService.saveArticle(userId, id);

        return ResponseEntity.ok("Article saved");
    }

    @DeleteMapping("/{id}/save")
    public ResponseEntity<String> unsaveArticle(
            @PathVariable Long id,
            Authentication authentication
    ) {

        Long userId = getUserId(authentication);

        interactionService.unsaveArticle(userId, id);

        return ResponseEntity.ok("Article unsaved");
    }

    private Long getUserId(Authentication authentication) {

        if (authentication == null
                || authentication.getPrincipal() == null) {

            throw new RuntimeException(
                    "User is not authenticated"
            );
        }

        return (Long) authentication.getPrincipal();
    }
}