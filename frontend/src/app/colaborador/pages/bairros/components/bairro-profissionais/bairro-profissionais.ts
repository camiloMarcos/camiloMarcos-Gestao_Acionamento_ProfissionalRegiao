// Componente que coordena a lista de profissionais vinculados ao bairro.
import { Component, input, output } from '@angular/core';
import { Profissional } from '../../../../../core/models/profissional.model';

@Component({
  selector: 'app-bairro-profissionais',
  standalone: true,
  templateUrl: './bairro-profissionais.html',
  styleUrl: './bairro-profissionais.css',
})
export class BairroProfissionaisComponent {
  readonly profissionais = input<Profissional[]>([]);
  readonly associar = output<void>();
  readonly remover = output<number>();
}