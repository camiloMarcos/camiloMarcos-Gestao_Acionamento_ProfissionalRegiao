# 📘 Guia de Estudo — Frontend Angular

> Documento de referência para entender a arquitetura e cada parte do frontend do projeto **Gestão de Acionamento de Profissionais por Região**.

---

## 1. Visão Geral

Este é um projeto **Angular 21** que funciona como interface visual para o sistema de gestão. Ele se conecta ao backend Java (Spring Boot) que roda em `http://localhost:8080`.

**Tecnologias principais:**
- Angular 21.2 (standalone components)
- TypeScript 5.9
- RxJS (programação reativa)
- Vitest (testes)
- Google Material Icons Outlined (ícones)
- Fonte Inter (tipografia)

---

## 2. Arquitetura — Como tudo se encaixa

Imagine o frontend como um **condomínio**:

| Conceito                 | Arquivo/Pasta                        | O que faz                                                     |
|--------------------------|--------------------------------------|---------------------------------------------------------------|
| **Fundação**             | `src/index.html`                     | O HTML base que o navegador carrega primeiro                  |
| **Porta de entrada**     | `src/main.ts`                        | O "interruptor" que liga a aplicação Angular                  |
| **Planta do condomínio** | `src/app/app.config.ts`              | Configurações globais (rotas, HTTP, etc.)                     |
| **Portaria**             | `src/app/app.ts` + `app.routes.ts`   | Shell mínimo que decide qual módulo/"apartamento" carregar    |
| **Apartamento**          | `src/app/colaborador/`               | Módulo do Colaborador (layout + páginas próprias)             |
| **Áreas comuns**         | `src/app/core/`                      | Models e serviços compartilhados entre todos os módulos       |

### Fluxo de inicialização (o que acontece quando você abre o site):

```
1. Navegador abre index.html → encontra <app-root></app-root>
2. main.ts inicializa o Angular e carrega o componente App (shell mínimo)
3. App olha a URL e carrega o módulo certo (ex: módulo Colaborador)
4. O módulo Colaborador renderiza SEU layout (sidebar + header)
5. Dentro do layout, o <router-outlet> exibe a página da URL atual
```

### O que é Modularização?

É como organizar uma empresa em **departamentos**. Em vez de jogar tudo numa sala só:
- Cada perfil de usuário (colaborador, admin, etc.) vira um **módulo independente**
- Cada módulo tem seu próprio layout, suas páginas e suas rotas
- O que é comum a todos (models, serviços) fica numa pasta **compartilhada** (`core/`)

**Por que isso é bom?**
- Se você alterar algo do Colaborador, não tem risco de quebrar o Admin (e vice-versa)
- O navegador só carrega o módulo que o usuário precisa (**lazy loading** — carregamento sob demanda)
- Fica muito mais fácil adicionar novos módulos no futuro

---

## 3. Diferença entre `index.html` e `Dashboard`

Essa é uma dúvida super comum!

### `index.html` — "O esqueleto"
- É o **único arquivo HTML real** que o navegador carrega
- Contém apenas `<head>` (fontes, título, favicon) e `<body>` com `<app-root></app-root>`
- **Nunca é editado** para adicionar conteúdo visual
- É como uma **moldura vazia** onde o Angular vai "pintar" tudo dinamicamente

### `Dashboard` — "A página inicial"
- É um **componente Angular** (um pedaço reutilizável de interface)
- Aparece **dentro** do `<router-outlet>` quando a URL é `/` (raiz do site)
- Tem lógica própria: busca dados da API, calcula estatísticas, exibe gráficos
- É o **conteúdo real** que o usuário vê

> **Analogia**: `index.html` é a **TV** (o aparelho). O `Dashboard` é o **programa** que está passando nela.

---

## 4. Estrutura de Pastas

