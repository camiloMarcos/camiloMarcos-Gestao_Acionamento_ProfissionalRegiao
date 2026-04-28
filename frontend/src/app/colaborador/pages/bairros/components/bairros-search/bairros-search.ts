import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-bairros-search',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './bairros-search.html',
  styleUrl: './bairros-search.css'
})
export class BairrosSearchComponent {
  /**
   * Estado de carregamento para desativar o botão durante a busca.
   */
  @Input() carregando = false;

  /**
   * Indica se a tela atual é a de resultados para exibir o botão "Limpar".
   */
  @Input() telaResultados = false;

  /**
   * Filtros de busca (two-way binding via componente pai ou inicialização local).
   */
  @Input() filtros = {
    id: '',
    nome: '',
    cidade: '',
    risco: '',
    profissionalId: '',
    profissionalNome: ''
  };

  /**
   * Tipo de busca selecionado (para controle visual dos chips).
   */
  @Input() tipoBusca: string = 'todos';

  /**
   * Emissor para disparar a execução da busca no componente pai.
   */
  @Output() buscar = new EventEmitter<void>();

  /**
   * Emissor para limpar os filtros e voltar ao estado inicial.
   */
  @Output() limpar = new EventEmitter<void>();

  /**
   * Emissor para navegar para a tela de criação.
   */
  @Output() criar = new EventEmitter<void>();

  /**
   * Emissor para navegar para a tela de deleção/congelamento.
   */
  @Output() deletar = new EventEmitter<void>();

  /**
   * Atualiza o tipo de busca quando um campo ganha foco.
   */
  setTipoBusca(tipo: string): void {
    this.tipoBusca = tipo;
  }

  /**
   * Dispara a ação de busca.
   */
  onExecutarBusca(): void {
    this.buscar.emit();
  }

  /**
   * Dispara a ação de limpar filtros.
   */
  onLimpar(): void {
    this.limpar.emit();
  }

  /**
   * Dispara a ação de ir para criação.
   */
  onIrParaCriar(): void {
    this.criar.emit();
  }

  /**
   * Dispara a ação de ir para deleção.
   */
  onIrParaDeletar(): void {
    this.deletar.emit();
  }
}
