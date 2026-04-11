
import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadChildren: () =>
      import('./colaborador/colaborador.routes').then((m) => m.colaboradorRoutes),
  },
];