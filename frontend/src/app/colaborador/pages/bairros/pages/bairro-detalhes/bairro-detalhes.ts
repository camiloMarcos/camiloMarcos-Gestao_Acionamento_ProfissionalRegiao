import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, forkJoin, of } from 'rxjs';
import { Bairro } from '../../../../../core/models/bairro.model';
import { Profissional } from '../../../../../core/models/profissional.model';
import { BairroService } from '../../../../../core/services/bairro.service';
import { ProfissionalService } from '../../../../../core/services/profissional.service';

@Component({
  selector: 'app-bairro-detalhes',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './bairro-detalhes.html',
  styleUrl: './bairro-detalhes.css',
})
export class BairroDetalhes implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly bairroService = inject(BairroService);
  private readonly profissionalService = inject(ProfissionalService);

  readonly bairro = signal<Bairro | null>(null);
  readonly profissionais = signal<Profissional[]>([]);
  readonly carregando = signal(true);
  readonly erro = signal('');

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    if (!id || id <= 0) {
      this.erro.set('Bairro inválido.');
      this.carregando.set(false);
      return;
    }

    forkJoin({
      bairro: this.bairroService.buscarPorId(id),
      profissionais: this.profissionalService.buscarPorBairroId(id).pipe(
        catchError(() => of([] as Profissional[]))
      ),
    }).subscribe({
      next: ({ bairro, profissionais }) => {
        this.bairro.set(bairro);
        this.profissionais.set(profissionais);
        this.carregando.set(false);
      },
      error: () => {
        this.erro.set('Não foi possível carregar os detalhes deste bairro.');
        this.carregando.set(false);
      },
    });
  }

  voltar(): void {
    this.router.navigate(['/bairros']);
  }

  editarBairro(): void {
    const id = this.bairro()?.id;

    if (id) {
      this.router.navigate(['/bairros'], { queryParams: { edit: id } });
    }
  }
}
