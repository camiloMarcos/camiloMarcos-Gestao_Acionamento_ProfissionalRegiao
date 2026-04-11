import { Component, OnInit, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { Profissional } from '../../../core/models/profissional.model';
import { Bairro } from '../../../core/models/bairro.model';

@Component({
  selector: 'app-dashboard',
  imports: [RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard implements OnInit {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080';

  totalProfissionais = 0;
  totalBairros = 0;
  bairrosSemProfissional = 0;
  bairrosPerigosos = 0;

  profissionais: Profissional[] = [];
  bairros: Bairro[] = [];
  especialidades: { nome: string; quantidade: number }[] = [];

  saudacao = '';
  dataAtual = '';

  ngOnInit() {
    this.definirSaudacao();
    this.carregarDados();
  }

  private definirSaudacao() {
    const hora = new Date().getHours();
    if (hora < 12) this.saudacao = 'Bom dia';
    else if (hora < 18) this.saudacao = 'Boa tarde';
    else this.saudacao = 'Boa noite';

    this.dataAtual = new Date().toLocaleDateString('pt-BR', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  }

  private carregarDados() {
    this.http.get<Profissional[]>(`${this.apiUrl}/profissional`).subscribe({
      next: (data) => {
        this.profissionais = data;
        this.totalProfissionais = data.length;
        this.calcularEspecialidades(data);
      },
      error: () => {
        this.profissionais = [];
        this.totalProfissionais = 0;
      }
    });

    this.http.get<Bairro[]>(`${this.apiUrl}/bairro`).subscribe({
      next: (data) => {
        this.bairros = data;
        this.totalBairros = data.length;
        this.bairrosSemProfissional = data.filter(
          (b) => !b.profissionais || b.profissionais.length === 0
        ).length;
        this.bairrosPerigosos = data.filter((b) => b.perigoDistante).length;
      },
      error: () => {
        this.bairros = [];
        this.totalBairros = 0;
      }
    });
  }

  private calcularEspecialidades(profissionais: Profissional[]) {
    const mapa = new Map<string, number>();
    for (const p of profissionais) {
      mapa.set(p.especialidade, (mapa.get(p.especialidade) || 0) + 1);
    }
    this.especialidades = Array.from(mapa.entries())
      .map(([nome, quantidade]) => ({ nome, quantidade }))
      .sort((a, b) => b.quantidade - a.quantidade);
  }

  getPercentual(quantidade: number): number {
    return this.totalProfissionais > 0
      ? Math.round((quantidade / this.totalProfissionais) * 100)
      : 0;
  }
}
