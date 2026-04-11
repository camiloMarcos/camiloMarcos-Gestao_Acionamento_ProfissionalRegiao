import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Bairro } from '../models/bairro.model';
import { Profissional } from '../models/profissional.model';

@Injectable({ providedIn: 'root' })
export class BairroService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/bairro';

  buscarTodos(): Observable<Bairro[]> {
    return this.http.get<Bairro[]>(this.apiUrl);
  }

  buscarPorId(id: number): Observable<Bairro> {
    return this.http.get<Bairro>(`${this.apiUrl}/${id}`);
  }

  buscarPorNome(nome: string): Observable<Bairro[]> {
    return this.http.get<Bairro[]>(`${this.apiUrl}/nome/${nome}`);
  }

  buscarProfissionaisPorBairroId(id: number): Observable<Profissional[]> {
    return this.http.get<Profissional[]>(`${this.apiUrl}/${id}/profissionais`);
  }

  buscarProfissionaisPorBairroNome(nome: string): Observable<Profissional[]> {
    return this.http.get<Profissional[]>(`${this.apiUrl}/nome/${nome}/profissionais`);
  }
}
