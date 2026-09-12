import {
  Component,
  inject,
  input,
  signal
} from '@angular/core';

import { RouterLink } from '@angular/router';

import { NewsService } from '../../core/services/news.service';

@Component({
  selector: 'app-news-card',
  imports: [RouterLink],
  templateUrl: './news-card.html',
  styleUrl: './news-card.css'
})
export class NewsCard {

  private newsService = inject(NewsService);

  article = input<any>();

  liked = signal(false);
  saved = signal(false);

  likeLoading = signal(false);
  saveLoading = signal(false);

  ngOnInit(): void {

    const article = this.article();

    if (!article) {
      return;
    }

    this.liked.set(article.liked === true);
    this.saved.set(article.saved === true);

  }

  toggleLike(event: MouseEvent): void {

    event.preventDefault();
    event.stopPropagation();

    const articleId = this.article().id;
    const wasLiked = this.liked();

    if (this.likeLoading()) {
      return;
    }

    this.liked.set(!wasLiked);
    this.likeLoading.set(true);

    const request = wasLiked
      ? this.newsService.unlikeArticle(articleId)
      : this.newsService.likeArticle(articleId);

    request.subscribe({

      next: () => {

        console.log(
          wasLiked
            ? 'Article unliked successfully'
            : 'Article liked successfully'
        );

      },

      error: (error) => {

        console.error(
          wasLiked
            ? 'Failed to unlike article'
            : 'Failed to like article',
          error
        );

        // Roll back optimistic update
        this.liked.set(wasLiked);

      },

      complete: () => {

        this.likeLoading.set(false);

      }

    });

  }

  toggleSave(event: MouseEvent): void {

    event.preventDefault();
    event.stopPropagation();

    const articleId = this.article().id;
    const wasSaved = this.saved();

    if (this.saveLoading()) {
      return;
    }

    this.saved.set(!wasSaved);
    this.saveLoading.set(true);

    const request = wasSaved
      ? this.newsService.unsaveArticle(articleId)
      : this.newsService.saveArticle(articleId);

    request.subscribe({

      next: () => {

        console.log(
          wasSaved
            ? 'Article unsaved successfully'
            : 'Article saved successfully'
        );

      },

      error: (error) => {

        console.error(
          wasSaved
            ? 'Failed to unsave article'
            : 'Failed to save article',
          error
        );

        // Roll back optimistic update
        this.saved.set(wasSaved);

      },

      complete: () => {

        this.saveLoading.set(false);

      }

    });

  }

}