```
frontend/
├── angular.json              ← Configuração do projeto Angular
├── package.json              ← Dependências e scripts
├── tsconfig.json             ← Configuração do TypeScript
├── tsconfig.app.json         ← Config TS para o app
├── tsconfig.spec.json        ← Config TS para testes
├── public/                   ← Arquivos estáticos (favicon, imagens)
└── src/
    ├── index.html            ← HTML base (fundação)
    ├── main.ts               ← Ponto de entrada (liga o Angular)
    ├── styles.css            ← Estilos GLOBAIS
    └── app/
        ├── app.ts            ← Shell mínimo (só <router-outlet>)
        ├── app.html          ← Apenas: <router-outlet />
        ├── app.css           ← Estilos mínimos
        ├── app.config.ts     ← Configurações globais
        ├── app.routes.ts     ← Rotas raiz (direciona para módulos)
        ├── app.spec.ts       ← Testes do componente App
        │
        ├── core/                          ← 🏗️ COMPARTILHADO
        │   └── models/                    ←    (usado por todos os módulos)
        │       ├── profissional.model.ts   ←    Interface Profissional
        │       └── bairro.model.ts         ←    Interface Bairro
        │
        └── colaborador/                   ← 👤 MÓDULO DO COLABORADOR
            ├── colaborador.routes.ts      ←    Rotas internas do módulo
            ├── layout/                    ←    Layout exclusivo (sidebar + header)
            │   ├── layout.ts
            │   ├── layout.html
            │   └── layout.css
            └── pages/                     ←    Páginas do colaborador
                ├── dashboard/             ←    (✅ implementada)
                │   ├── dashboard.ts
                │   ├── dashboard.html
                │   ├── dashboard.css
                │   └── dashboard.spec.ts
                ├── bairros/               ←    (⏳ esqueleto)
                │   ├── bairros.ts
                │   ├── bairros.html
                │   ├── bairros.css
                │   └── bairros.spec.ts
                └── profissionais/         ←    (⏳ esqueleto)
                    ├── profissionais.ts
                    ├── profissionais.html
                    ├── profissionais.css
                    └── profissionais.spec.ts
```

### Entendendo as 3 camadas:

```
┌───────────────────────────────────────────────┐
│  app/ (Shell)                                 │  ← "Portaria" — só redireciona
│  ┌───────────────────────────────────────────┐│
│  │  core/ (Compartilhado)                    ││  ← "Áreas comuns" — models, services
│  └───────────────────────────────────────────┘│
│  ┌───────────────────────────────────────────┐│
│  │  colaborador/ (Módulo)                    ││  ← "Apartamento" — layout + páginas
│  │    layout/ + pages/                       ││
│  └───────────────────────────────────────────┘│
│  ┌───────────────────────────────────────────┐│
│  │  admin/ (Futuro módulo)                   ││  ← Outro "apartamento" (quando precisar)
│  └───────────────────────────────────────────┘│
└───────────────────────────────────────────────┘
```

---

## 5. Cada Pacote/Dependência Explicado

### Dependências de Produção (o que vai pro site final):

| Pacote                     | Para que serve                                                     |
|----------------------------|--------------------------------------------------------------------|
| `@angular/core`            | O "motor" do Angular — componentes, injeção de dependências        |
| `@angular/common`          | Diretivas comuns (`*ngIf`, `*ngFor`), pipes (`DatePipe`)           |
| `@angular/compiler`        | Compila os templates HTML em código JavaScript                     |
| `@angular/forms`           | Formulários (inputs, validação, `ngModel`) — **ainda não usado**   |
| `@angular/platform-browser`| Permite o Angular rodar no navegador                               |
| `@angular/router`          | Sistema de navegação entre páginas (URLs)                          |
| `rxjs`                     | Programação reativa — lida com chamadas HTTP (Observables)         |
| `tslib`                    | Helpers do TypeScript para reduzir código gerado                   |

### Dependências de Desenvolvimento (só para desenvolver/testar):

| Pacote                    | Para que serve                                                      |
|---------------------------|---------------------------------------------------------------------|
| `@angular/cli`            | Ferramenta de linha de comando (`ng serve`, `ng generate`, etc.)    |
| `@angular/build`          | Builder que compila e empacota o projeto                            |
| `@angular/compiler-cli`   | Compilador Angular para produção                                    |
| `typescript`              | Linguagem usada no código (TypeScript → JavaScript)                 |
| `vitest`                  | Framework de testes (moderno, substitui Karma/Jasmine)              |
| `jsdom`                   | Simula um navegador para rodar testes sem abrir o Chrome            |
| `prettier`                | Formatador de código (deixa o código bonito automaticamente)        |

