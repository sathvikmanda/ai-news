import { Routes } from '@angular/router';

import { Login } from './pages/login/login';
import { Register } from './pages/register/register';
import { Feed } from './pages/feed/feed';
import { Article } from './pages/article/article';
import { Profile } from './pages/profile/profile';

import { authGuard } from './services/auth.guard';

export const routes: Routes = [

  {
    path: '',
    redirectTo: 'feed',
    pathMatch: 'full'
  },

  {
    path: 'login',
    component: Login
  },

  {
    path: 'register',
    component: Register
  },

  {
    path: 'feed',
    component: Feed,
    canActivate: [authGuard]
  },

  {
    path: 'article/:id',
    component: Article,
    canActivate: [authGuard]
  },

  {
    path: 'profile',
    component: Profile,
    canActivate: [authGuard]
  },

  {
    path: 'auth/callback',
    loadComponent: () =>
      import('./auth-callback/auth-callback.component')
        .then(m => m.AuthCallbackComponent)
  }

];