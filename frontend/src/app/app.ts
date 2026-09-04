import { Component, OnInit, signal } from '@angular/core';
import { ApiService } from './services/api.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {

  backendMessage = signal('Connecting to backend...');

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    this.apiService.healthCheck().subscribe({
      next: (response) => {
        console.log('Backend response:', response);
        this.backendMessage.set(response);
      },

      error: (error) => {
        console.error('Backend error:', error);
        this.backendMessage.set('Backend connection failed!');
      }
    });
  }
}