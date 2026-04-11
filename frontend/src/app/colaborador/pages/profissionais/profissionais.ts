import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ProfissionalService } from '../../../core/services/profissional.service';
import { BairroService } from '../../../core/services/bairro.service';
import { Profissional } from '../../../core/models/profissional.model';

type Tela = 'inicio' | 'busca' | 'resultados' | 'formulario' | 'deletar' | 'confirmar-exclusao';
type TipoBusca = 'todos' | 'id' | 'nome' | 'bairro';

@Component({
  selector: 'app-profissionais',
  imports: [FormsModule],
  templateUrl: './profissionais.html',
  styleUrl: './profissionais.css',
})
export class Profissionais {
  private profService = inject(ProfissionalService);
  private bairroService = inject(BairroService);

  // Estado da tela
  telaAtual: Tela = 'inicio';
  tipoBusca: TipoBusca = 'todos';
  editando = false;

  // Busca
  termoBusca = '';
  resultados: Profissional[] = [];

  // Formulário
  profissional: Profissional = this.novoProfissional();

  // Deletar
  profParaDeletar: Profissional | null = null;

  // Feedback
  mensagem = '';
  tipoMensagem: 'sucesso' | 'erro' | 'info' = 'info';
  carregando = false;

  // ========================
  // NAVEGAÇÃO ENTRE TELAS
  // ========================

  irParaBusca() {
    this.limparMensagem();
    this.resultados = [];
    this.termoBusca = '';
    this.tipoBusca = 'todos';
    this.telaAtual = 'busca';
  }

  irParaCriar() {
    this.limparMensagem();
    this.editando = false;
    this.profissional = this.novoProfissional();
    this.telaAtual = 'formulario';
  }

  irParaDeletar() {
    this.limparMensagem();
    this.resultados = [];
    this.termoBusca = '';
    this.tipoBusca = 'todos';
    this.profParaDeletar = null;
    this.telaAtual = 'deletar';
  }

  voltar() {
    this.limparMensagem();
    if (this.telaAtual === 'resultados') {
      this.telaAtual = 'busca';
    } else if (this.telaAtual === 'formulario' && this.editando) {
      this.telaAtual = 'resultados';
    } else if (this.telaAtual === 'confirmar-exclusao') {
      this.telaAtual = 'deletar';
    } else {
      this.telaAtual = 'inicio';
    }
  }

  voltarInicio() {
    this.limparMensagem();
    this.telaAtual = 'inicio';
  }

  // ========================
  // BUSCA
  // ========================

