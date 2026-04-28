import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Bairro } from '../../../../../core/models/bairro.model';

@Component({
  selector: 'app-bairros-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './bairros-form.html',
  styleUrl: './bairros-form.css'
})
export class BairrosFormComponent {
  /**
   * Dados do bairro sendo criado ou editado.
   * Recebido do componente pai.
   */
  @Input() bairro: Bairro = { nome: '', cidade: '', perigoDistante: false };

  /**
   * Define se o formulário está em modo de edição ou criação.
   */
  @Input() editando = false;

  /**
   * Estado de carregamento para controle de botões e spinners.
   */
  @Input() carregando = false;

  /**
   * Emite um evento para salvar os dados (Create ou Update).
   */
  @Output() salvar = new EventEmitter<void>();

  /**
   * Emite um evento para cancelar a operação e descartar mudanças.
   */
  @Output() cancelar = new EventEmitter<void>();

  /**
   * Emite um evento para deletar o registro atual (usado no modo edição).
   */
  @Output() deletar = new EventEmitter<void>();

  /**
   * Notifica o pai para salvar os dados.
   */
  onSalvar(): void {
    this.salvar.emit();
  }

  /**
   * Notifica o pai para cancelar e voltar.
   */
  onCancelar(): void {
    this.cancelar.emit();
  }

  /**
   * Notifica o pai para abrir a exclusão do registro.
   */
  onDeletar(): void {
    this.deletar.emit();
  }
}
