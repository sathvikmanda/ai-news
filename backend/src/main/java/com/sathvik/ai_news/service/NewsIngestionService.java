package com.sathvik.ai_news.service;

import com.rometools.rome.feed.synd.SyndEnclosure;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import com.sathvik.ai_news.entity.Article;
import com.sathvik.ai_news.repository.ArticleRepository;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class NewsIngestionService {

    private static final int WORDS_PER_MINUTE = 200;

    private final ArticleRepository articleRepository;
    private final TitleNormalizationService titleNormalizationService;
    private final ContentExtractionService contentExtractionService;
    private final TopicService topicService;

    public NewsIngestionService(
            ArticleRepository articleRepository,
            TitleNormalizationService titleNormalizationService,
            ContentExtractionService contentExtractionService,
            TopicService topicService
    ) {
        this.articleRepository = articleRepository;
        this.titleNormalizationService = titleNormalizationService;
        this.contentExtractionService = contentExtractionService;
        this.topicService = topicService;
    }

    public void fetchFeed(
            String feedUrl,
            String source,
            String category
    ) {

        try {

            System.out.println();
            System.out.println("=================================");
            System.out.println("Starting ingestion: " + source);
            System.out.println("Feed: " + feedUrl);
            System.out.println("=================================");

            URL url = new URL(feedUrl);

            SyndFeedInput input = new SyndFeedInput();

            SyndFeed feed = input.build(
                    new XmlReader(url)
            );

            int saved = 0;
            int skipped = 0;
            int extractionFailed = 0;

            for (SyndEntry entry : feed.getEntries()) {

                String articleUrl = entry.getLink();

                // -----------------------------------------
                // 1. Validate URL
                // -----------------------------------------

                if (articleUrl == null || articleUrl.isBlank()) {
                    continue;
                }

                // -----------------------------------------
                // 2. URL-based deduplication
                // -----------------------------------------

                if (articleRepository
                        .findByUrl(articleUrl)
                        .isPresent()) {

                    skipped++;
                    continue;
                }

                String title = entry.getTitle();

                if (title == null || title.isBlank()) {
                    continue;
                }

                // -----------------------------------------
                // 3. Normalize title
                // -----------------------------------------

                String normalizedTitle =
                        titleNormalizationService
                                .normalize(title);

                // -----------------------------------------
                // 4. Normalized-title deduplication
                // -----------------------------------------

                if (articleRepository
                        .findByNormalizedTitle(normalizedTitle)
                        .isPresent()) {

                    skipped++;
                    continue;
                }

                // -----------------------------------------
                // 5. RSS description
                // -----------------------------------------

                String description = null;

                if (entry.getDescription() != null) {

                    description =
                            cleanDescription(
                                    entry.getDescription().getValue()
                            );
                }

                // -----------------------------------------
                // 6. Published date
                // -----------------------------------------

                LocalDateTime publishedAt =
                        extractPublishedDate(entry);

                // -----------------------------------------
                // 7. Extract image from RSS enclosure
                // -----------------------------------------

                String imageUrl =
                        extractImageUrl(entry);

                // -----------------------------------------
                // 8. Extract full article content
                // -----------------------------------------

                System.out.println(
                        "Extracting content: " + title
                );

                String content = null;

                try {

                    content =
                            contentExtractionService
                                    .extractContent(articleUrl);

                } catch (Exception extractionException) {

                    System.err.println(
                            "Content extraction exception for: "
                                    + articleUrl
                    );

                    extractionException.printStackTrace();
                }

                if (content == null || content.isBlank()) {

                    extractionFailed++;

                    System.out.println(
                            "Content extraction failed. "
                                    + "Using RSS description."
                    );

                    content = description;
                }

                // -----------------------------------------
                // 9. Calculate read time
                // -----------------------------------------

                String readTime =
                        calculateReadTime(content);

                // -----------------------------------------
                // 10. Create Article
                // -----------------------------------------

                Article article = new Article(
                        title,
                        description,
                        content,
                        articleUrl,
                        source,
                        publishedAt,
                        imageUrl
                );

                article.setCategory(category);

                article.setNormalizedTitle(
                        normalizedTitle
                );

                article.setReadTime(
                        readTime
                );

                // -----------------------------------------
                // 11. Assign topics
                // -----------------------------------------

                topicService.assignTopics(article);

                // -----------------------------------------
                // 12. Save article
                // -----------------------------------------

                articleRepository.save(article);

                saved++;
            }

            // -----------------------------------------
            // 13. Ingestion summary
            // -----------------------------------------

            System.out.println();
            System.out.println("=================================");
            System.out.println(
                    source + " ingestion complete"
            );
            System.out.println(
                    "Articles fetched: "
                            + feed.getEntries().size()
            );
            System.out.println(
                    "Articles saved: "
                            + saved
            );
            System.out.println(
                    "Duplicates skipped: "
                            + skipped
            );
            System.out.println(
                    "Content extraction failed: "
                            + extractionFailed
            );
            System.out.println("=================================");

        } catch (Exception e) {

            System.err.println();
            System.err.println(
                    "FAILED TO FETCH RSS FEED: "
                            + source
            );

            System.err.println(
                    "Feed URL: " + feedUrl
            );

            e.printStackTrace();
        }
    }

    public void fetchAllFeeds() {

        // -----------------------------------------
        // TechCrunch
        // -----------------------------------------

        fetchFeed(
                "https://techcrunch.com/category/artificial-intelligence/feed/",
                "TechCrunch",
                "AI"
        );

        // -----------------------------------------
        // The Verge
        // -----------------------------------------

        fetchFeed(
                "https://www.theverge.com/rss/ai-artificial-intelligence/index.xml",
                "The Verge",
                "AI"
        );

        // -----------------------------------------
        // MIT Technology Review
        // -----------------------------------------

        fetchFeed(
                "https://www.technologyreview.com/feed/",
                "MIT Technology Review",
                "AI"
        );
    }

    private String cleanDescription(String description) {

        if (description == null || description.isBlank()) {
            return null;
        }

        String cleaned =
                Jsoup.parse(description)
                        .text()
                        .trim();

        return cleaned.isBlank()
                ? null
                : cleaned;
    }

    private LocalDateTime extractPublishedDate(
            SyndEntry entry
    ) {

        Date publishedDate =
                entry.getPublishedDate();

        if (publishedDate == null) {
            publishedDate =
                    entry.getUpdatedDate();
        }

        if (publishedDate == null) {
            return null;
        }

        return publishedDate
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    private String extractImageUrl(
            SyndEntry entry
    ) {

        List<SyndEnclosure> enclosures =
                entry.getEnclosures();

        if (enclosures == null) {
            return null;
        }

        for (SyndEnclosure enclosure : enclosures) {

            String enclosureUrl =
                    enclosure.getUrl();

            String type =
                    enclosure.getType();

            if (enclosureUrl == null
                    || enclosureUrl.isBlank()) {
                continue;
            }

            if (type != null
                    && type.startsWith("image/")) {

                return enclosureUrl;
            }
        }

        return null;
    }

    private String calculateReadTime(
            String content
    ) {

        if (content == null || content.isBlank()) {
            return null;
        }

        String[] words =
                content.trim().split("\\s+");

        int wordCount =
                words.length;

        int minutes =
                (int) Math.ceil(
                        (double) wordCount
                                / WORDS_PER_MINUTE
                );

        minutes =
                Math.max(minutes, 1);

        return minutes + " min read";
    }
}