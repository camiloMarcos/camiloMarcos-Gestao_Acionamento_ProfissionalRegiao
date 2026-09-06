import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { Bairro } from '../../../../../core/models/bairro.model';
import { Profissional } from '../../../../../core/models/profissional.model';
import { BairroService } from '../../../../../core/services/bairro.service';
import { ProfissionalBairroService } from '../../../../../core/services/profissional-bairro.service';
import { ProfissionalService } from '../../../../../core/services/profissional.service';
import { BairroDetalhes } from './bairro-detalhes';

describe('BairroDetalhes', () => {
  let component: BairroDetalhes;
  let fixture: ComponentFixture<BairroDetalhes>;
  let bairroService: { buscarPorId: ReturnType<typeof vi.fn> };
  let profissionalService: {
    buscarPorBairroId: ReturnType<typeof vi.fn>;
    buscarTodos: ReturnType<typeof vi.fn>;
  };
  let profissionalBairroService: {
    associarProfissionalAoBairro: ReturnType<typeof vi.fn>;
    desassociarProfissionalDoBairro: ReturnType<typeof vi.fn>;
  };
  let router: { navigate: ReturnType<typeof vi.fn> };

  const bairro: Bairro = {
    id: 12,
    nome: 'Boa Viagem',
    cidade: 'Recife',
    perigoDistante: false,
  };
  const profissional: Profissional = {
    id: 4,
    nome: 'Marisa Monte',
    especialidade: 'Geral',
    numeroConselho: 'CRM-444',
    telefone: '81999999999',
    email: 'marisa@email.com',
    endereco: 'Rua A',
    cidade: 'Recife',
  };
  const profissionalDisponivel: Profissional = {
    ...profissional,
    id: 5,
    nome: 'Maria Bthânia',
    numeroConselho: 'CRM-555',
    especialidade: 'Pediatria',
  };

  beforeEach(async () => {
    bairroService = { buscarPorId: vi.fn().mockReturnValue(of(bairro)) };
    profissionalService = {
      buscarPorBairroId: vi.fn().mockReturnValue(of([profissional])),
      buscarTodos: vi.fn().mockReturnValue(of([profissional, profissionalDisponivel])),
    };
    profissionalBairroService = {
      associarProfissionalAoBairro: vi.fn().mockReturnValue(of('ok')),
      desassociarProfissionalDoBairro: vi.fn().mockReturnValue(of(void 0)),
    };
    router = { navigate: vi.fn() };

    await TestBed.configureTestingModule({
      imports: [BairroDetalhes],
      providers: [
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: { get: () => '12' } } } },
        { provide: Router, useValue: router },
        { provide: BairroService, useValue: bairroService },
        { provide: ProfissionalService, useValue: profissionalService },
        { provide: ProfissionalBairroService, useValue: profissionalBairroService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(BairroDetalhes);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('deve carregar o bairro e os profissionais', () => {
    expect(component.bairro()).toEqual(bairro);
    expect(component.profissionais()).toEqual([profissional]);
    expect(component.carregando()).toBe(false);
  });

  it('deve exibir a quantidade de profissionais', () => {
    expect(fixture.nativeElement.textContent).toContain('Profissionais vinculados');
    expect(fixture.nativeElement.querySelector('.summary-item strong').textContent).toContain('1');
  });

  it('deve exibir o estado vazio quando não houver profissionais', () => {
    component.profissionais.set([]);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain(
      'Nenhum profissional está vinculado a este bairro.'
    );
  });

  it('deve mostrar erro ao falhar no carregamento do bairro', async () => {
    bairroService.buscarPorId.mockReturnValue(throwError(() => new Error('falha')));
    const novoFixture = TestBed.createComponent(BairroDetalhes);

    novoFixture.detectChanges();

    expect(novoFixture.componentInstance.erro()).toContain(
      'Não foi possível carregar os detalhes deste bairro.'
    );
  });

  it('deve voltar para a lista de bairros', () => {
    component.voltar();

    expect(router.navigate).toHaveBeenCalledWith(['/bairros']);
  });

  it('deve navegar para edição do bairro atual', () => {
    component.editarBairro();

    expect(router.navigate).toHaveBeenCalledWith(['/bairros'], { queryParams: { edit: 12 } });
  });

  it('deve atualizar a lista após associação', () => {
    component.solicitarAssociacao();
    component.selecionarProfissional(5);
    component.confirmarAssociacao();

    expect(profissionalBairroService.associarProfissionalAoBairro).toHaveBeenCalledWith(5, 12);
    expect(component.profissionais()).toEqual([profissional]);
    expect(component.mensagem()).toBe('Profissional associado com sucesso.');
    expect(profissionalService.buscarPorBairroId).toHaveBeenCalledTimes(2);
  });

  it('deve atualizar a lista após remoção', () => {
    profissionalService.buscarPorBairroId
      .mockReturnValueOnce(of([profissional]))
      .mockReturnValueOnce(of([]));
    component.solicitarRemocao(4);
    component.confirmarRemocao();

    expect(profissionalBairroService.desassociarProfissionalDoBairro).toHaveBeenCalledWith(4, 12);
    expect(component.profissionais()).toEqual([]);
    expect(component.mensagem()).toBe('Vínculo removido com sucesso.');
    expect(profissionalService.buscarPorBairroId).toHaveBeenCalledTimes(2);
  });
});