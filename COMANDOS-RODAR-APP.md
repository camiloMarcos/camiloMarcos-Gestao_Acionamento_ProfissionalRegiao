# Comandos para rodar a aplicacao

## Docker

```powershell
cd C:\Users\marco\OneDrive\Documentos\MeusProjetos\superHome
docker compose build
docker compose up -d
```

Depois que subir, acesse:

- Frontend: http://localhost:4200
- Backend: http://localhost:8080

Observação: o comando `docker compose up -d` sobe os containers em segundo plano, então o terminal não mostra automaticamente o link do localhost.

```powershell
docker compose logs -f backend
docker compose logs -f frontend
```

```powershell
docker compose down
```

## Local (sem Docker)

### Backend

```powershell
cd C:\Users\marco\OneDrive\Documentos\MeusProjetos\superHome
.\mvnw.cmd spring-boot:run
```

### Frontend

```powershell
cd C:\Users\marco\OneDrive\Documentos\MeusProjetos\superHome\frontend
npm install
npm start
```
