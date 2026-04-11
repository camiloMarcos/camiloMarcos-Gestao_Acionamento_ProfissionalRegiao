# 🐳 Docker - Passo-a-Passo Executado (Simples!)

**Objetivo:** Suba Backend + Frontend em Docker em 5 minutos

---

## ✅ PRÉ-REQUISITO

Você tem Docker instalado?

```powershell
docker --version
```

Se não funcionar, instale: https://www.docker.com/products/docker-desktop

---

## 🚀 Passo 1: Compilar o Backend (3 min)

**Abra PowerShell e execute:**

```powershell
cd C:\Users\marco\OneDrive\Documentos\MeusProjetos\superHome\backend

mvnw clean package -DskipTests
```

**⏳ Aguarde terminar...**

```
Você vai ver muitas linhas. Está tudo bem:
[INFO] BUILD SUCCESS
```

✅ **Pronto!** Backend compilado.

---

## 🐳 Passo 2: Subir Docker (2 min)

**Volta para a pasta principal:**

```powershell
cd C:\Users\marco\OneDrive\Documentos\MeusProjetos\superHome
```

**Execute:**

```powershell
docker-compose up
```

**⏳ Aguarde... você vai ver:**

```
backend-gestao  | . . . . .
backend-gestao  | . . . . application started
frontend-gestao | . . . . ready
```

✅ **Tudo rodando!**

---

## ✨ Passo 3: Testar (1 min)

**Abra navegador em dois abas:**

1️⃣ **Aba 1 - Backend:**
```
http://localhost:8080/bairro
```
Deve retornar um JSON com bairros

2️⃣ **Aba 2 - Frontend:**
```
http://localhost:4200
```
Deve mostrar a interface Angular

✅ **Funciona?** Perfeito! 🎉

---

## ⏹️ Passo 4: Parar (1 seg)

**No PowerShell onde está rodando:**

```
Pressione: Ctrl + C
```

**Pronto!** Tudo parado.

---

## 🔁 Próxima Vez

Quando quiser rodar de novo é só:

```powershell
cd C:\Users\marco\OneDrive\Documentos\MeusProjetos\superHome
docker-compose up
```

**5 segundos. Sem compilar de novo.**

---

## 🆘 Se Tiver Erro

### ❌ "Port 8080 is already allocated"

```powershell
# Parar tudo
docker-compose down

# Tentar de novo
docker-compose up
```

### ❌ Vê erro de conexão no frontend

1. Aguarde 30 segundos (backend pode estar lento)
2. Atualize a página (F5)
3. Se ainda não funcionar, veja o Troubleshooting do GUIA-DOCKER-DO-ZERO.md

### ❌ BUILD FAILED no backend

```powershell
# Tentar de novo (pode ter sido erro temporário)
mvnw clean package -DskipTests
```

---

## 📋 Checklist

- ✅ Docker instalado
- ✅ Backend compilado (mvnw clean package)
- ✅ docker-compose up rodando
- ✅ http://localhost:8080/bairro funciona
- ✅ http://localhost:4200 funciona
- ✅ Frontend consegue listar bairros

**Se tudo ✅, você conseguiu!** 🚀

---

## 📂 Arquivos que Você Tem

```
superHome/
├── docker-compose.yml          ✅ (já criei)
├── GUIA-DOCKER-DO-ZERO.md      ✅ (teoria completa)
├── DOCKER-COMANDOS-RAPIDOS.md  ✅ (referência rápida)
│
├── camiloMarcos-Gestao.../
│   ├── Dockerfile               ✅ (já criei)
│   ├── .dockerignore            ✅ (já criei)
│   └── src/
│
└── frontend/
    ├── Dockerfile               ✅ (já criei)
    ├── nginx.conf               ✅ (já criei)
    ├── .dockerignore            ✅ (já criei)
    └── src/
```

**Tudo já está pronto para usar!**

---

## 🎓 Quer Entender Mais?

Leia: [GUIA-DOCKER-DO-ZERO.md](./GUIA-DOCKER-DO-ZERO.md)

Lá tem explicação completa de cada arquivo e conceito.

---

**Boa sorte!** 🐳🚀