---

## 6. Os Arquivos de Configuração

### `angular.json`
A "certidão de nascimento" do projeto. Define:
- Que o projeto é do tipo `application`
- Que o ponto de entrada é `src/main.ts`
- Que os estilos globais estão em `src/styles.css`
- Limites de tamanho do build (500kB warning, 1MB erro)
- Configurações de dev (source maps) vs produção (otimizado)

### `tsconfig.json`
Configurações do TypeScript com **modo estrito ativado** (`"strict": true`). Isso significa que o compilador vai te avisar de erros potenciais — ótimo para aprender!

### `package.json`
Lista de dependências e scripts disponíveis:
- `npm start` ou `ng serve` → roda o servidor de desenvolvimento
- `ng test` → roda os testes
- `ng build` → gera o build de produção

---

## 7. Os Componentes — Explicação Detalhada

### O que é um Componente?

Um componente é um **bloco independente de interface**. Cada componente tem 3 (ou 4) arquivos:

| Arquivo     | Função                                          |
|-------------|--------------------------------------------------|
| `.ts`       | **Lógica** — variáveis, métodos, chamadas à API  |
| `.html`     | **Template** — o que aparece na tela              |
| `.css`      | **Estilos** — visual do componente (encapsulado!) |
| `.spec.ts`  | **Testes** — verifica se o componente funciona    |

> **Encapsulado** significa que o CSS de um componente NÃO afeta outro. É como se cada componente tivesse seu próprio mundinho de estilos.

---

### Componente `App` — O Shell (Casca Mínima)

**Arquivos**: `app.ts` | `app.html` | `app.css`

Após a modularização, o App ficou **extremamente simples**. Ele é apenas uma "portaria" que redireciona para o módulo certo.

**O que contém agora:**
- Apenas um `<router-outlet>` — nada de sidebar, header ou visual
- Sua única função é carregar o módulo correto baseado na URL

**Código do `app.ts` (simplificado):**
```typescript
@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',    // Contém apenas: <router-outlet />
  styleUrl: './app.css'
})
export class App {}
```

> **Antes** o App tinha sidebar + header. **Agora** isso foi movido para o layout do Colaborador.
> Isso permite que no futuro um módulo de Admin tenha um layout DIFERENTE.

---

### Componente `Layout` — O Layout do Colaborador

**Arquivos**: `colaborador/layout/layout.ts` | `layout.html` | `layout.css`

É o **esqueleto visual** do módulo Colaborador. Tudo que o colaborador vê tem esse layout por volta.

**O que contém:**
- **Sidebar** (menu lateral): Links para Dashboard, Profissionais e Bairros
- **Header** (barra superior): Botão de menu, notificações, dark mode, avatar
- **`<router-outlet>`**: O "buraco" onde as páginas do colaborador são injetadas

**Código importante no `layout.ts`:**
```typescript
@Component({
  selector: 'app-colaborador-layout',               // Nome da tag HTML
  imports: [RouterOutlet, RouterLink, RouterLinkActive], // Dependências
  templateUrl: './layout.html',                      // Template visual
  styleUrl: './layout.css'                           // Estilos
})
export class Layout {
  protected readonly title = signal('Gestão de Acionamento'); // Signal (reativo)
  protected sidebarCollapsed = false;                         // Estado do menu

  toggleSidebar() {                               // Abre/fecha o menu lateral
    this.sidebarCollapsed = !this.sidebarCollapsed;
  }
}
```

**Conceitos importantes aqui:**
- `signal()` — forma moderna do Angular de criar variáveis reativas
- `imports: [...]` — standalone components importam dependências direto no componente (sem NgModule)
- `RouterOutlet` — componente que renderiza a página da rota atual
- `RouterLink` — transforma um `<a>` em link de navegação Angular (sem recarregar a página)
- `RouterLinkActive` — adiciona a classe CSS `active` ao link da página atual

