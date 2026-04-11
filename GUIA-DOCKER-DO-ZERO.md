# 🐳 Guia Completo: Docker para Iniciantes - Do Zero ao Deploy

**Projeto:** Sistema de Gestão de Profissionais e Bairros  
**Objetivo:** Empacotar Backend (Spring Boot) + Frontend (Angular) em Docker

---

## 📚 Índice
1. [O que é Docker e por quê usar?](#1-o-que-é-docker)
2. [Pré-requisitos - Docker Installation](#2-pré-requisitos)
3. [Conceitos Básicos (Simples!)](#3-conceitos-básicos)
4. [Criando Dockerfile do Backend](#4-dockerfile-backend)
5. [Criando Dockerfile do Frontend](#5-dockerfile-frontend)
6. [Docker Compose - Tudo junto](#6-docker-compose)
7. [Testando e Rodando](#7-testando-e-rodando)
8. [Troubleshooting](#8-troubleshooting)
9. [Fazer Manualmente no VS Code](#9-fazer-manualmente-no-vs-code)

---

## 1. O que é Docker?

### 🤔 Explicação Simples

Pense em Docker como um **contêiner de transporte** (tipo os que veem em navio).

**A Analogia:**
```
┌──────────────────────────────────────┐
│          DOCKÉ (CONTÊINER)           │
│  ┌────────────────────────────────┐  │
│  │ Backend (Spring Boot 8080)     │  │
│  │ - Java 17                      │  │
│  │ - Maven                        │  │
│  │ - Dependências                 │  │
│  └────────────────────────────────┘  │
│  ┌────────────────────────────────┐  │
│  │ Frontend (Angular 4200)        │  │
│  │ - Node.js                      │  │
│  │ - npm                          │  │
│  │ - Dependências                 │  │
│  └────────────────────────────────┘  │
│  ┌────────────────────────────────┐  │
│  │ Banco de Dados (H2)            │  │
│  │ - Configuração                 │  │
│  └────────────────────────────────┘  │
└──────────────────────────────────────┘

✅ Sobe igual em qualquer máquina!
```

### ✅ Por que Docker?

**Sem Docker (Problema Atual):**
```
MEU COMPUTADOR          SEU COMPUTADOR
Java 17 ✅              Java 21 ❌
Node 20 ✅              Node 18 ❌
Port 8080 ✅            Port 8080 em uso ❌
"Na minha máquina funciona!" 😢
```

**Com Docker (Solução):**
```
MEU CONTAINER          SEU CONTAINER
Java 17 ✅              Java 17 ✅
Node 20 ✅              Node 20 ✅
Port 8080 ✅            Port 8080 ✅
"Funciona em qualquer lugar!" 😊
```

### 🎯 O que Docker faz:

1. **Empacota** seu código + dependências
2. **Isola** (não interfere com outras coisas)
3. **Reproduz** (roda igual em todo lugar)

---

## 2. Pré-requisitos

### ✅ Você Precisa de:

#### 2.1 Docker Desktop

**No Windows:**
1. Acesse: https://www.docker.com/products/docker-desktop
2. Baixe a versão Windows
3. Execute o instalador
4. Reinicie o computador

**Verificar instalação:**
```powershell
docker --version
# Deve mostrar: Docker version 24.0.0 (ou similar)

docker run hello-world
# Deve mostrar uma mensagem amigável
```

Se tiver erro, releia a caixa de troubleshooting no final.

---

## 3. Conceitos Básicos (Vou Explicar Simples!)

### 3.1 Imagem vs Contêiner

**Imagem** = Receita (como um bolo de chocolate)
**Contêiner** = Bolo pronto (o resultado da receita)

```
IMAGEM (Dockerfile)         CONTÊINER (docker run)
┌──────────────────┐        ┌──────────────┐
│ FROM ubuntu      │   →    │ Aplicação    │
│ RUN apt-get      │        │ Rodando      │
│ COPY app.jar     │        │ Porta 8080   │
│ EXPOSE 8080      │        │ Banco H2     │
│ CMD ["java"...]  │        │ Funcionando! │
└──────────────────┘        └──────────────┘
```

### 3.2 As Linhas do Dockerfile (Receita)

```dockerfile
# FROM = comece com essa "base"
FROM openjdk:17-jdk-slim
# O que você está instalando? Java 17 em cima de Linux slim

# WORKDIR = mude para essa pasta
WORKDIR /app
# Igual fazer: cd /app

# COPY = copie arquivo
COPY target/*.jar app.jar
# Copia seu arquivo JAR compilado

# EXPOSE = abra essa porta
EXPOSE 8080
# Só documenta, não abre de verdade

# CMD = quando iniciar, execute isso
CMD ["java", "-jar", "app.jar"]
# Quando o contêiner iniciar, rode: java -jar app.jar
```

### 3.3 Docker Compose (Dirigente de Orquestra 🎼)

Sem Compose:
```powershell
docker build -t backend .
docker build -t frontend .
docker run -p 8080:8080 backend
docker run -p 4200:4200 frontend
# 4 comandos, fácil esquecer, fácil errar
```

Com Compose:
```powershell
docker-compose up
# Um comando. Sobe tudo. Pronto!
```

---

## 4. Dockerfile do Backend

### 📂 Estrutura que Você Vai Ter

```
backend/
├── pom.xml
├── mvnw
├── src/
├── target/
└── Dockerfile          👈 CRIAR AQUI
```

### 🎬 Passo 1: Compilar o Backend

Antes de fazer Docker, você precisa compilar seu projeto Spring Boot.

**Abra PowerShell na pasta do backend:**
```powershell
cd C:\Users\marco\OneDrive\Documentos\MeusProjetos\superHome\backend

# Compile seu projeto
mvnw clean package -DskipTests

# Aguarde... (leva 2-3 minutos)
# Se ver: BUILD SUCCESS ✅
```

✅ **O que aconteceu:** Sua aplicação foi compilada. Criou um arquivo `demo-0.0.1-SNAPSHOT.jar` na pasta `target/`

---

### 🐳 Passo 2: Criar o Dockerfile do Backend

**Crie um arquivo:** `Dockerfile` (sem extensão!) na raiz da pasta do backend

**Caminho completo:**
```
C:\Users\marco\OneDrive\Documentos\MeusProjetos\superHome\backend\Dockerfile
```

**Conteúdo:**

```dockerfile
# Etapa 1: Compilar (Build)
FROM maven:3.9-eclipse-temurin-17 as builder

# Pasta de trabalho dentro do contêiner
WORKDIR /app

# Copiar arquivo de dependências
COPY pom.xml .
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn

# Copiar código-fonte
COPY src ./src

# Compilar
RUN mvn clean package -DskipTests

# ============================================

# Etapa 2: Rodar (Runtime)
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copiar JAR da etapa anterior
COPY --from=builder /app/target/*.jar app.jar

# Documentar que nossa app usa porta 8080
EXPOSE 8080

# Inicializar
CMD ["java", "-jar", "app.jar"]
```

**O que cada linha significa:**

| Linha | O que faz | Por quê |
|-------|-----------|--------|
| `FROM maven:3.9...` | Começa com Maven instalado | Para compilar Java |
| `WORKDIR /app` | Cria pasta /app | Tudo aqui dentro |
| `COPY pom.xml .` | Copia seu pom.xml | Maven precisa disso |
| `RUN mvn clean package` | Compila seu código | Cria o JAR |
| `FROM openjdk:17...` | Começa novo contêiner | Só com Java, sem Maven |
| `COPY --from=builder...` | Copia JAR compilado | Leve e rápido |
| `EXPOSE 8080` | Documenta porta 8080 | Informativo |
| `CMD ["java"...]` | Ao iniciar, rode isso | Inicia sua aplicação |

**Por que 2 Etapas (FROM)?**
- Primeira compila (precisa Maven, pesado)
- Segunda executa (só precisa Java, leve)
- Imagem final muito menor! 📉

---

### ✅ Teste o Dockerfile do Backend

**Na pasta do backend, rode:**

```powershell
# Buildar a imagem
docker build -t backend-app .

# Espere terminar (5-10 minutos primeira vez)
# Deve terminar com: Successfully tagged backend-app:latest

# Testar rodando
docker run -p 8080:8080 backend-app

# Abra o navegador: http://localhost:8080/bairro
# Deve retornar um JSON com bairros

# Parar: Ctrl+C
```

✅ **Sucesso!** Seu backend tá em Docker!

---

## 5. Dockerfile do Frontend

### 📂 Estrutura

```
frontend/
├── src/
├── package.json
├── angular.json
└── Dockerfile          👈 CRIAR AQUI
```

### 🎬 Passo 1: Criar o Dockerfile do Frontend

**Crie:** `Dockerfile` na raiz da pasta do frontend

```dockerfile
# Etapa 1: Build (Compilar)
FROM node:20 as builder

WORKDIR /app

# Copiar package.json
COPY package.json package-lock.json ./

# Instalar dependências
RUN npm ci

# Copiar código
COPY . .

# Compilar para produção
RUN npm run build

# ============================================

# Etapa 2: Runtime (Servir)
FROM nginx:latest

# Copiar arquivos compilados para nginx
COPY --from=builder /app/dist/frontend/browser /usr/share/nginx/html

# Copiar configuração nginx (próximo passo)
COPY nginx.conf /etc/nginx/nginx.conf

# Porta 4200
EXPOSE 4200

# Iniciar nginx
CMD ["nginx", "-g", "daemon off;"]
```

**Explicação:**

| Linha | O que faz | Por quê |
|-------|-----------|--------|
| `FROM node:20` | Começa com Node | Para compilar Angular |
| `RUN npm ci` | Instala dependências | Precisa do npm_modules |
| `RUN npm run build` | Compila Angular | Cria HTML/CSS/JS final |
| `FROM nginx:latest` | Começa novo contêiner | Servidor web leve |
| `COPY --from=builder...` | Copia HTML compilado | Nginx serve arquivos estáticos |
| `EXPOSE 4200` | Documenta porta 4200 | Interface web |

---

### 🎬 Passo 2: Configuração do Nginx

**Crie:** `nginx.conf` na raiz da pasta do frontend

```nginx
events {
    worker_connections 1024;
}

http {
    # Tipos de arquivo
    include /etc/nginx/mime.types;
    default_type application/octet-stream;

    # Servidor
    server {
        listen 4200;
        server_name _;

        # Pasta onde os arquivos compilados estão
        root /usr/share/nginx/html;
        index index.html;

        # Todas as rotas caem em index.html (Angular routing)
        location / {
            try_files $uri $uri/ /index.html;
        }

        # API - redireciona para backend
        location /api/ {
            proxy_pass http://backend:8080/;
        }
    }
}
```

**O que faz:**
- Nginx escuta porta 4200
- Serve arquivos estáticos (HTML, CSS, JS)
- Redireciona `/api/` para backend

---

### ✅ Teste o Dockerfile do Frontend (Opcional Agora)

Se quiser testar agora:

```powershell
cd C:\Users\marco\OneDrive\Documentos\MeusProjetos\superHome\frontend

# Build
docker build -t frontend-app .

# Espere 3-5 minutos

# Test
docker run -p 4200:4200 frontend-app

# Abra: http://localhost:4200
# Parar: Ctrl+C
```

---

## 6. Docker Compose - Tudo Junto!

### 📂 Estrutura

```
superHome/
├── backend/
│   ├── Dockerfile
│   └── src/
├── frontend/
│   ├── Dockerfile
│   ├── nginx.conf
│   └── src/
└── docker-compose.yml          👈 CRIAR NA RAIZ!
```

### 🎬 Passo 1: Criar docker-compose.yml

**Crie:** `docker-compose.yml` na pasta `superHome` (a raiz onde tá tudo)

```yaml
version: '3.8'

services:
  # Serviço 1: Backend
  backend:
    # Nome da imagem
    image: backend-app:latest
    
    # Construir a partir do Dockerfile
    build:
      context: ./backend
      dockerfile: Dockerfile
    
    # Portas: (porta da máquina):(porta do contêiner)
    ports:
      - "8080:8080"
    
    # Nome do contêiner
    container_name: backend-gestao
    
    # Variáveis de ambiente
    environment:
      SPRING_PROFILES_ACTIVE: docker
    
    # Aguarde que o backend tá pronto antes de iniciar frontend
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/bairro"]
      interval: 10s
      timeout: 5s
      retries: 5

  # Serviço 2: Frontend
  frontend:
    image: frontend-app:latest
    
    build:
      context: ./frontend
      dockerfile: Dockerfile
    
    ports:
      - "4200:4200"
    
    container_name: frontend-gestao
    
    # Frontend depende do backend estar pronto
    depends_on:
      backend:
        condition: service_healthy
    
    # Link para conversa entre contêineres
    networks:
      - app-network

  # (Opcional) Serviço 3: Banco de Dados PostgreSQL
  # postgres:
  #   image: postgres:15-alpine
  #   container_name: db-gestao
  #   environment:
  #     POSTGRES_DB: gestao_db
  #     POSTGRES_USER: admin
  #     POSTGRES_PASSWORD: senha123
  #   ports:
  #     - "5432:5432"
  #   networks:
  #     - app-network

# Rede para comunicação entre contêineres
networks:
  app-network:
    driver: bridge
```

**Explicação do YAML:**

| Campo | O que faz | Exemplo |
|-------|-----------|---------|
| `services` | Lista de contêineres | backend, frontend |
| `image` | Nome da imagem | backend-app:latest |
| `build` | Como buildar | Dockerfile path |
| `ports` | Portas mapeadas | "8080:8080" |
| `environment` | Variáveis | SPRING_PROFILES_ACTIVE |
| `depends_on` | Ordem de inicialização | frontend espera backend |
| `networks` | Rede privada | app-network |

---

### 🎬 Passo 2: Testar o docker-compose.yml

**Abra PowerShell na pasta superHome:**

```powershell
cd C:\Users\marco\OneDrive\Documentos\MeusProjetos\superHome

# Verificar que docker-compose.yml existe
dir docker-compose.yml
# Deve listar: docker-compose.yml

# Subir tudo
docker-compose up

# Aguarde... você vai ver logs dos dois serviços

# Abra em dois navegadores:
# Backend: http://localhost:8080/bairro
# Frontend: http://localhost:4200

# Parar (próximo passo tem melhor forma)
# Pressione: Ctrl+C
```

✅ **Sucesso!** Você tem Backend + Frontend rodando em Docker!

---

## 7. Testando e Rodando

### 🎬 Comandos Principais

**Subir (rodar) tudo em Background:**
```powershell
docker-compose up -d

# -d = daemon (background)
# Volta ao prompt de comando imediatamente
```

**Ver logs:**
```powershell
# Todos os serviços
docker-compose logs -f

# Só backend
docker-compose logs -f backend

# Só frontend
docker-compose logs -f frontend

# -f = follow (continua seguindo logs)
```

**Parar tudo:**
```powershell
docker-compose down

# Remove contêineres mas mantém imagens
```

**Ver contêineres rodando:**
```powershell
docker ps

# Deve listar: backend-gestao, frontend-gestao
```

**Ver imagens:**
```powershell
docker images

# Deve listar: backend-app, frontend-app
```

---

### ✅ Checklist de Funcionamento

```
Subir: docker-compose up -d
Esperar 30 segundos...

☐ Backend rodando em http://localhost:8080
  - Teste: http://localhost:8080/bairro
  - Deve retornar JSON

☐ Frontend rodando em http://localhost:4200
  - Teste: http://localhost:4200
  - Deve carregar a interface Angular

☐ Frontend consegue falar com Backend
  - Na tela do frontend, deve listar bairros
  - Se tiver bairro, sucesso! ✅

☐ Parar: docker-compose down
```

---

## 8. Troubleshooting

### ❌ Erro: "docker: command not found"

**Causa:** Docker não instalado ou não na PATH

**Solução:**
```powershell
# Verificar
docker --version

# Se não funcionar, instale Docker Desktop
# https://www.docker.com/products/docker-desktop

# Depois reinicie PowerShell
```

---

### ❌ Erro: "Port 8080 is already allocated"

**Causa:** Outro programa usando porta 8080

**Solução 1 - Parar o que está usando:**
```powershell
# Ver o que está na porta 8080
netstat -ano | findstr :8080

# Se for seu backend rodando normal (não em Docker)
# Parar ele normalmente no VS Code

# Depois:
docker-compose down
docker-compose up
```

**Solução 2 - Usar outra porta:**
```yaml
# Em docker-compose.yml, mude:
ports:
  - "8081:8080"  # Use 8081 em vez de 8080
```

---

### ❌ Erro: "Build failed" no Docker

**Causa:** Arquivo Dockerfile com erro

**Solução:**
```powershell
# Ver erro detalhado:
docker build -t debug . --verbose

# Copiar o erro e ler com calma
# Geralmente é indent (espaçamento) ou typo (erro de digitação)
```

---

### ❌ Erro: "Cannot GET /" no Frontend

**Causa:** Angular não compilou corretamente

**Solução:**
```powershell
# Verificar se nginx.conf existe no frontend
dir angulargesto-profissionais-frontend\nginx.conf

# Se não existe, cria conforme instruído acima

# Rebuild
docker-compose down
docker image rm frontend-app
docker-compose up --build
```

---

### ❌ Erro: "Connection refused" Frontend → Backend

**Causa:** Frontend não consegue falar com backend

**Solução 1 - Verificar nginx.conf:**
```nginx
# Em nginx.conf, confira:
proxy_pass http://backend:8080/;
# Deve ser "backend" (nome do service) e porta 8080
```

**Solução 2 - Verificar network:**
```powershell
docker network ls

# Deve ter: superHome_app-network
```

**Solução 3 - Testar manualmente:**
```powershell
# Entrar no contêiner frontend
docker exec -it frontend-gestao /bin/bash

# Dentro do contêiner, testar
curl http://backend:8080/bairro

# Se funcionar, é erro de configuração no nginx.conf
```

---

### ❌ Erro: "health check timeout"

**Causa:** Backend demorando para iniciar

**Solução:**
```yaml
# Em docker-compose.yml, aumente o timeout:
healthcheck:
  test: ["CMD", "curl", "-f", "http://localhost:8080/bairro"]
  interval: 15s      # Era 10s
  timeout: 10s       # Era 5s
  retries: 10        # Era 5
```

---

## 9. Fazer Manualmente no VS Code

Se você prefere aprender "na mão", esse é o fluxo mais seguro para renomear pasta sem quebrar o projeto.

### ✅ Objetivo desta seção

Renomear a pasta do backend e atualizar caminhos antigos nos arquivos de configuração e guias.

### 1. Renomear a pasta no terminal

**PowerShell:**
```powershell
cd "C:\Users\marco\OneDrive\Documentos\MeusProjetos\superHome"
Rename-Item -LiteralPath ".\camiloMarcos-Gestao_Acionamento_ProfissionalRegiao" -NewName "backend"
```

**Git Bash:**
```bash
cd ~/OneDrive/Documentos/MeusProjetos/superHome
mv camiloMarcos-Gestao_Acionamento_ProfissionalRegiao backend
```

### 2. Buscar nome antigo no VS Code

1. Abra a busca global com `Ctrl + Shift + F`.
2. Pesquise: `camiloMarcos-Gestao_Acionamento_ProfissionalRegiao`.
3. Revise os resultados antes de substituir.

### 3. Substituir com segurança

1. Clique na seta para abrir o campo **Substituir**.
2. Em substituir por, use: `backend`.
3. Faça primeiro arquivo por arquivo (mais seguro para iniciantes).
4. Depois, se estiver confortável, use **Substituir tudo**.

### 4. Pontos obrigatórios para conferir

- `docker-compose.yml`:
  - backend `build.context` deve ser `./backend`
  - frontend `build.context` deve ser `./frontend`
- `frontend/Dockerfile`:
  - cópia do build Angular deve apontar para `dist/frontend/browser`

### 5. Validar em 60 segundos

```powershell
cd "C:\Users\marco\OneDrive\Documentos\MeusProjetos\superHome"
docker-compose config
docker-compose up --build
```

Teste no navegador:

- `http://localhost:8080/bairro`
- `http://localhost:4200`

Se os dois abrirem, está correto e organizado.

---

## 📊 Resumo Visual

### Seu Projeto Agora:

```
┌─────────────────── DOCKER ───────────────────┐
│                                              │
│  ┌──────────────────┐   ┌──────────────────┐ │
│  │   BACKEND        │   │   FRONTEND       │ │
│  │ Spring Boot      │   │ Angular + Nginx  │ │
│  │ Porta 8080       │   │ Porta 4200       │ │
│  │ Java 17          │   │ Node.js 20       │ │
│  │ H2 Database      │   │ npm              │ │
│  └──────────────────┘   └──────────────────┘ │
│         ↑                        ↑            │
│         └────── app-network ─────┘            │
│                                              │
└──────────────────────────────────────────────┘
```

---

## 🎯 Próximas Ações Recomendadas

### Nível 1 (Agora):
- ✅ Subir com `docker-compose up`
- ✅ Testar tudo funciona
- ✅ Parar com `docker-compose down`

### Nível 2 (Depois):
- [ ] Banco PostgreSQL vs H2
- [ ] Variáveis de ambiente por profile
- [ ] Docker Hub (publicar imagens)
- [ ] CI/CD (GitHub Actions)

### Nível 3 (Produção):
- [ ] Kubernetes
- [ ] Load Balancing
- [ ] Monitoring
- [ ] Logs centralizados

---

## 💡 Dicas Importantes

### 1. **Sempre Compile Antes**
```powershell
# Backend precisa estar compilado
mvnw clean package -DskipTests

# Frontend também (ou deixa Docker fazer)
```

### 2. **Limpar para Recomeçar**
```powershell
# Parar tudo
docker-compose down

# Remover imagens antigas
docker image prune

# Limpar volumes não usados
docker volume prune

# Começar do zero
docker-compose up --build
```

### 3. **Ver o que Tá Acontecendo**
```powershell
# Logs em tempo real
docker-compose logs -f

# Entrar dentro do contêiner
docker exec -it backend-gestao /bin/bash
docker exec -it frontend-gestao /bin/bash

# Sair do contêiner
exit
```

### 4. **Debug com Variáveis**
```yaml
# Em docker-compose.yml, adicione:
environment:
  DEBUG: "true"
  SPRING_JPA_SHOW_SQL: "true"
```

---

## 🎉 Checklist Final

### Arquivos que Você Criou:

- ✅ `backend/Dockerfile`
- ✅ `frontend/Dockerfile`
- ✅ `frontend/nginx.conf`
- ✅ `superHome/docker-compose.yml`

### Testes Completados:

- ✅ Backend em Docker (teste individual)
- ✅ Frontend em Docker (teste individual)
- ✅ Docker Compose (tudo junto)
- ✅ Frontend consegue falar com Backend

### Pronto Para:

- ✅ Desenvolver com Docker
- ✅ Compartilhar projeto com outros
- ✅ Deploy em servidor

---

**Parabéns!** Você conseguiu containerizar seu projeto! 🚀

Agora quando alguém disser "na minha máquina funciona", você responde:
**"Funciona em Docker!"** 🐳

---

**Dúvidas?** Vá para a seção **Troubleshooting** acima. Provavelmente sua resposta está lá.
