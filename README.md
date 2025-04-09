# Moliceiro Meals

Aplicação Spring Boot para gerenciamento de refeições e reservas em restaurantes.

## Requisitos

- Docker
- Docker Compose

## Configuração

1. Clone o repositório:
```bash
git clone <repository-url>
cd moliceiro-meals
```

2. Configure as variáveis de ambiente:
Crie um arquivo `.env` na raiz do projeto com as seguintes variáveis:
```
WEATHER_API_KEY=sua_chave_api
```

## Executando a aplicação

1. Construa e inicie os containers:
```bash
docker-compose up --build
```

2. A aplicação estará disponível em:
- Aplicação: http://localhost:8080
- Banco de dados PostgreSQL: localhost:5432

## Parando a aplicação

Para parar os containers:
```bash
docker-compose down
```

Para parar os containers e remover os volumes:
```bash
docker-compose down -v
```

## Estrutura do projeto

- `src/main/java`: Código fonte da aplicação
- `src/test`: Testes
- `src/main/resources`: Arquivos de configuração
- `docker-compose.yml`: Configuração do Docker Compose
- `Dockerfile`: Configuração do container da aplicação

## Tecnologias utilizadas

- Spring Boot
- PostgreSQL
- Docker
- Docker Compose
- Maven
- JUnit
- Selenium
- TestContainers
