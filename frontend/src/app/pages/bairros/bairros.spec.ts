import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Bairros } from './bairros';

describe('Bairros', () => {
  let component: Bairros;
  let fixture: ComponentFixture<Bairros>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Bairros],
    }).compileComponents();

    fixture = TestBed.createComponent(Bairros);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
