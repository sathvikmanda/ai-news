import { Component, signal } from '@angular/core';
import { NewsCard } from '../../shared/news-card/news-card';

@Component({
  selector: 'app-feed',
  imports: [NewsCard],
  templateUrl: './feed.html',
  styleUrl: './feed.css'
})
export class Feed {

  articles = signal([
    {
      id: 1,
      title: 'OpenAI announces new AI model',
      summary: 'A new generation of AI models is changing the industry.',
      source: 'Tech News',
      category: 'AI'
    },
    {
      id: 2,
      title: 'Google advances Gemini',
      summary: 'Google announces new capabilities for its AI platform.',
      source: 'AI Weekly',
      category: 'AI'
    }
  ]);

}