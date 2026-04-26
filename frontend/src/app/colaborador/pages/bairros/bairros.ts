import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BairroService } from '../../../core/services/bairro.service';
import { Bairro } from '../../../core/models/bairro.model';

type Tela = 'inicio' | 'busca' | 'resultados' | 'formulario' | 'deletar' | 'confirmar-exclusao';
type TipoBuscaBairro = 'todos' | 'id' | 'nome-exato' | 'nome-parcial' | 'cidade' | 'risco' | 'profissional';

@Component({
  selector: 'app-bairros',
  imports: [FormsModule],
  templateUrl: './bairros.html',
  styleUrl: './bairros.css',
})
export class Bairros {
  private bairroService = inject(BairroService);

  // ========================
  // [S] ESTADOS REATIVOS (SIGNALS)
  // ========================
  
  // Controle da tela atual
  telaAtual = signal<Tela>('inicio');
  
  // Lista de resultados da busca
  resultados = signal<Bairro[]>([]);
  
  // Status de carregamento (Spinner)
  carregando = signal<boolean>(false);
  
  // Mensagens de feedback
  mensagem = signal<string>('');
  tipoMensagem = signal<'sucesso' | 'erro' | 'info'>('info');

  // ========================
  // VARIÁVEIS DE CONTROLE
  // ========================

  tipoBusca: TipoBuscaBairro = 'todos';
  editando = false;
  
  // Objeto de filtros individualizados para evitar replicação de dados
  filtros = {
    id: '',
    nome: '',
    cidade: '',
    risco: '',
    profissionalId: '',
    profissionalNome: ''
  };

  termoBusca = ''; // Mantido temporariamente para compatibilidade
  bairro: Bairro = this.novoBairro();
  bairroParaDeletar: Bairro | null = null;

  // ========================
  // MATRIZ DE NAVEGAÇÃO
  // ========================

  voltar() {
    this.limparMensagem();
    if (this.telaAtual() === 'resultados') {
      this.resultados.set([]);
      this.termoBusca = '';
      this.telaAtual.set('inicio');
    } else if (this.telaAtual() === 'formulario' && this.editando) {
      this.voltarInicio();
    } else if (this.telaAtual() === 'confirmar-exclusao') {
      this.telaAtual.set('inicio');
    } else {
      this.telaAtual.set('inicio');
    }
  }

  resultsParaInicio() {
    this.voltarInicio();
  }

  voltarInicio() {
    this.limparMensagem();
    this.resultados.set([]);
    this.termoBusca = '';
    this.filtros = {
      id: '',
      nome: '',
      cidade: '',
      risco: '',
      profissionalId: '',
      profissionalNome: ''
    };
    this.tipoBusca = 'todos';
    this.telaAtual.set('inicio');
  }

  irParaCriar() {
    this.limparMensagem();
    this.editando = false;
    this.bairro = this.novoBairro();
    this.telaAtual.set('formulario');
  }

  irParaDeletar() {
    this.limparMensagem();
    this.resultados.set([]);
    this.termoBusca = '';
    this.tipoBusca = 'todos';
    this.bairroParaDeletar = null;
    this.telaAtual.set('deletar');
  }

  // ========================
  // LÓGICA DE BUSCA (DINÂMICA)
  // ========================

  executarBusca() {
    this.limparMensagem();
    this.carregando.set(true);

    // Na busca dinâmica, enviamos o objeto de filtros completo.
    // O backend tratará o que foi preenchido.
    this.bairroService.buscarDinamico(this.filtros).subscribe({
      next: (data: Bairro[]) => {
        this.resultados.set(data);
        this.telaAtual.set('resultados');
        this.carregando.set(false);
      },
      error: (err: any) => {
        this.carregando.set(false);
        const error = err as { error?: { message?: string }; status?: number };
        if (error.status === 404) {
          this.mostrarMensagem('Nenhum bairro encontrado com os filtros informados.', 'info');
          this.resultados.set([]);
          this.telaAtual.set('resultados');
        } else {
          this.mostrarMensagem(
            error.error?.message || 'Erro ao se comunicar com o servidor.',
            'erro'
          );
        }
      },
    });
  }

  executarBuscaDeletar() {
    this.limparMensagem();
    this.carregando.set(true);

    if (this.tipoBusca === 'id') {
      const id = parseInt(this.termoBusca, 10);
      if (!id || id <= 0) {
        this.mostrarMensagem('Digite um ID válido.', 'erro');
        this.carregando.set(false);
        return;
      }
      this.bairroService.buscarPorId(id).subscribe({
        next: (data: any) => this.tratarResultados([data]),
        error: (err: any) => this.tratarErro(err),
      });
    } else if (this.tipoBusca === 'nome-exato') {
      this.bairroService.buscarPorNomeExato(this.termoBusca).subscribe({
        next: (data: any) => this.tratarResultados([data]),
        error: (err: any) => this.tratarErro(err),
      });
    } else if (this.tipoBusca === 'nome-parcial') {
      this.bairroService.buscarPorNomeParcial(this.termoBusca).subscribe({
        next: (data: any) => this.tratarResultados(data),
        error: (err: any) => this.tratarErro(err),
      });
    } else if (this.tipoBusca === 'cidade') {
      this.bairroService.buscarPorCidade(this.termoBusca).subscribe({
        next: (data: any) => this.tratarResultados(data),
        error: (err: any) => this.tratarErro(err),
      });
    } else if (this.tipoBusca === 'risco') {
      this.bairroService.buscarPorRisco(this.termoBusca === 'perigoso').subscribe({
        next: (data: any) => this.tratarResultados(data),
        error: (err: any) => this.tratarErro(err),
      });
    } else if (this.tipoBusca === 'profissional') {
      // Método legado de busca simples por Profissional ID
      const profId = parseInt(this.termoBusca, 10);
      if (!profId || profId <= 0) {
        this.mostrarMensagem('Digite um ID de profissional válido.', 'erro');
        this.carregando.set(false);
        return;
      }
      // Aqui usamos os novos campos no buscarDinamico para manter a consistência
      this.bairroService.buscarDinamico({ profissionalId: this.termoBusca }).subscribe({
        next: (data: any) => this.tratarResultados(data),
        error: (err: any) => this.tratarErro(err),
      });
    } else {
      this.bairroService.buscarTodos().subscribe({
        next: (data: any) => this.tratarResultados(data),
        error: (err: any) => this.tratarErro(err),
      });
    }
  }