  executarBusca() {
    this.limparMensagem();
    this.carregando = true;

    switch (this.tipoBusca) {
      case 'todos':
        this.profService.buscarTodos().subscribe({
          next: (data) => this.tratarResultados(data),
          error: (err) => this.tratarErro(err),
        });
        break;

      case 'id': {
        const id = parseInt(this.termoBusca, 10);
        if (!id || id <= 0) {
          this.mostrarMensagem('Digite um ID válido (número maior que zero).', 'erro');
          this.carregando = false;
          return;
        }
        this.profService.buscarPorId(id).subscribe({
          next: (data) => this.tratarResultados([data]),
          error: (err) => this.tratarErro(err),
        });
        break;
      }

      case 'nome':
        if (!this.termoBusca.trim()) {
          this.mostrarMensagem('Digite um nome para buscar.', 'erro');
          this.carregando = false;
          return;
        }
        this.profService.buscarPorNome(this.termoBusca).subscribe({
          next: (data) => this.tratarResultados(data),
          error: (err) => this.tratarErro(err),
        });
        break;

      case 'bairro':
        if (!this.termoBusca.trim()) {
          this.mostrarMensagem('Digite o nome do bairro para buscar.', 'erro');
          this.carregando = false;
          return;
        }
        this.bairroService.buscarProfissionaisPorBairroNome(this.termoBusca).subscribe({
          next: (data) => this.tratarResultados(data),
          error: (err) => this.tratarErro(err),
        });
        break;
    }
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
      this.profService.buscarPorId(id).subscribe({
        next: (data) => this.tratarResultados([data]),
        error: (err) => this.tratarErro(err),
      });
    } else if (this.tipoBusca === 'nome') {
      if (!this.termoBusca.trim()) {
        this.mostrarMensagem('Digite um nome para buscar.', 'erro');
        this.carregando = false;
        return;
      }
      this.profService.buscarPorNome(this.termoBusca).subscribe({
        next: (data) => this.tratarResultados(data),
        error: (err) => this.tratarErro(err),
      });
    } else {
      this.profService.buscarTodos().subscribe({
        next: (data) => this.tratarResultados(data),
        error: (err) => this.tratarErro(err),
      });
    }
  }

  private tratarResultados(data: Profissional[]) {
    this.resultados = data;
    this.carregando = false;
    if (data.length === 0) {
      this.mostrarMensagem('Nenhum profissional encontrado.', 'info');
    }
    if (this.telaAtual === 'busca') {
      this.telaAtual = 'resultados';
    }
  }

  private tratarErro(err: unknown) {
    this.carregando = false;
    const error = err as { error?: { message?: string }; status?: number };
    if (error.status === 404) {
      this.mostrarMensagem('Nenhum profissional encontrado.', 'info');
      this.resultados = [];
      if (this.telaAtual === 'busca') {
        this.telaAtual = 'resultados';
      }
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

  editarProfissional(prof: Profissional) {
    this.limparMensagem();
    this.editando = true;
    this.profissional = { ...prof };
    this.telaAtual = 'formulario';
  }

  salvarProfissional() {
    this.limparMensagem();
    this.carregando = true;

    if (this.editando && this.profissional.id) {
      this.profService.atualizar(this.profissional.id, this.profissional).subscribe({
        next: () => {
          this.carregando = false;
          this.mostrarMensagem('Profissional atualizado com sucesso!', 'sucesso');
          this.telaAtual = 'inicio';
        },
        error: (err) => {
          this.carregando = false;
          const error = err as { error?: { message?: string } };
          this.mostrarMensagem(
            error.error?.message || 'Erro ao atualizar profissional.',
            'erro'
          );
        },
      });
    } else {
      this.profService.salvar(this.profissional).subscribe({
        next: () => {
          this.carregando = false;
          this.mostrarMensagem('Profissional cadastrado com sucesso!', 'sucesso');
          this.telaAtual = 'inicio';
        },
        error: (err) => {
          this.carregando = false;
          const error = err as { error?: { message?: string } };
          this.mostrarMensagem(
            error.error?.message || 'Erro ao cadastrar profissional.',
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

  selecionarParaDeletar(prof: Profissional) {
    this.profParaDeletar = { ...prof };
    this.telaAtual = 'confirmar-exclusao';
  }

  confirmarExclusao() {
    if (!this.profParaDeletar) return;
    this.carregando = true;

    this.profService.deletar(this.profParaDeletar.id).subscribe({
      next: () => {
        this.carregando = false;
        this.mostrarMensagem(
          `Profissional "${this.profParaDeletar!.nome}" excluído permanentemente.`,
          'sucesso'
        );
        this.profParaDeletar = null;
        this.telaAtual = 'inicio';
      },
      error: (err) => {
        this.carregando = false;
        const error = err as { error?: { message?: string } };
        this.mostrarMensagem(
          error.error?.message || 'Erro ao excluir profissional.',
          'erro'
        );
      },
    });
  }

  congelarProfissional() {
    this.mostrarMensagem(
      `Funcionalidade "Congelar" será implementada em breve. O profissional "${this.profParaDeletar?.nome}" permanece ativo.`,
      'info'
    );
    this.profParaDeletar = null;
    this.telaAtual = 'inicio';
  }

  deletarDoFormulario() {
    if (!this.profissional.id) return;
    this.profParaDeletar = { ...this.profissional };
    this.telaAtual = 'confirmar-exclusao';
  }

  // ========================
  // HELPERS
  // ========================

  private novoProfissional(): Profissional {
    return {
      id: 0,
      nome: '',
      especialidade: '',
      numeroConselho: '',
      telefone: '',
      email: '',
      endereco: '',
      cidade: '',
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
