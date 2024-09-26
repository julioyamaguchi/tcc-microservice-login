# Projeto de Autenticação com Spring Boot

## Descrição
Este projeto implementa um sistema de autenticação utilizando Spring Boot, JWT (JSON Web Tokens) e PostgreSQL. Ele inclui um serviço básico de login e proteção de rotas com autenticação baseada em tokens.

## Requisitos

Certifique-se de ter as seguintes ferramentas instaladas no seu sistema:
- **JDK 17** ou superior.
- **Maven** (para gerenciar as dependências e compilar o projeto).
- **PostgreSQL** (para o banco de dados).

## Instalação

Siga os passos abaixo para instalar todas as dependências do projeto e configurá-lo localmente:

1. **Clone o repositório:**
    ```bash
     git clone https://github.com/julioyamaguchi/tcc-microservice-login.git
    cd seu-repositorio
    ```

2. **Configure o banco de dados:**

   Certifique-se de ter o PostgreSQL rodando e crie o banco de dados necessário.
   O script de criação da tabela esta na pasta login

   No arquivo `application.properties`, configure os detalhes da conexão com o banco de dados:

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/seu-banco
   spring.datasource.username=seu-usuario
   spring.datasource.password=sua-senha
   spring.jpa.hibernate.ddl-auto=update

3. **Rode o projeto spring utilizando o spring boot dashboard (web2)**
   dentro do vscode digite:
   ctrl + alt + p

   pesquise por: view: Show spring boot dashboard