  private tratarResultados(data: Bairro[]) {
    this.resultados.set(data);
    this.carregando.set(false);
  }

  private tratarErro(err: unknown) {
    this.carregando.set(false);
    const error = err as { error?: { message?: string }; status?: number };
    if (error.status === 404) {
      this.mostrarMensagem('Nenhum bairro encontrado.', 'info');
      this.resultados.set([]);
    } else {
      this.mostrarMensagem(
        error.error?.message || 'Erro ao se comunicar com o servidor.',
        'erro'
      );
    }
  }

  // ========================
  // CRIAR / EDITAR
  // ========================

  editarBairro(bairroSelecionado: Bairro) {
    this.limparMensagem();
    this.editando = true;
    this.bairro = { ...bairroSelecionado };
    this.telaAtual.set('formulario');
  }

  salvarBairro() {
    this.limparMensagem();

    if (!this.bairro.nome || !this.bairro.nome.trim()) {
      this.mostrarMensagem('Nome do bairro é obrigatório.', 'erro');
      return;
    }

    if (!this.bairro.cidade || !this.bairro.cidade.trim()) {
      this.mostrarMensagem('Cidade é obrigatória.', 'erro');
      return;
    }

    this.carregando.set(true);

    if (this.editando && this.bairro.id) {
      this.bairroService.atualizar(this.bairro.id, this.bairro).subscribe({
        next: () => {
          this.carregando.set(false);
          this.mostrarMensagem('Bairro atualizado com sucesso!', 'sucesso');
          this.telaAtual.set('inicio');
        },
        error: (err: any) => {
          this.carregando.set(false);
          const error = err as { error?: { message?: string } };
          this.mostrarMensagem(
            error.error?.message || 'Erro ao atualizar bairro.',
            'erro'
          );
        },
      });
    } else {
      this.bairroService.criar(this.bairro).subscribe({
        next: () => {
          this.carregando.set(false);
          this.mostrarMensagem('Bairro cadastrado com sucesso!', 'sucesso');
          this.telaAtual.set('inicio');
        },
        error: (err: any) => {
          this.carregando.set(false);
          const error = err as { error?: { message?: string } };
          this.mostrarMensagem(
            error.error?.message || 'Erro ao cadastrar bairro.',
            'erro'
          );
        },
      });
    }
  }

  descartarAlteracoes() {
    this.limparMensagem();
    if (this.editando) {
      this.telaAtual.set('resultados');
    } else {
      this.telaAtual.set('inicio');
    }
  }

  // ========================
  // DELETAR / CONGELAR
  // ========================

  selecionarParaDeletar(bairroSelecionado: Bairro) {
    this.bairroParaDeletar = { ...bairroSelecionado };
    this.telaAtual.set('confirmar-exclusao');
  }

  confirmarExclusao() {
    if (!this.bairroParaDeletar) return;
    this.carregando.set(true);

    this.bairroService.deletar(this.bairroParaDeletar.id).subscribe({
      next: () => {
        this.carregando.set(false);
        this.mostrarMensagem(
          `Bairro "${this.bairroParaDeletar!.nome}" excluído permanentemente.`,
          'sucesso'
        );
        this.bairroParaDeletar = null;
        this.telaAtual.set('inicio');
      },
      error: (err: any) => {
        this.carregando.set(false);
        const error = err as { error?: { message?: string } };
        this.mostrarMensagem(
          error.error?.message || 'Erro ao excluir bairro.',
          'erro'
        );
      },
    });
  }

  congelarBairro() {
    this.mostrarMensagem(
      `Funcionalidade "Congelar" será implementada em breve. O bairro "${this.bairroParaDeletar?.nome}" permanece ativo.`,
      'info'
    );
    this.bairroParaDeletar = null;
    this.telaAtual.set('inicio');
  }

  deletarDoFormulario() {
    if (!this.bairro.id) return;
    this.bairroParaDeletar = { ...this.bairro };
    this.telaAtual.set('confirmar-exclusao');
  }

  // ========================
  // HELPERS
  // ========================

  private novoBairro(): Bairro {
    return {
      id: 0,
      nome: '',
      cidade: '',
      perigoDistante: false,
    };
  }

  private mostrarMensagem(texto: string, tipo: 'sucesso' | 'erro' | 'info') {
    this.mensagem.set(texto);
    this.tipoMensagem.set(tipo);
  }

  limparMensagem() {
    this.mensagem.set('');
  }
}