---

### Componente `Dashboard` — A Página Principal

**Arquivos**: `dashboard.ts` | `dashboard.html` | `dashboard.css`

O componente **mais completo** do projeto. Serve de referência para implementar os outros.

**O que faz no TypeScript (`dashboard.ts`):**

```typescript
// 1. Importa interfaces compartilhadas da pasta core/models/
//    (antes eram definidas aqui dentro, agora ficam no lugar certo)
import { Profissional } from '../../../core/models/profissional.model';
import { Bairro } from '../../../core/models/bairro.model';

// 2. Injeta o HttpClient para fazer chamadas à API
private http = inject(HttpClient);
private apiUrl = 'http://localhost:8080';

// 3. No ngOnInit (quando a página carrega):
//    - Define saudação (Bom dia/Boa tarde/Boa noite)
//    - Chama GET /profissional e GET /bairro
//    - Calcula estatísticas
```

> **Nota sobre os imports**: O `../../../core/models/` parece complicado, mas é só o caminho relativo:
> Dashboard está em `colaborador/pages/dashboard/` → sobe 3 pastas (`../../../`) → entra em `core/models/`

**O que mostra no HTML (`dashboard.html`):**
- Saudação personalizada com data
- Banner informativo
- 4 cards de resumo (Profissionais, Bairros, Sem Cobertura, Área de Risco)
- Lista dos últimos 5 profissionais
- Lista dos últimos 5 bairros
- Gráfico de barras por especialidade

**Sintaxe do template Angular usada:**
```html
{{ variavel }}                          ← Interpolação (mostra o valor)
[class.danger]="condicao"               ← Binding de classe CSS
(click)="metodo()"                      ← Evento de clique
[style.width.%]="valor"                 ← Binding de estilo inline
@if (condicao) { ... } @else { ... }    ← Condicional (Angular 17+)
@for (item of lista; track item.id) { } ← Loop (Angular 17+)
```

---

### Componente `Bairros` — Esqueleto Vazio ⏳

**Localização**: `colaborador/pages/bairros/`

```typescript
export class Bairros {}   // Classe vazia, sem lógica
```
```html
<p>bairros works!</p>     <!-- Placeholder padrão do Angular CLI -->
```

**Precisa ser implementado com:** listagem, formulário de cadastro/edição, integração com API.

---

### Componente `Profissionais` — Esqueleto Vazio ⏳

**Localização**: `colaborador/pages/profissionais/`

Mesmo caso do Bairros — só o placeholder padrão.

---

## 8. Sistema de Rotas (com Lazy Loading)

Agora o sistema de rotas funciona em **2 níveis**:

### Nível 1 — Rotas Raiz (`app.routes.ts`)

O App raiz não sabe nada sobre Dashboard, Bairros ou Profissionais. Ele só sabe que existe um **módulo Colaborador**:

```typescript
// app.routes.ts — "Portaria" do condomínio
export const routes: Routes = [
  {
    path: '',
    loadChildren: () =>
      import('./colaborador/colaborador.routes').then((m) => m.colaboradorRoutes),
  },
  // Futuro: { path: 'admin', loadChildren: () => import('./admin/admin.routes')... }
];
```

> **`loadChildren`** = **Lazy Loading** (carregamento sob demanda).
> O código do Colaborador **só é baixado** quando alguém acessa a URL.
> Isso deixa o carregamento inicial do site mais rápido!

### Nível 2 — Rotas do Colaborador (`colaborador.routes.ts`)

Dentro do módulo, as rotas são definidas como **filhas** do Layout:

```typescript
// colaborador.routes.ts — "Mapa do apartamento"
export const colaboradorRoutes: Routes = [
  {
    path: '',
    component: Layout,           // ← Sidebar + Header envolvem tudo
    children: [
      { path: '', component: Dashboard },          // URL: localhost:4200/
      { path: 'bairros', component: Bairros },      // URL: localhost:4200/bairros
      { path: 'profissionais', component: Profissionais }, // URL: localhost:4200/profissionais
    ],
  },
];
```

