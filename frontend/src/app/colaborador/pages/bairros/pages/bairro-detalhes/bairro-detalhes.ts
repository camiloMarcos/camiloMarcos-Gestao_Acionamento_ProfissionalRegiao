import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, forkJoin, of } from 'rxjs';
import { Bairro } from '../../../../../core/models/bairro.model';
import { Profissional } from '../../../../../core/models/profissional.model';
import { BairroService } from '../../../../../core/services/bairro.service';
import { ProfissionalService } from '../../../../../core/services/profissional.service';
import { ProfissionalBairroService } from '../../../../../core/services/profissional-bairro.service';
import { BairroProfissionaisComponent } from '../../components/bairro-profissionais/bairro-profissionais';
import { ConfirmModalComponent } from '../../../../../shared/components/confirm-modal/confirm-modal';

@Component({
  selector: 'app-bairro-detalhes',
  standalone: true,
  imports: [CommonModule, BairroProfissionaisComponent, ConfirmModalComponent],
  templateUrl: './bairro-detalhes.html',
  styleUrl: './bairro-detalhes.css',
})
export class BairroDetalhes implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly bairroService = inject(BairroService);
  private readonly profissionalService = inject(ProfissionalService);
  private readonly profissionalBairroService = inject(ProfissionalBairroService);

  readonly bairro = signal<Bairro | null>(null);
  readonly profissionais = signal<Profissional[]>([]);
  readonly carregando = signal(true);
  readonly erro = signal('');
  readonly erroProfissionais = signal('');

  // Controla mensagens, modal e estados do fluxo de associação.
  readonly mensagem = signal('');
  readonly tipoMensagem = signal<'sucesso' | 'erro' | 'info'>('info');
  readonly modalAssociacaoAberto = signal(false);
  readonly profissionaisDisponiveis = signal<Profissional[]>([]);
  readonly filtroNome = signal('');
  readonly filtroNumeroConselho = signal('');
  readonly profissionaisFiltrados = computed(() => {
    const nome = this.filtroNome().trim().toLocaleLowerCase();
    const numeroConselho = this.filtroNumeroConselho().trim().toLocaleLowerCase();

    return this.profissionaisDisponiveis().filter((profissional) => {
      const correspondeAoNome = !nome || profissional.nome.toLocaleLowerCase().includes(nome);
      const correspondeAoConselho = !numeroConselho ||
        profissional.numeroConselho.toLocaleLowerCase().includes(numeroConselho);

      return correspondeAoNome && correspondeAoConselho;
    });
  });
  readonly profissionalSelecionadoId = signal<number | null>(null);
  readonly carregandoDisponiveis = signal(false);
  readonly associando = signal(false);
  readonly profissionalParaRemover = signal<Profissional | null>(null);
  readonly removendo = signal(false);

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
        catchError(() => {
          this.erroProfissionais.set('Não foi possível carregar os profissionais vinculados.');
          return of([] as Profissional[]);
        })
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

  solicitarAssociacao(): void {
    const bairroId = this.bairro()?.id;

    if (!bairroId) {
      return;
    }

    this.limparMensagem();
    this.profissionalSelecionadoId.set(null);
    this.limparFiltrosAssociacao();
    this.modalAssociacaoAberto.set(true);
    this.carregandoDisponiveis.set(true);

    this.profissionalService.buscarTodos().subscribe({
      next: (profissionais) => {
        const vinculados = new Set(this.profissionais().map((profissional) => profissional.id));
        this.profissionaisDisponiveis.set(
          profissionais.filter((profissional) => !vinculados.has(profissional.id))
        );
        this.carregandoDisponiveis.set(false);
      },
      error: () => {
        this.carregandoDisponiveis.set(false);
        this.fecharModalAssociacao();
        this.mostrarMensagem('Não foi possível carregar os profissionais disponíveis.', 'erro');
      },
    });
  }

  selecionarProfissional(profissionalId: number): void {
    this.profissionalSelecionadoId.set(profissionalId);
  }

  fecharModalAssociacao(): void {
    this.modalAssociacaoAberto.set(false);
    this.profissionaisDisponiveis.set([]);
    this.profissionalSelecionadoId.set(null);
    this.limparFiltrosAssociacao();
    this.carregandoDisponiveis.set(false);
  }

  atualizarFiltroNome(valor: string): void {
    this.filtroNome.set(valor);
    this.profissionalSelecionadoId.set(null);
  }

  atualizarFiltroNumeroConselho(valor: string): void {
    this.filtroNumeroConselho.set(valor);
    this.profissionalSelecionadoId.set(null);
  }

  confirmarAssociacao(): void {
    const profissionalId = this.profissionalSelecionadoId();
    const bairroId = this.bairro()?.id;

    if (!profissionalId || !bairroId || this.associando()) {
      return;
    }

    this.associando.set(true);
    this.profissionalBairroService.associarProfissionalAoBairro(profissionalId, bairroId).subscribe({
      next: () => {
        this.fecharModalAssociacao();
        this.mostrarMensagem('Profissional associado com sucesso.', 'sucesso');
        this.carregarProfissionais(bairroId);
      },
      error: (error: { error?: { detalhe?: string; message?: string } }) => {
        this.associando.set(false);
        this.mostrarMensagem(
          error.error?.detalhe || error.error?.message || 'Não foi possível associar o profissional.',
          'erro'
        );
      },
    });
  }

  solicitarRemocao(profissionalId: number): void {
    const profissional = this.profissionais().find((item) => item.id === profissionalId);

    if (profissional) {
      this.profissionalParaRemover.set(profissional);
    }
  }

  cancelarRemocao(): void {
    this.profissionalParaRemover.set(null);
  }

  confirmarRemocao(): void {
    const profissional = this.profissionalParaRemover();
    const bairroId = this.bairro()?.id;

    if (!profissional || !bairroId || this.removendo()) {
      return;
    }

    this.removendo.set(true);
    this.profissionalBairroService.desassociarProfissionalDoBairro(profissional.id, bairroId).subscribe({
      next: () => {
        this.profissionalParaRemover.set(null);
        this.mostrarMensagem('Vínculo removido com sucesso.', 'sucesso');
        this.carregarProfissionais(bairroId);
      },
      error: (error: { error?: { detalhe?: string; message?: string } }) => {
        this.removendo.set(false);
        this.mostrarMensagem(
          error.error?.detalhe || error.error?.message || 'Não foi possível remover o vínculo.',
          'erro'
        );
      },
    });
  }

  private carregarProfissionais(bairroId: number): void {
    this.profissionalService.buscarPorBairroId(bairroId).subscribe({
      next: (profissionais) => {
        this.profissionais.set(profissionais);
        this.erroProfissionais.set('');
        this.associando.set(false);
        this.removendo.set(false);
      },
      error: () => {
        this.associando.set(false);
        this.removendo.set(false);
        this.erroProfissionais.set('Não foi possível atualizar os profissionais vinculados.');
      },
    });
  }

  private mostrarMensagem(mensagem: string, tipo: 'sucesso' | 'erro' | 'info'): void {
    this.mensagem.set(mensagem);
    this.tipoMensagem.set(tipo);
    setTimeout(() => this.mensagem.set(''), 3500);
  }

  private limparMensagem(): void {
    this.mensagem.set('');
  }

  private limparFiltrosAssociacao(): void {
    this.filtroNome.set('');
    this.filtroNumeroConselho.set('');
  }
}
