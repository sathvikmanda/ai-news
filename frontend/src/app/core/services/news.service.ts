
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class NewsService {

  private http = inject(HttpClient);

  private apiUrl = 'http://localhost:8080/api/news';

  getArticles() {
    return this.http.get(this.apiUrl);
  }

  getArticle(id: number) {
    return this.http.get(`${this.apiUrl}/${id}`);
  }
}

