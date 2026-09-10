package com.sathvik.ai_news.service;

import org.springframework.stereotype.Service;

@Service
public class TitleNormalizationService {

    public String normalize(String title) {

        if (title == null) {
            return "";
        }

        return title
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", " ")
                .trim();
    }
}