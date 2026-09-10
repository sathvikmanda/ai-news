import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-auth-callback',
  standalone: true,
  template: `
    <div class="callback-page">
      <h2>Signing you in...</h2>
      <p>Please wait.</p>
    </div>
  `,
  styles: [`
    .callback-page {
      min-height: 100vh;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
    }
  `]
})
export class AuthCallback implements OnInit {

  constructor(
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {

    const token = this.route.snapshot.queryParamMap.get('token');

    if (!token) {
      console.error('No JWT token received');
      this.router.navigate(['/login']);
      return;
    }

    console.log('JWT received');

    localStorage.setItem('token', token);

    this.router.navigate(['/feed']);
  }
}