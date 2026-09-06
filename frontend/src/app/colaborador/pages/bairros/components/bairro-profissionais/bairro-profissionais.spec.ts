// Testes básicos do componente de profissionais vinculados.
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Profissional } from '../../../../../../core/models/profissional.model';
import { BairroProfissionaisComponent } from './bairro-profissionais';

describe('BairroProfissionaisComponent', () => {
  let component: BairroProfissionaisComponent;
  let fixture: ComponentFixture<BairroProfissionaisComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BairroProfissionaisComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(BairroProfissionaisComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('deve criar o componente', () => {
    expect(component).toBeTruthy();
  });

  it('deve renderizar a lista de profissionais', () => {
    const profissional: Profissional = {
      id: 1,
      nome: 'Maria Bthânia',
      especialidade: 'Pediatria',
      numeroConselho: 'CRM-123',
      telefone: '81999999999',
      email: 'maria@email.com',
      endereco: 'Rua A',
      cidade: 'Olinda',
    };

    fixture.componentRef.setInput('profissionais', [profissional]);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Maria Bthânia');
    expect(fixture.nativeElement.textContent).toContain('Pediatria');
    expect(fixture.nativeElement.querySelector('.data-table')).toBeTruthy();
  });

  it('deve renderizar o estado vazio', () => {
    expect(fixture.nativeElement.textContent).toContain(
      'Nenhum profissional está vinculado a este bairro.'
    );
    expect(fixture.nativeElement.querySelector('.empty-state')).toBeTruthy();
  });

  it('deve emitir o evento de associação', () => {
    const associar = vi.fn();
    component.associar.subscribe(associar);

    fixture.nativeElement.querySelector('.btn-primary').click();

    expect(associar).toHaveBeenCalledOnce();
  });

  it('deve emitir o ID ao remover um profissional', () => {
    const profissional: Profissional = {
      id: 7,
      nome: 'Joao Silva',
      especialidade: 'Clinica geral',
      numeroConselho: 'CRM-777',
      telefone: '81988888888',
      email: 'joao@email.com',
      endereco: 'Rua B',
      cidade: 'Recife',
    };
    const remover = vi.fn();
    component.remover.subscribe(remover);

    fixture.componentRef.setInput('profissionais', [profissional]);
    fixture.detectChanges();
    fixture.nativeElement.querySelector('.unlink').click();

    expect(remover).toHaveBeenCalledWith(7);
  });
});