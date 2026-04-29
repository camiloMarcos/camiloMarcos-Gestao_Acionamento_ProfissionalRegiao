import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Bairro } from '../../../../../core/models/bairro.model';

@Component({
  selector: 'app-bairros-table',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './bairros-table.html',
  styleUrl: './bairros-table.css'
})
export class BairrosTableComponent {
  /**
   * Lista de bairros a serem exibidos na tabela.
   * Recebida do componente pai (BairrosPage).
   */
  @Input() resultados: Bairro[] = [];

  /**
   * Emissor de evento para quando o usuário deseja editar um bairro.
   */
  @Output() editar = new EventEmitter<Bairro>();

  /**
   * Emissor de evento para quando o usuário deseja excluir um bairro.
   */
  @Output() excluir = new EventEmitter<Bairro>();

  /**
   * Emissor de evento para quando o usuário deseja visualizar detalhes de um bairro.
   */
  @Output() visualizar = new EventEmitter<Bairro>();

  /**
   * Notifica o componente pai para visualizar os detalhes do bairro selecionado.
   */
  onVisualizar(bairro: Bairro): void {
    this.visualizar.emit(bairro);
  }

  /**
   * Notifica o componente pai para iniciar a edição do bairro selecionado.
   */
  onEditar(bairro: Bairro): void {
    this.editar.emit(bairro);
  }

  /**
   * Notifica o componente pai para iniciar a exclusão do bairro selecionado.
   */
  onExcluir(bairro: Bairro): void {
    this.excluir.emit(bairro);
  }
}