### Tabela de Rotas:

| URL                | Módulo       | Layout       | Página         | Status            |
|--------------------|-------------|--------------|----------------|-------------------|
| `/`                | Colaborador | Layout (sidebar) | Dashboard  | ✅ Implementado    |
| `/bairros`         | Colaborador | Layout (sidebar) | Bairros    | ⏳ Só esqueleto    |
| `/profissionais`   | Colaborador | Layout (sidebar) | Profissionais | ⏳ Só esqueleto |

### Como funciona a navegação agora:
1. Usuário abre `localhost:4200` → App (shell) carrega o módulo Colaborador via lazy loading
2. Módulo Colaborador renderiza o Layout (sidebar + header)
3. Dentro do Layout, o `<router-outlet>` mostra a página da URL (Dashboard)
4. Usuário clica em "Bairros" na sidebar
5. O Angular **não recarrega** a página (SPA = Single Page Application)
6. Apenas o conteúdo dentro do `<router-outlet>` muda para o componente Bairros
7. A URL atualiza para `localhost:4200/bairros`

---

## 9. Estilos — Global vs Componente

### `styles.css` — Estilos Globais
```css
* { margin: 0; padding: 0; box-sizing: border-box; }  /* Reset */
html, body { font-family: 'Inter'; background: #f0f2f5; }
```
Afeta **toda** a aplicação.

### CSS de Componente (ex: `dashboard.css`)
- **Encapsulado**: só afeta o componente onde está definido
- O Angular adiciona atributos únicos (como `_ngcontent-abc123`) automaticamente para isolar os estilos
- Você pode usar `:host` para estilizar o elemento raiz do componente

---

## 10. Configuração da Aplicação (`app.config.ts`)

```typescript
export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),  // Captura erros globais
    provideRouter(routes),                 // Habilita o sistema de rotas
    provideHttpClient()                    // Habilita chamadas HTTP (fetch da API)
  ]
};
```

É aqui que se registram **serviços globais**. Quando você criar services, interceptors ou guards, é aqui que eles serão configurados.

---

## 11. Testes (arquivos `.spec.ts`)

O projeto usa **Vitest** (moderno e rápido). Os testes existentes são básicos — apenas verificam se os componentes são criados sem erro:

```typescript
it('should create', () => {
  expect(component).toBeTruthy();  // "O componente existe? Sim!"
});
```

Para rodar: `ng test`

---

## 12. Glossário de Termos

