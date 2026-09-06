import { Routes } from '@angular/router';
import { Layout } from './layout/layout';
import { Dashboard } from './pages/dashboard/dashboard';
import { Bairros } from './pages/bairros/bairros';
import { Profissionais } from './pages/profissionais/profissionais';
import { BairroDetalhes } from './pages/bairros/pages/bairro-detalhes/bairro-detalhes';

export const colaboradorRoutes: Routes = [
  {
    path: '',
    component: Layout,
    children: [
      { path: '', component: Dashboard },
      { path: 'bairros', component: Bairros },
      { path: 'bairros/:id', component: BairroDetalhes },
      { path: 'profissionais', component: Profissionais },
    ],
  },
];
