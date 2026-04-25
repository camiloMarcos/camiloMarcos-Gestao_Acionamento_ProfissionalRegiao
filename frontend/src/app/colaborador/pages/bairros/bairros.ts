import { Component, inject } from '@angular/core';
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

  // Estado da tela
  telaAtual: Tela = 'inicio';
  tipoBusca: TipoBuscaBairro = 'todos';
  editando = false;

  // Busca
  termoBusca = '';
  resultados: Bairro[] = [];

  // Formulário
  bairro: Bairro = this.novoBairro();

  // Deletar
  bairroParaDeletar: Bairro | null = null;

  // Feedback
  mensagem = '';
  tipoMensagem: 'sucesso' | 'erro' | 'info' = 'info';
  carregando = false;

  // ========================
  // NAVEGAÇÃO ENTRE TELAS
  // ========================

  voltar() {
    this.limparMensagem();
    if (this.telaAtual === 'resultados') {
      this.resultados = [];
      this.termoBusca = '';
      this.telaAtual = 'inicio';
    } else if (this.telaAtual === 'formulario' && this.editando) {
      this.voltarInicio();
    } else if (this.telaAtual === 'confirmar-exclusao') {
      this.telaAtual = 'inicio';
    } else {
      this.telaAtual = 'inicio';
    }
  }

  resultsParaInicio() {
    this.voltarInicio();
  }

  voltarInicio() {
    this.limparMensagem();
    this.resultados = [];
    this.termoBusca = '';
    this.tipoBusca = 'todos';
    this.telaAtual = 'inicio';
  }

  irParaCriar() {
    this.limparMensagem();
    this.editando = false;
    this.bairro = this.novoBairro();
    this.telaAtual = 'formulario';
  }

  irParaDeletar() {
    this.limparMensagem();
    this.resultados = [];
    this.termoBusca = '';
    this.tipoBusca = 'todos';
    this.bairroParaDeletar = null;
    this.telaAtual = 'deletar';
  }

  // ========================
  // BUSCA COM FILTROS DINÂMICOS
  // ========================

  executarBusca() {
    this.limparMensagem();

    // Validações
    if (this.tipoBusca !== 'todos' && !this.termoBusca.trim()) {
      this.mostrarMensagem('Digite algo para buscar.', 'erro');
      return;
    }

    if (this.tipoBusca === 'id') {
      const id = parseInt(this.termoBusca, 10);
      if (!id || id <= 0) {
        this.mostrarMensagem('Digite um ID válido (número maior que zero).', 'erro');
        return;
      }
    }

    this.carregando = true;

    // Map de operações baseado no tipo de busca
    const operacoes: Record<TipoBuscaBairro, () => any> = {
      'todos': () => this.bairroService.buscarTodos(),
      'id': () => this.bairroService.buscarPorId(parseInt(this.termoBusca, 10)),
      'nome-exato': () => this.bairroService.buscarPorNomeExato(this.termoBusca),
      'nome-parcial': () => this.bairroService.buscarPorNomeParcial(this.termoBusca),
      'cidade': () => this.bairroService.buscarPorCidade(this.termoBusca),
      'risco': () => this.bairroService.buscarPorRisco(this.termoBusca === 'perigoso'),
      'profissional': () => this.bairroService.buscarPorProfissional(parseInt(this.termoBusca, 10)),
    };

    operacoes[this.tipoBusca]().subscribe({
      next: (data: any) => {
        this.resultados = Array.isArray(data) ? data : [data];
        this.telaAtual = 'resultados';
        this.carregando = false;
      },
      error: (err: any) => {
        this.carregando = false;
        const error = err as { error?: { message?: string }; status?: number };
        if (error.status === 404) {
          this.mostrarMensagem('Nenhum bairro encontrado com os critérios informados.', 'info');
          this.resultados = [];
          this.telaAtual = 'resultados';
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
    this.carregando = true;

    if (this.tipoBusca === 'id') {
      const id = parseInt(this.termoBusca, 10);
      if (!id || id <= 0) {
        this.mostrarMensagem('Digite um ID válido.', 'erro');
        this.carregando = false;
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
      this.bairroService.buscarPorProfissional(parseInt(this.termoBusca, 10)).subscribe({
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
    this.resultados = data;
    this.carregando = false;
  }

  private tratarErro(err: unknown) {
    this.carregando = false;
    const error = err as { error?: { message?: string }; status?: number };
    if (error.status === 404) {
      this.mostrarMensagem('Nenhum bairro encontrado.', 'info');
      this.resultados = [];
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
    this.telaAtual = 'formulario';
  }

  salvarBairro() {
    this.limparMensagem();

    // Validação básica
    if (!this.bairro.nome || !this.bairro.nome.trim()) {
      this.mostrarMensagem('Nome do bairro é obrigatório.', 'erro');
      return;
    }

    if (!this.bairro.cidade || !this.bairro.cidade.trim()) {
      this.mostrarMensagem('Cidade é obrigatória.', 'erro');
      return;
    }

    this.carregando = true;

    if (this.editando && this.bairro.id) {
      this.bairroService.atualizar(this.bairro.id, this.bairro).subscribe({
        next: () => {
          this.carregando = false;
          this.mostrarMensagem('Bairro atualizado com sucesso!', 'sucesso');
          this.telaAtual = 'inicio';
        },
        error: (err: any) => {
          this.carregando = false;
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
          this.carregando = false;
          this.mostrarMensagem('Bairro cadastrado com sucesso!', 'sucesso');
          this.telaAtual = 'inicio';
        },
        error: (err: any) => {
          this.carregando = false;
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
      this.telaAtual = 'resultados';
    } else {
      this.telaAtual = 'inicio';
    }
  }

  // ========================
  // DELETAR / CONGELAR
  // ========================

  selecionarParaDeletar(bairroSelecionado: Bairro) {
    this.bairroParaDeletar = { ...bairroSelecionado };
    this.telaAtual = 'confirmar-exclusao';
  }

  confirmarExclusao() {
    if (!this.bairroParaDeletar) return;
    this.carregando = true;

    this.bairroService.deletar(this.bairroParaDeletar.id).subscribe({
      next: () => {
        this.carregando = false;
        this.mostrarMensagem(
          `Bairro "${this.bairroParaDeletar!.nome}" excluído permanentemente.`,
          'sucesso'
        );
        this.bairroParaDeletar = null;
        this.telaAtual = 'inicio';
      },
      error: (err: any) => {
        this.carregando = false;
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
    this.telaAtual = 'inicio';
  }

  deletarDoFormulario() {
    if (!this.bairro.id) return;
    this.bairroParaDeletar = { ...this.bairro };
    this.telaAtual = 'confirmar-exclusao';
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
    this.mensagem = texto;
    this.tipoMensagem = tipo;
  }

  limparMensagem() {
    this.mensagem = '';
  }
}