| Termo               | Significado                                                        |
|----------------------|--------------------------------------------------------------------|
| **Componente**       | Bloco reutilizável de interface (TS + HTML + CSS)                  |
| **Template**         | O HTML de um componente                                            |
| **Standalone**       | Componente que não precisa de NgModule (forma moderna)              |
| **Signal**           | Variável reativa moderna do Angular                                |
| **Observable**       | Fluxo de dados assíncrono (usado em HTTP)                          |
| **subscribe()**      | "Escutar" um Observable e reagir quando dados chegam               |
| **inject()**         | Pedir ao Angular uma instância de um serviço                       |
| **Router**           | Sistema que controla qual página aparece em qual URL               |
| **RouterOutlet**     | "Buraco" no HTML onde a página da rota atual é renderizada         |
| **SPA**              | Single Page Application — a página nunca recarrega por completo    |
| **ngOnInit()**       | Método executado quando o componente é inicializado                |
| **Interpolação**     | `{{ variavel }}` — mostra o valor de uma variável no HTML          |
| **Binding**          | Conexão entre dados do TypeScript e o template HTML                |
| **Encapsulamento**   | CSS de um componente não vaza para outros                          |
| **Módulo**           | Agrupamento de funcionalidades por perfil/área (ex: colaborador)   |
| **Lazy Loading**     | Carregamento sob demanda — só baixa o código quando o usuário acessa |
| **Shell**            | Casca mínima da aplicação que carrega os módulos                   |
| **Layout**           | Componente "moldura" que envolve as páginas (sidebar + header)     |
| **Model/Interface**  | Definição da "forma" dos dados (quais campos existem e seus tipos) |
| **core/**            | Pasta com código compartilhado entre todos os módulos              |

---

## 13. O que já foi feito na modularização ✅

1. ✅ **Models compartilhados criados** → `core/models/profissional.model.ts` e `bairro.model.ts`
2. ✅ **Layout do Colaborador extraído** → `colaborador/layout/` (sidebar + header)
3. ✅ **Páginas movidas para o módulo** → `colaborador/pages/`
4. ✅ **Rotas do Colaborador isoladas** → `colaborador/colaborador.routes.ts`
5. ✅ **App raiz simplificado** → Shell mínimo com apenas `<router-outlet />`
6. ✅ **Lazy Loading ativado** → Módulo carrega sob demanda
7. ✅ **Dashboard atualizado** → Usa models do `core/` em vez de interfaces locais

---

## 14. O que falta fazer (Próximos Passos)

1. **Implementar a página Profissionais** — CRUD completo (listar, criar, editar, excluir)
2. **Implementar a página Bairros** — CRUD completo
3. **Criar Services** — Extrair as chamadas HTTP do Dashboard para services reutilizáveis:
   - `core/services/profissional.service.ts`
   - `core/services/bairro.service.ts`
4. **Funcionalidades do header** — Notificações, dark mode
5. **Tratamento de erros** — Mensagens amigáveis quando a API estiver fora

### Como adicionar um novo módulo no futuro (ex: Admin):
```
1. Criar pasta: src/app/admin/
2. Criar layout: admin/layout/ (pode ter sidebar diferente!)
3. Criar páginas: admin/pages/
4. Criar rotas: admin/admin.routes.ts
5. Registrar no app.routes.ts:
   { path: 'admin', loadChildren: () => import('./admin/admin.routes')... }
```

---

## 15. Comandos Úteis

```bash
cd frontend

# Primeira vez (instalar dependências)
npm install

# Rodar o servidor de desenvolvimento (http://localhost:4200)
ng serve

# Criar um novo componente DENTRO do módulo colaborador
ng generate component colaborador/pages/nome-do-componente

# Criar um service compartilhado
ng generate service core/services/nome-do-service

# Rodar testes
ng test

# Gerar build de produção
ng build
```

---

## 16. Resumo Final

```
┌──────────────────────────────────────────────────────────┐
│                       index.html                          │
│  ┌────────────────────────────────────────────────────┐  │
│  │  <app-root>  (App Shell — só um <router-outlet>)   │  │
│  │  ┌──────────────────────────────────────────────┐  │  │
│  │  │     MÓDULO COLABORADOR (lazy loaded)          │  │  │
│  │  │  ┌──────────┐  ┌──────────────────────────┐  │  │  │
│  │  │  │ SIDEBAR  │  │  HEADER                  │  │  │  │
│  │  │  │          │  ├──────────────────────────┤  │  │  │
│  │  │  │ Dashboard│  │                          │  │  │  │
│  │  │  │ Profiss. │  │    <router-outlet>       │  │  │  │
│  │  │  │ Bairros  │  │                          │  │  │  │
│  │  │  │          │  │  ← Página atual aqui     │  │  │  │
│  │  │  │          │  │                          │  │  │  │
│  │  │  └──────────┘  └──────────────────────────┘  │  │  │
│  │  └──────────────────────────────────────────────┘  │  │
│  └────────────────────────────────────────────────────┘  │
│                                                          │
│  ┌────────────────────────────────────────────────────┐  │
│  │  core/ (models e services compartilhados)           │  │
│  │  Profissional, Bairro                               │  │
│  └────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────┘
```

> O projeto está **modularizado**! O módulo do Colaborador tem seu próprio layout,
> suas páginas e suas rotas — tudo isolado. O Dashboard está implementado, e as
> páginas de Bairros e Profissionais são esqueletos prontos para serem construídos.
> Para adicionar um novo módulo (Admin, Profissional, etc.), basta criar uma nova
> pasta com layout + páginas + rotas, sem mexer em nada do Colaborador.
