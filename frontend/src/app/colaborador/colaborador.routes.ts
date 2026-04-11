import { Routes } from '@angular/router';
import { Layout } from './layout/layout';
import { Dashboard } from './pages/dashboard/dashboard';
import { Bairros } from './pages/bairros/bairros';
import { Profissionais } from './pages/profissionais/profissionais';

export const colaboradorRoutes: Routes = [
  {
    path: '',
    component: Layout,
    children: [
      { path: '', component: Dashboard },
      { path: 'bairros', component: Bairros },
      { path: 'profissionais', component: Profissionais },
    ],
  },
];
