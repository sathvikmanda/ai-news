import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Article {
  id: number;
  title: string;
  description: string;
  content: string;
  summary: string | null;
  whyItMatters: string | null;
  keyPoints: string | null;
  category: string;
  source: string;
  url: string;
  imageUrl: string | null;
  publishedAt: string;
  readTime: string;
  aiProcessed: boolean;
  liked: boolean;
  saved: boolean;
  topics: string[];
}

@Injectable({
  providedIn: 'root'
})
export class NewsService {

  private http = inject(HttpClient);

  private apiUrl = 'http://localhost:8080/api/articles';


  // ---------- ARTICLES ----------

  getArticles(): Observable<Article[]> {

    return this.http.get<Article[]>(this.apiUrl);

  }


  getArticle(id: number): Observable<Article> {

    return this.http.get<Article>(
      `${this.apiUrl}/${id}`
    );

  }


  // ---------- LIKE ----------

  likeArticle(id: number) {

    return this.http.post(
      `${this.apiUrl}/${id}/like`,
      {},
      {
        responseType: 'text'
      }
    );

  }


  unlikeArticle(id: number) {

    return this.http.delete(
      `${this.apiUrl}/${id}/like`,
      {
        responseType: 'text'
      }
    );

  }


  // ---------- SAVE ----------

  saveArticle(id: number) {

    return this.http.post(
      `${this.apiUrl}/${id}/save`,
      {},
      {
        responseType: 'text'
      }
    );

  }


  unsaveArticle(id: number) {

    return this.http.delete(
      `${this.apiUrl}/${id}/save`,
      {
        responseType: 'text'
      }
    );

  }

}