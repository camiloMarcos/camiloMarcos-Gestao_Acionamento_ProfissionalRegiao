
import { Routes } from '@angular/router';
import { Dashboard } from './pages/dashboard/dashboard';
import { Bairros } from './pages/bairros/bairros';
import { Profissionais } from './pages/profissionais/profissionais';

export const routes: Routes = [
  { path: '', component: Dashboard },
  { path: 'bairros', component: Bairros },
  { path: 'profissionais', component: Profissionais }
];