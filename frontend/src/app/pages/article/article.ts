import { Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-article',
  templateUrl: './article.html',
  styleUrl: './article.css'
})
export class Article {

  private route = inject(ActivatedRoute);

  articleId = this.route.snapshot.paramMap.get('id');

}