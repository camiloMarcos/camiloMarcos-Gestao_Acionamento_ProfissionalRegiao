primeiro import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Profissional } from '../models/profissional.model';

@Injectable({ providedIn: 'root' })
export class ProfissionalService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/profissional';

  buscarTodos(): Observable<Profissional[]> {
    return this.http.get<Profissional[]>(this.apiUrl);
  }

  buscarPorId(id: number): Observable<Profissional> {
    return this.http.get<Profissional>(`${this.apiUrl}/${id}`);
  }

  buscarPorNome(nome: string): Observable<Profissional[]> {
    return this.http.get<Profissional[]>(`${this.apiUrl}/nome/${nome}`);
  }

  salvar(profissional: Profissional): Observable<Profissional> {
    return this.http.post<Profissional>(this.apiUrl, profissional);
  }

  atualizar(id: number, profissional: Profissional): Observable<Profissional> {
    return this.http.put<Profissional>(`${this.apiUrl}/${id}`, profissional);
  }

  deletar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
