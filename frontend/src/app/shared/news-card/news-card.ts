import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-news-card',
  imports: [RouterLink],
  templateUrl: './news-card.html',
  styleUrl: './news-card.css'
})
export class NewsCard {

  article = input<any>();

}