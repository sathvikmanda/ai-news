import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

export interface User {
  id: number;
  name: string;
  email: string;
  pictureUrl: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly API_URL = 'http://localhost:8080/api/auth';

  currentUser = signal<User | null>(null);

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  /**
   * Check whether a JWT exists in localStorage.
   */
  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  /**
   * Get the JWT.
   */
  getToken(): string | null {
    return localStorage.getItem('token');
  }

  /**
   * Store the JWT after Google authentication.
   */
  setToken(token: string): void {
    localStorage.setItem('token', token);
  }

  /**
   * Ask the backend who is currently logged in.
   */
  loadCurrentUser(): void {

    if (!this.isLoggedIn()) {
      return;
    }

    this.http.get<User>(`${this.API_URL}/me`)
      .subscribe({
        next: (user) => {
          this.currentUser.set(user);
        },

        error: (error) => {
          console.error('Failed to load current user:', error);

          this.logout();
        }
      });
  }

  /**
   * Log the user out.
   */
  logout(): void {

    localStorage.removeItem('token');

    this.currentUser.set(null);

    this.router.navigate(['/login']);
  }
}