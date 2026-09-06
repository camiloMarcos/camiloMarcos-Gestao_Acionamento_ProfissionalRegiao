// Modal reutilizável para confirmar ações importantes do sistema.
import { Component, input, output } from '@angular/core';

@Component({
  selector: 'app-confirm-modal',
  standalone: true,
  templateUrl: './confirm-modal.html',
  styleUrl: './confirm-modal.css',
})
export class ConfirmModalComponent {
  readonly titulo = input('Confirmar ação');
  readonly mensagem = input('Deseja continuar?');
  readonly textoConfirmar = input('Confirmar');
  readonly textoCancelar = input('Cancelar');
  readonly perigo = input(false);
  readonly confirmar = output<void>();
  readonly cancelar = output<void>();
}