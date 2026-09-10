import { Component } from '@angular/core';

@Component({
  selector: 'app-login',
  standalone: true,
  template: `
    <div class="login-page">

      <div class="login-card">

        <h1>AI News</h1>

        <p>Sign in to your personalized AI news feed.</p>

        <button
          class="google-button"
          (click)="continueWithGoogle()"
        >
          Continue with Google
        </button>

      </div>

    </div>
  `,
  styles: [`
    .login-page {
      min-height: 100vh;
      display: flex;
      justify-content: center;
      align-items: center;
      background: #f5f5f5;
    }

    .login-card {
      width: 380px;
      padding: 40px;
      background: white;
      border-radius: 16px;
      text-align: center;
      box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
    }

    h1 {
      margin-bottom: 10px;
    }

    p {
      color: #666;
      margin-bottom: 30px;
    }

    .google-button {
      width: 100%;
      padding: 14px;
      border: 1px solid #ddd;
      border-radius: 8px;
      background: white;
      font-size: 16px;
      font-weight: 600;
      cursor: pointer;
    }

    .google-button:hover {
      background: #f7f7f7;
    }
  `]
})
export class Login {

  continueWithGoogle(): void {
    window.location.href =
      'http://localhost:8080/oauth2/authorization/google';
  }
}