import { Component, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { NewsService } from '../../core/services/news.service';

@Component({
  selector: 'app-article',
  imports: [DatePipe],
  templateUrl: './article.html',
  styleUrl: './article.css'
})
export class Article {

  private route = inject(ActivatedRoute);
  private newsService = inject(NewsService);

  article = signal<any | null>(null);
  loading = signal(true);
  error = signal(false);

  ngOnInit() {

    const id = this.route.snapshot.paramMap.get('id');

    if (!id) {
      console.error('No article ID found in route');
      this.loading.set(false);
      this.error.set(true);
      return;
    }

    this.newsService.getArticle(Number(id)).subscribe({

      next: (article) => {

        console.log('Article loaded:', article);

        this.article.set(article as any);
        this.loading.set(false);
      },

      error: (error) => {

        console.error('Failed to load article:', error);

        this.loading.set(false);
        this.error.set(true);
      }
    });
  }
}