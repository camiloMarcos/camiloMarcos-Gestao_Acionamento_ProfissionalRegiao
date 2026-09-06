// Serviço responsável pelas operações entre profissionais e bairros.
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ProfissionalBairroService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = '/api/profissional-bairro';

  // Envia a solicitação para vincular um profissional já cadastrado ao bairro.
  associarProfissionalAoBairro(profissionalId: number, bairroId: number): Observable<string> {
    return this.http.post<string>(`${this.apiUrl}/associar`, {
      profissionalId,
      bairroId,
    });
  }

  desassociarProfissionalDoBairro(profissionalId: number, bairroId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${profissionalId}/bairro/${bairroId}`);
  }
}