import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class NewsService {

  private http = inject(HttpClient);

  private apiUrl = 'http://localhost:8080/api/articles';

  getArticles() {
    return this.http.get(this.apiUrl);
  }

  getArticle(id: number) {
    return this.http.get(`${this.apiUrl}/${id}`);
  }

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