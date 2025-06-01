
# Autenticação - Microserviço de Login

Este repositório contém o microserviço responsável pela autenticação de usuários do Sistema de Formação e Gestão de Grupos de TCC, desenvolvido como parte do Trabalho de Conclusão de Curso de Tecnologia em Análise e Desenvolvimento de Sistemas - UFPR 2025/01.

O sistema utiliza autenticação baseada em JWT (JSON Web Token) e permite o registro, login e proteção de rotas por meio de tokens.

## Tecnologias Utilizadas

- Java 17 + Spring Boot – Backend de autenticação
- Spring Security – Controle de autenticação e autorização
- JWT – Geração e validação de tokens
- PostgreSQL – Banco de dados relacional

## Requisitos

Certifique-se de ter as seguintes ferramentas instaladas:

- JDK 17 ou superior
- Maven
- PostgreSQL

## Configuração e Execução

### 1. Clonar o repositório

```
git clone https://github.com/julioyamaguchi/tcc-microservice-login.git
cd tcc-microservice-login
```

### 2. Criar o banco de dados

Certifique-se de que o PostgreSQL esteja em execução e crie o banco necessário:

```
CREATE DATABASE seu_banco;
```

### 3. Configurar as credenciais do banco

No arquivo `src/main/resources/application.properties`, atualize os dados de conexão:

```
spring.datasource.url=jdbc:postgresql://localhost:5432/seu_banco
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=update
```

> O script de criação da tabela pode ser encontrado no seguinte caminho `tcc-microservice-login\script_criação_tabela`.

### 4. Iniciar o projeto

Você pode iniciar o microserviço diretamente pela IDE utilizando o Spring Boot Dashboard.

Na IDE que estiver utilizando, pressione:

```
Ctrl + Alt + P
```

E pesquise por:

```
View: Show Spring Boot Dashboard
```

A partir dessa visualização, selecione o projeto e clique em "Start".
