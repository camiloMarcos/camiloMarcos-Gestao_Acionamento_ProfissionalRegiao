# Comandos para rodar a aplicacao

## Docker

```powershell
cd C:\Users\marco\OneDrive\Documentos\MeusProjetos\superHome
docker compose build
docker compose up -d
```

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
