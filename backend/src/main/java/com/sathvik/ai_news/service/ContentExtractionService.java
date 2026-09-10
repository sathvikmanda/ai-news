package com.sathvik.ai_news.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class ContentExtractionService {

    public String extractContent(String articleUrl) {

        if (articleUrl == null || articleUrl.isBlank()) {
            return null;
        }

        try {

            Document document = Jsoup.connect(articleUrl)
                    .userAgent(
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                                    "AppleWebKit/537.36 (KHTML, like Gecko) " +
                                    "Chrome/131.0 Safari/537.36"
                    )
                    .timeout(10000)
                    .get();

            // Try common article containers first
            Elements paragraphs = document.select(
                    "article p, " +
                            "[itemprop=articleBody] p, " +
                            ".article-content p, " +
                            ".article-body p, " +
                            ".entry-content p"
            );

            StringBuilder content = new StringBuilder();

            for (Element paragraph : paragraphs) {

                String text = paragraph.text().trim();

                if (!text.isBlank()) {

                    if (!content.isEmpty()) {
                        content.append("\n\n");
                    }

                    content.append(text);
                }
            }

            String extractedContent = content.toString().trim();

            // If no article paragraphs were found,
            // return null rather than storing junk HTML.
            if (extractedContent.isBlank()) {
                return null;
            }

            return extractedContent;

        } catch (Exception e) {

            System.err.println(
                    "Failed to extract article content: "
                            + articleUrl
            );

            System.err.println(
                    "Reason: " + e.getMessage()
            );

            return null;
        }
    }
}