import { Bairro } from './bairro.model';

export interface Profissional {
  id: number;
  nome: string;
  especialidade: string;
  numeroConselho: string;
  telefone: string;
  email: string;
  endereco: string;
  cidade: string;
  bairrosAtendidos?: Bairro[];
}
