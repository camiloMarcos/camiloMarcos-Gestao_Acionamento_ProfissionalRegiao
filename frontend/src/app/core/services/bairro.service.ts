import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Bairro } from '../models/bairro.model';

@Injectable({ providedIn: 'root' })
export class BairroService {
  private http = inject(HttpClient);
  private apiUrl = '/api/bairro';

  // Buscar todos os bairros
  buscarTodos(): Observable<Bairro[]> {
    return this.http.get<Bairro[]>(this.apiUrl);
  }

  // Buscar bairro por ID
  buscarPorId(id: number): Observable<Bairro> {
    return this.http.get<Bairro>(`${this.apiUrl}/${id}`);
  }

  // Buscar bairro por nome exato (case-insensitive)
  buscarPorNomeExato(nome: string): Observable<Bairro> {
    return this.http.get<Bairro>(`${this.apiUrl}/nome/${nome}`);
  }

  // Buscar bairros por nome contendo (busca parcial, case-insensitive)
  buscarPorNomeParcial(nome: string): Observable<Bairro[]> {
    return this.http.get<Bairro[]>(`${this.apiUrl}/buscar/${nome}`);
  }

  // Buscar bairros por cidade
  buscarPorCidade(cidade: string): Observable<Bairro[]> {
    return this.http.get<Bairro[]>(`${this.apiUrl}/cidade/${cidade}`);
  }

  // Buscar bairros por risco/perigo
  buscarPorRisco(perigo: boolean): Observable<Bairro[]> {
    return this.http.get<Bairro[]>(`${this.apiUrl}/risco/${perigo}`);
  }

  // Buscar bairros por profissional
  buscarPorProfissional(profissionalId: number): Observable<Bairro[]> {
    return this.http.get<Bairro[]>(`${this.apiUrl}/profissional/${profissionalId}`);
  }

  // Criar novo bairro
  criar(bairro: Bairro): Observable<Bairro> {
    return this.http.post<Bairro>(this.apiUrl, bairro);
  }

  // Atualizar bairro existente
  atualizar(id: number, bairro: Bairro): Observable<Bairro> {
    return this.http.put<Bairro>(`${this.apiUrl}/${id}`, bairro);
  }

  // Deletar bairro
  deletar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
