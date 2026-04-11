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
COPY --from=builder /app/dist/gestao-profissionais-frontend /usr/share/nginx/html

# Copiar configuração nginx
COPY nginx.conf /etc/nginx/nginx.conf

# Porta 4200
EXPOSE 4200

# Iniciar nginx
CMD ["nginx", "-g", "daemon off;"]
