import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { NewsCard } from '../../shared/news-card/news-card';
import { NewsService } from '../../core/services/news.service';

@Component({
  selector: 'app-feed',
  imports: [
    NewsCard,
    RouterLink
  ],
  templateUrl: './feed.html',
  styleUrl: './feed.css'
})
export class Feed {

  private newsService = inject(NewsService);

  articles = signal<any[]>([]);
  loading = signal(true);
  error = signal(false);

  ngOnInit() {

    this.newsService.getArticles().subscribe({

      next: (articles) => {
        this.articles.set(articles as any[]);
        this.loading.set(false);
      },

      error: (error) => {
        console.error('Failed to load articles:', error);
        this.loading.set(false);
        this.error.set(true);
      }

    });

  }

}