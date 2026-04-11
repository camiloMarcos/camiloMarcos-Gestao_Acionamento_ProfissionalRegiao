# 🚀 Docker - Comandos Rápidos (Referência)

## 📋 Antes de Começar

**Verificar Docker instalado:**
```powershell
docker --version
docker-compose --version
```

---

## ✅ Seu Fluxo de Trabalho

### 1️⃣ Compilar o Backend (UMA VEZ)

```powershell
cd C:\Users\marco\OneDrive\Documentos\MeusProjetos\superHome

\.\mvnw.cmd clean package -DskipTests

# Aguarde... BUILD SUCCESS ✅
```

---

### 2️⃣ Subir Tudo com Docker

```powershell
cd C:\Users\marco\OneDrive\Documentos\MeusProjetos\superHome

# Subir em background
docker-compose up -d

# OU subir vendo logs (mais fácil para ver erros)
docker-compose up

# Pressione Ctrl+C para parar
```

---

### 3️⃣ Testar

```
Backend: http://localhost:8080/bairro
Frontend: http://localhost:4200
```

---

## 🔧 Comandos Úteis

### Ver o que tá rodando
```powershell
# Contêineres rodando
docker ps

# Todas as imagens
docker images
```

### Ver logs
```powershell
# Todos os logs
docker-compose logs -f

# Só backend
docker-compose logs -f backend

# Só frontend
docker-compose logs -f frontend

# Parar (Ctrl+C)
```

### Entrar dentro do contêiner
```powershell
# No backend
docker exec -it backend-gestao /bin/bash

# No frontend
docker exec -it frontend-gestao /bin/bash

# Sair
exit
```

### Parar tudo
```powershell
docker-compose down

# Remove TUDO (contêineres, volumes, etc)
docker-compose down -v
```

---

## 🧹 Limpar Tudo (Recomeçar do Zero)

```powershell
# Parar tudo
docker-compose down -v

# Remover imagens
docker image rm backend-app frontend-app

# Limpar lixo
docker system prune

# Começar do zero
docker-compose up --build
```

---

## ❌ Se Tiver Erro

### Porta já em uso
```powershell
# Parar o que está usando
docker-compose down

# Ou usar outra porta em docker-compose.yml
# Mude: "8080:8080" para "8081:8080"
```

### Ver erro detalhado
```powershell
# Build com detalhe
docker-compose up --build

# Veja a mensagem de erro com calma
```

### Resetar completamente
```powershell
docker-compose down -v
docker image prune -a
docker-compose up --build
```

---

## 📊 Fluxo de Desenvolvimento

```
┌─────────────────────────────────────────────┐
│  Primeira Vez (Setup Inicial)               │
├─────────────────────────────────────────────┤
│ 1. mvnw clean package -DskipTests           │
│ 2. docker-compose up --build                │
│ 3. Testar tudo                              │
└─────────────────────────────────────────────┘
               ↓
┌─────────────────────────────────────────────┐
│  Próximas Vezes (Desenvolvimento)           │
├─────────────────────────────────────────────┤
│ 1. Editar código                            │
│ 2. docker-compose down                      │
│ 3. docker-compose up --build                │
│ 4. Testar                                   │
└─────────────────────────────────────────────┘
```

---

## 💡 Dica Pro

Se quiser que Docker recompile automático quando você muda código:

```yaml
# Em docker-compose.yml, adicione:
services:
  backend:
    # ... resto da config
    volumes:
      - ./backend/src:/app/src
```

Mas deixa isso para depois. Primeiro aprende o básico!

---

## 🎯 O Mínimo para Começar

```powershell
# 1. Compilar 
mvnw clean package -DskipTests

# 2. Subir
cd .. & docker-compose up

# 3. Abrir navegador
# http://localhost:8080/bairro
# http://localhost:4200

# 4. Parar
# Ctrl+C
```

---

**Salve esse arquivo como referência!**
