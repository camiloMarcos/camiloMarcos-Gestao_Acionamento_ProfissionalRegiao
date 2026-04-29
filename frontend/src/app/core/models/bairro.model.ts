import { Profissional } from './profissional.model';

export interface Bairro {
  id?: number;
  nome: string;
  cidade: string;
  perigoDistante: boolean;
  profissionais?: Profissional[];
}
