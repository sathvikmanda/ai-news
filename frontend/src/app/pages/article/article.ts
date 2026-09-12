import {
  Component,
  inject,
  signal
} from '@angular/core';

import { DatePipe } from '@angular/common';

import {
  ActivatedRoute,
  RouterLink
} from '@angular/router';

import {
  NewsService,
  Article as ArticleModel
} from '../../core/services/news.service';

@Component({
  selector: 'app-article',
  imports: [
    DatePipe,
    RouterLink
  ],
  templateUrl: './article.html',
  styleUrl: './article.css'
})
export class Article {

  private route = inject(ActivatedRoute);

  private newsService = inject(NewsService);


  // ---------- ARTICLE STATE ----------

  article = signal<ArticleModel | null>(null);

  loading = signal(true);

  error = signal(false);


  // ---------- INTERACTION STATE ----------

  liked = signal(false);

  saved = signal(false);

  likeLoading = signal(false);

  saveLoading = signal(false);


  // ---------- LOAD ARTICLE ----------

  ngOnInit(): void {

    const id = this.route.snapshot.paramMap.get('id');

    if (!id) {

      console.error(
        'No article ID found in route'
      );

      this.loading.set(false);

      this.error.set(true);

      return;
    }


    this.newsService
      .getArticle(Number(id))
      .subscribe({

        next: (article) => {

          console.log(
            'Article loaded:',
            article
          );

          this.article.set(article);

          this.liked.set(
            article.liked === true
          );

          this.saved.set(
            article.saved === true
          );

          this.loading.set(false);

        },

        error: (error) => {

          console.error(
            'Failed to load article:',
            error
          );

          this.loading.set(false);

          this.error.set(true);

        }

      });

  }


  // ---------- LIKE ----------

  toggleLike(event: MouseEvent): void {

    event.preventDefault();

    event.stopPropagation();


    const article = this.article();

    if (!article) {

      return;

    }


    const articleId = article.id;

    const wasLiked = this.liked();


    if (this.likeLoading()) {

      return;

    }


    // Optimistic UI update

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


        // Roll back UI

        this.liked.set(wasLiked);

      },

      complete: () => {

        this.likeLoading.set(false);

      }

    });

  }


  // ---------- SAVE ----------

  toggleSave(event: MouseEvent): void {

    event.preventDefault();

    event.stopPropagation();


    const article = this.article();

    if (!article) {

      return;

    }


    const articleId = article.id;

    const wasSaved = this.saved();


    if (this.saveLoading()) {

      return;

    }


    // Optimistic UI update

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


        // Roll back UI

        this.saved.set(wasSaved);

      },

      complete: () => {

        this.saveLoading.set(false);

      }

    });

  }

}