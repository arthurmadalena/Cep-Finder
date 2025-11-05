# CepFinder - Sistema de Gerenciamento de CEPs

---

## AVISO IMPORTANTE - Configuração e Execução

### Passo 1: Criar o Banco de Dados

Antes de executar a aplicação, crie o banco de dados PostgreSQL:

```sql
-- Conecte-se ao PostgreSQL e execute:
CREATE DATABASE cepmanager;
```

**Comandos no terminal PostgreSQL**:

```bash
# Windows (psql)
psql -U postgres
CREATE DATABASE cepmanager;
\q

# Ou via pgAdmin: 
# Clique direito em "Databases" > "Create" > "Database"
# Nome: cepmanager
```

### Passo 2: Executar a Aplicação

Se o frontend **não abrir** ao executar pelo IntelliJ IDEA, siga estes passos:

#### Opção 1: Via Maven (Terminal)

```bash
# 1. Compilar o projeto completo
cd C:\Seu-diretorio\Cep-Finder
mvn clean compile

# 2. Entrar no módulo webapp
cd webapp

# 3. Executar a aplicação
mvn spring-boot:run
```

#### Opção 2: Via IntelliJ IDEA

1. Abra o terminal integrado do IntelliJ (Alt + F12)
2. Execute os comandos acima
3. Aguarde a mensagem "Started CepManagerApplication"
4. Acesse: http://localhost:8080/cep-manager/index.xhtml

**Nota**: O projeto usa arquitetura multi-módulo Maven. Sempre compile da raiz primeiro!

**Importante**: O Liquibase criará automaticamente todas as tabelas e dados iniciais no primeiro startup.

---

## Acesso Rapido

### URLs da Aplicacao

- **Frontend JSF**: (http://localhost:8080/cep-manager/index.xhtml)
- **API REST**: (http://localhost:8080/cep-manager/api)
- **Documentacao Swagger**: (http://localhost:8080/cep-manager/swagger-ui/index.html)

### Credenciais de Acesso

- **Administrador**: `admin` / `admin123` (Permissoes: ROLE_ADMIN, ROLE_USER)
- **Usuario Padrao**: `user` / `admin123` (Permissoes: ROLE_USER)

**Observacao**: Os usuarios padrao sao criados automaticamente pelo Liquibase (changeset-007) com `email_verificado = true` para uso imediato.

### Colecao Postman

Para testar a API via Postman, importe o arquivo:

**Arquivo**: `CepFinder-API.postman_collection.json`

**Como importar**:
1. Abra o Postman
2. Clique em "Import" no canto superior esquerdo
3. Selecione o arquivo `CepFinder-API.postman_collection.json` da raiz do projeto
4. A colecao sera importada com 23 requisicoes prontas para uso
5. Execute primeiro "1. Autenticacao > Login - Admin" para obter o token JWT
6. O token sera capturado automaticamente e usado nas demais requisicoes

---

## Visão Geral

O CepFinder é uma aplicação completa de gerenciamento de CEPs (Código de Endereçamento Postal) desenvolvida com Spring Boot 3 e Java 17. O sistema oferece uma API RESTful robusta para operações CRUD, além de uma interface web completa desenvolvida com JSF e PrimeFaces.

---

## Tecnologias Utilizadas

### Backend: 

- **Java 17**: Escolhido por ser uma versão LTS (Long Term Support) com suporte estendido e recursos modernos da linguagem
- **Spring Boot 3.2.0**: Framework principal que simplifica a configuração e desenvolvimento de aplicações enterprise
- **Spring Data JPA**: Abstração de acesso a dados que reduz código boilerplate e facilita operações com banco de dados
- **PostgreSQL**: Banco de dados relacional robusto e open-source, ideal para aplicações em produção
- **Liquibase**: Ferramenta de versionamento de banco de dados que garante consistência entre ambientes
- **Hibernate**: ORM (Object-Relational Mapping) que gerencia a persistência de objetos Java
- **Spring Security**: Framework de segurança que protege os endpoints da aplicação
- **OAuth2 Resource Server**: Implementação moderna de JWT usando Spring Security OAuth2
- **Nimbus JOSE + JWT**: Biblioteca para codificação e decodificação de tokens JWT
- **Spring Mail**: Envio de emails transacionais (verificação, notificações)
- **SpringDoc OpenAPI (Swagger)**: Geração automática de documentação interativa da API
- **Lombok**: Biblioteca que reduz código boilerplate através de anotações
- **Maven**: Gerenciador de dependências e build tool

### Frontend:

- **JSF (Jakarta Server Faces) 4.0**: Framework MVC para desenvolvimento de interfaces web baseadas em componentes
- **PrimeFaces 13.0**: Biblioteca de componentes UI ricos para JSF
- **JoinFaces**: Integração do JSF com Spring Boot
- **XHTML/CSS**: Estruturação e estilização das páginas

### Testes
- **JUnit 5**: Framework de testes unitários
- **Mockito**: Framework para criação de mocks em testes
- **AssertJ**: Biblioteca de assertions fluentes
- **Spring Test**: Suporte para testes de integração
- **MockMvc**: Testes de controllers REST
- **H2 Database**: Banco de dados em memória para testes

---

### Padrão Arquitetural: Multi-Module Maven Project

O projeto foi estruturado em módulos Maven para separar responsabilidades e facilitar manutenção:

```
CepFinder/
├── core/                 # Módulo de domínio e lógica de negócio
│   ├── entity/          # Entidades JPA (Cep, Usuario)
│   ├── dto/             # Data Transfer Objects
│   ├── dao/             # Repositórios (CepDAO, UsuarioDAO)
│   ├── service/         # Camada de serviço e lógica de negócio
│   ├── mapper/          # Conversores Entity <-> DTO
│   └── exception/       # Exceções personalizadas
│
└── webapp/              # Módulo de apresentação
    ├── rest/            # Controllers REST (Cep, Usuario, Auth)
    ├── beans/           # Managed Beans JSF (CepBean, UsuarioBean, LoginBean, RegistroBean)
    ├── security/        # Segurança (SecurityConfig, UserDetailsServiceImpl, UserDetailsImpl)
    ├── service/         # Serviços aplicação (AuthenticationService, JwtService, EmailService)
    ├── properties/      # Configurações (JwtProperties)
    ├── converter/       # Conversores JSF
    └── resources/       # Recursos estáticos (CSS, imagens)
```

### Por que Multi-Module?

1. **Separação de Responsabilidades**: O módulo `core` contém toda a lógica de negócio e pode ser reutilizado em outros projetos
2. **Testabilidade**: Facilita a criação de testes isolados para cada camada
3. **Manutenibilidade**: Mudanças em uma camada não afetam diretamente outras
4. **Reusabilidade**: O módulo core pode ser usado como dependência em outros projetos

### Camadas da Aplicação
#### 1. Camada de Persistência (DAO)

**Responsabilidade**: Acesso direto ao banco de dados

**Tecnologia**: Spring Data JPA com queries JPQL e nativas

**Justificativa**: As queries estão explícitas através de `@Query`, permitindo visualizar e otimizar o SQL gerado. Isso atende ao requisito de "SQL visível".

```java
@Query("SELECT c FROM Cep c WHERE c.codigo = :codigo")
Optional<Cep> findByCodigo(@Param("codigo") String codigo);
```

#### 2. Camada de Serviço (Service)

**Responsabilidade**: Lógica de negócio e orquestração

**Padrão**: Interface + Implementação

**Justificativa**: Permite múltiplas implementações e facilita testes com mocks.

```java
public interface CepService {
    CepDTO findByCodigo(String codigo);
}

@Service
public class CepServiceImpl implements CepService {
    // Implementação
}
```

#### 3. Camada de Apresentação

**REST API**: Controllers REST para consumo externo

**JSF/PrimeFaces**: Interface web para usuário

**Justificativa**: Oferece duas formas de interação com o sistema - API para integração e UI para uso direto.

---

## Estrutura do Projeto

### Módulo Core

```
core/
├── src/main/java/
│   └── br/com/arthur/madalena/cepmanager/
│       ├── entity/
│       │   └── Cep.java                    # Entidade JPA
│       ├── dto/
│       │   └── CepDTO.java                 # DTO para transferência
│       ├── dao/
│       │   └── CepDAO.java                 # Repository com queries
│       ├── service/
│       │   ├── CepService.java             # Interface do serviço
│       │   └── CepServiceImpl.java         # Implementação
│       ├── mapper/
│       │   └── CepMapper.java              # Entity <-> DTO
│       └── exception/
│           ├── BusinessException.java
│           └── ResourceNotFoundException.java
│
├── src/main/resources/
│   ├── liquibase/
│   │   ├── changelog-master.xml
│   │   ├── changeset-001-create-table-cep.xml
│   │   └── changeset-002-insert-sample-data.xml
│   └── application.properties
│
└── src/test/java/
    └── br/com/arthur/madalena/cepmanager/
        ├── dao/
        │   └── CepDAOTest.java             # 15 testes
        └── service/
            └── CepServiceImplTest.java     # Testes de serviço
```

### Módulo Webapp

```
webapp/
├── src/main/java/
│   └── br/com/arthur/madalena/cepmanager/
│       ├── CepManagerApplication.java      # Classe principal
│       ├── rest/
│       │   ├── CepRestController.java      # Endpoints REST de CEPs
│       │   ├── UsuarioRestController.java  # Endpoints REST de Usuarios
│       │   ├── RegistroRestController.java # Registro e verificacao de email
│       │   ├── AuthRestController.java     # Autenticação e geração JWT
│       │   └── GlobalExceptionHandler.java # Tratamento global de erros (401/403/400/500)
│       ├── beans/           # Managed Beans JSF
│       │   ├── CepBean.java                # Gerenciamento de CEPs
│       │   ├── UsuarioBean.java            # Gerenciamento de Usuarios
│       │   ├── LoginBean.java              # Autenticação JSF
│       │   ├── RegistroBean.java           # Registro de conta
│       │   └── VerificacaoBean.java        # Verificação de email
│       ├── security/
│       │   ├── SecurityConfig.java         # Configuração Spring Security
│       │   ├── UserDetailsServiceImpl.java # Carregamento de usuários
│       │   ├── UserDetailsImpl.java        # Implementação UserDetails
│       │   └── UsuarioAtivoFilter.java     # Validação de usuário ativo/email verificado
│       ├── service/         # Serviços de aplicação
│       │   ├── AuthenticationService.java  # Lógica de autenticação
│       │   ├── JwtService.java             # Geração/decodificação JWT
│       │   └── EmailService.java           # Envio de emails
│       ├── converter/
│       │   └── CepConverter.java           # Formatação de CEP
│       ├── properties/
│       │   └── JwtProperties.java          # Configurações JWT
│       └── config/
│           └── OpenApiConfig.java          # Configuração Swagger
│
├── src/main/webapp/
│   ├── index.xhtml                         # Redirecionamento inicial
│   ├── login.xhtml                         # Tela de login
│   ├── templates/
│   │   └── base.xhtml                      # Template base
│   ├── pages/
│   │   ├── dashboard.xhtml                 # Dashboard com estatísticas
│   │   ├── registro.xhtml                  # Formulário de registro de conta
│   │   ├── verificar-email.xhtml           # Página de verificação de email
│   │   ├── cep/
│   │   │   ├── consulta.xhtml              # Listagem e busca de CEPs
│   │   │   ├── cadastro.xhtml              # Formulário de cadastro de CEP
│   │   │   └── visualizar.xhtml            # Visualização/Edição de CEP
│   │   └── usuario/
│   │       ├── consulta.xhtml              # Listagem de usuários (ADMIN)
│   │       └── visualizar.xhtml            # Gerenciar permissões (ADMIN)
│   └── resources/
│       ├── css/
│       │   └── custom.css                  # Estilos customizados
│       └── images/
│           ├── iconCep.png                 # Logo da aplicação
│           └── cidade.webp                 # Background login
│
└── src/test/java/
    └── br/com/arthur/madalena/cepmanager/
        └── rest/
            ├── CepRestControllerTest.java
            └── CepRestControllerIntegrationTest.java  # 17 testes
```

---

## Funcionalidades

### API REST

#### Autenticação e Registro

- **POST** `/api/auth/login`: Autenticação e geração de token JWT
  - Usuários disponíveis: `admin/admin123` e `user/user123`
- **POST** `/api/usuarios/registro`: Criar nova conta de usuário
- **GET** `/api/usuarios/verificar-email?token={token}`: Ativar conta via email
- **POST** `/api/usuarios/reenviar-verificacao`: Reenviar email de verificação

#### Gerenciamento de Usuários (ADMIN)

- **GET** `/api/usuarios`: Lista todos os usuários (paginado)
- **GET** `/api/usuarios/{id}`: Busca usuário por ID
- **GET** `/api/usuarios/username/{username}`: Busca por username
- **PUT** `/api/usuarios/{id}`: Atualiza dados do usuário
- **POST** `/api/usuarios/{id}/permissoes`: Adiciona permissão
- **DELETE** `/api/usuarios/{id}/permissoes/{permissao}`: Remove permissão
- **PATCH** `/api/usuarios/{id}/ativo`: Ativa/desativa usuário
- **POST** `/api/usuarios/resetar-senha`: Reseta senha e envia por email

#### Operações CRUD de CEPs

- **GET** `/api/ceps`: Lista todos os CEPs (paginado)
- **GET** `/api/ceps/{codigo}`: Busca CEP por código
- **GET** `/api/ceps/id/{id}`: Busca CEP por ID
- **POST** `/api/ceps`: Cria novo CEP
- **PUT** `/api/ceps`: Atualiza CEP existente
- **DELETE** `/api/ceps/{codigo}`: Remove CEP

#### Buscas Especializadas

- **GET** `/api/ceps/logradouro/{logradouro}`: Busca por nome da rua
- **GET** `/api/ceps/cidade/{cidade}`: Busca por cidade
- **GET** `/api/ceps/uf/{uf}`: Busca por estado
- **GET** `/api/ceps/search/{termo}`: Pesquisa geral em todos os campos

### Interface Web (JSF)

#### Login
- Autenticação com banco de dados
- Validação de credenciais (usuário inativo, email não verificado)
- Mensagens de erro específicas para cada situação
- Redirecionamento automático para dashboard
- Link para criação de nova conta

#### Registro de Conta
- Formulário de criação de conta
- Validação de email e confirmação de senha
- Feedback visual após registro
- Opção de reenviar email de verificação
- Instruções claras sobre o processo de ativação

#### Dashboard
- Total de CEPs cadastrados
- Número de cidades
- Número de estados
- Gráfico de distribuição por UF

#### Consulta de CEPs
- Filtros múltiplos (CEP, Logradouro, Cidade, Bairro, UF)
- Pesquisa geral (busca em todos os campos)
- Listagem paginada
- Link direto para visualização

#### Cadastro/Edição de CEP
- Formulário completo de CEP
- Validação de campos obrigatórios
- Máscaras de entrada (CEP formatado com traço: 00000-000)
- Suporta entrada com ou sem traço (remove automaticamente antes de salvar)
- Validação via Bean Validation (`@Pattern`) que aceita ambos os formatos
- Redirecionamento após salvar

#### Visualização de CEPs
- Exibição de todos os dados do CEP
- Modo visualização/edição
- Opção de exclusão com confirmação
- Data de cadastro e última alteração

#### Gerenciar Usuários (ADMIN)
- Listagem de todos os usuários cadastrados
- Filtros por nome/username e status (ativo/inativo)
- Visualização de permissões de cada usuário
- Indicador visual de email verificado
- Ações rápidas: ativar/desativar, resetar senha
- Link direto para tela de gerenciamento de permissões

#### Gerenciar Permissões (ADMIN)
- Visualização detalhada do usuário
- Lista de permissões atribuídas com descrição completa de cada role
- Adicionar novas permissões via dropdown
- Remover permissões existentes com confirmação
- Proteção contra remoção da última permissão (ROLE_USER)
- Mensagem informativa quando todas as permissões já estão atribuídas
- Edição de dados do usuário (nome, email)
- Salvamento com redirecionamento
- Alinhamento visual entre status e email verificado

### Sistema de Gerenciamento de Usuários (Resumo)

#### Registro de Usuários
- Tela de criação de conta pública
- Validação de email único e username único
- Validação de confirmação de senha
- Envio automático de email de verificação
- Link de verificação com expiração de 24 horas

#### Autenticação e Autorização
- Login com validação no banco de dados
- Geração de token JWT com OAuth2 Resource Server
- Verificação de email obrigatória para acesso
- Controle de usuários ativos/inativos
- Sistema de permissões baseado em roles

#### Gerenciamento de Usuários (ADMIN)
- Listagem de todos os usuários do sistema
- Filtros por nome, username e status
- Visualização de permissões atribuídas
- Adicionar/remover permissões (ROLE_USER, ROLE_ADMIN, ROLE_GERENTE)
- Ativar/desativar usuários
- Resetar senha de usuários (envia nova senha por email)
- Proteção contra desativação do admin principal

#### Permissões Disponíveis
- **ROLE_USER**: Usuário Padrão - Consultar CEPs e visualizar informações básicas
- **ROLE_GERENTE**: Gerente de CEPs - Gerenciar e editar CEPs, visualizar relatórios
- **ROLE_ADMIN**: Administrador do Sistema - Acesso total: gerenciar usuários, CEPs e todas as funcionalidades

#### Sistema de Email
- Email de verificação de conta
- Email de boas-vindas após ativação
- Email de reset de senha
- Email de alteração de permissões
- Reenvio de email de verificação

---

## Configuração e Execução

### Pré-requisitos

- Java JDK 17 ou superior
- Maven 3.6+
- PostgreSQL 12+
- (Opcional) IDE com suporte a Maven (IntelliJ IDEA, Eclipse, VSCode)

### Configuração do Banco de Dados

1. Crie um banco de dados PostgreSQL:

```sql
CREATE DATABASE cepfinder;
CREATE USER cepfinder_user WITH PASSWORD 'cepfinder123';
GRANT ALL PRIVILEGES ON DATABASE cepfinder TO cepfinder_user;
```

2. Configure o arquivo `webapp/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/cepfinder
spring.datasource.username=cepfinder_user
spring.datasource.password=cepfinder123
```

### Compilação

```bash
# Na raiz do projeto
mvn clean install
```

### Execução

```bash
# Navegar para o módulo webapp
cd webapp

# Executar a aplicação
mvn spring-boot:run
```

A aplicação estará disponível conforme descrito na seção "Acesso Rapido" no inicio deste documento.

### Configuração de Email (Opcional)

Para habilitar o envio de emails (verificação de conta, reset de senha):

1. Configure um servidor SMTP no `application.properties`:

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=seu-email@gmail.com
spring.mail.password=sua-senha-app
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

2. Para Gmail, gere uma senha de app em: https://myaccount.google.com/apppasswords

**Nota**: O sistema funciona sem configuração de email, mas os usuários criados via registro precisarão ser ativados manualmente pelo admin.

---

## Documentação da API

Todas as informacoes sobre acesso a API, Swagger e Postman estao disponiveis na secao "Acesso Rapido" no inicio deste documento.

---

## Testes

### Cobertura de Testes

O projeto possui uma suíte completa de **193 testes unitários** cobrindo todas as camadas da aplicação:

#### Módulo Core (145 testes)
- **DAOs**: 35 testes (CepDAO: 13 | UsuarioDAO: 22)
- **DTOs**: 38 testes (CepDTO: 17 | RegistroUsuarioDTO: 8 | UsuarioDTO: 13)
- **Mappers**: 21 testes (CepMapper: 12 | UsuarioMapper: 9)
- **Services**: 51 testes (CepService: 11 | EmailService: 10 | UsuarioService: 30)

#### Módulo WebApp (48 testes)
- **Converters**: 12 testes (CepConverter: 12)
- **Security**: 24 testes (UserDetailsImpl: 14 | UserDetailsService: 10)
- **Services**: 12 testes (AuthenticationService: 7 | JwtService: 5)

### Como Executar os Testes

```bash
# Executar todos os testes (193 testes)
cd C:\Seu-diretorio\Cep-Finder
mvn test

# Executar testes de um módulo específico
cd core
mvn test

# Executar uma classe de teste específica
mvn test -Dtest=CepServiceImplTest

# Executar testes com logs detalhados
mvn test -X

# Pular testes durante compilação
mvn clean install -DskipTests
```

### Relatório de Cobertura

Para gerar relatório de cobertura de código:

```bash
# Gerar relatório
mvn clean test jacoco:report

# Visualizar relatório
# Abra: core/target/site/jacoco/index.html
# Abra: webapp/target/site/jacoco/index.html
```

### Resultados dos Testes

```
[INFO] Reactor Summary for CEP Manager - Parent 1.0.0:
[INFO]
[INFO] CEP Manager - Parent ............................... SUCCESS
[INFO] CEP Manager - Core ................................. SUCCESS (145 testes)
[INFO] CEP Manager - WebApp ............................... SUCCESS (48 testes)
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Tests run: 193, Failures: 0, Errors: 0, Skipped: 0
```

### Detalhamento dos Testes

#### 1. Testes de DAO (35 testes)

**CepDAOTest** (13 testes):
- Busca por código único
- Busca por logradouro, cidade, bairro, UF
- Busca por cidade + UF combinados
- Pesquisa geral em todos os campos
- Verificação de existência de código
- Validação de queries JPQL e nativas

**UsuarioDAOTest** (22 testes):
- Busca por username, email, token de verificação
- Validação de existência de username e email
- Busca por status ativo/inativo
- Busca por nome completo (case-insensitive)
- Busca por permissão específica
- Operações CRUD completas
- Validação de eager loading de permissões

#### 2. Testes de DTO (38 testes)

**CepDTOTest** (17 testes):
- Validação de campos obrigatórios (CEP, Logradouro, Bairro, Cidade, UF)
- Validação de formatos (CEP com/sem hífen)
- Validação de tamanhos máximos
- Validação de IBGE (7 dígitos numéricos ou vazio)
- Validação de UF (2 caracteres)
- Validação de campos opcionais (Complemento, IBGE)

**RegistroUsuarioDTOTest** (8 testes):
- Getters e setters
- Construtores (padrão e completo)
- Validação de campos

**UsuarioDTOTest** (13 testes):
- Getters e setters completos
- Gerenciamento de permissões (Set mutável)
- Campos de auditoria (datHoraCadastro, usuarioCadastro, etc)
- Construtores

#### 3. Testes de Mapper (21 testes)

**CepMapperTest** (12 testes):
- Conversão Entity para DTO e vice-versa
- Conversão de strings vazias para NULL (complemento e IBGE)
- Remoção de espaços em branco
- Atualização de entidades preservando ID e código
- Tratamento de valores nulos

**UsuarioMapperTest** (9 testes):
- Conversão Entity para DTO (sem incluir password)
- Conversão DTO para Entity
- Preservação de permissões
- Preservação de dados de auditoria
- Tratamento de valores nulos

#### 4. Testes de Service (51 testes)

**CepServiceImplTest** (11 testes):
- Operações CRUD completas (create, read, update, delete)
- Validação de regras de negócio
- Tratamento de exceções (ResourceNotFoundException, BusinessException)
- Validação de código duplicado
- Paginação e buscas

**EmailServiceImplTest** (10 testes):
- Envio de email de verificação
- Envio de email de boas-vindas
- Envio de email de reset de senha
- Envio de email de alteração de permissão
- Tratamento de erros de envio
- Validação de configurações (emailFrom, frontendUrl)

**UsuarioServiceImplTest** (30 testes):
- Registro de usuários com validações completas
- Verificação de email com token
- Reenvio de email de verificação
- Buscas por ID, username, nome, status
- Atualização de dados de usuário
- Gerenciamento de permissões (adicionar/remover)
- Ativação/desativação de usuários
- Alteração e reset de senha
- Validações de negócio (senhas coincidentes, duplicação, token expirado)
- Proteções de segurança (admin não pode ser desativado, última permissão não pode ser removida)

#### 5. Testes de Security (24 testes)

**UserDetailsImplTest** (14 testes):
- Implementação de UserDetails do Spring Security
- Validação de authorities (conversão de permissões)
- Validação de conta bloqueada/expirada
- Validação de usuário habilitado (ativo AND email verificado)
- Getters de username, password

**UserDetailsServiceImplTest** (10 testes):
- Carregamento de usuário por username
- Validações de usuário ativo e email verificado
- Criação de novos usuários
- Criptografia de senhas com BCrypt
- Tratamento de exceções (UsernameNotFoundException, DisabledException)

#### 6. Testes de Converter (12 testes)

**CepConverterTest** (12 testes):
- Conversão de CEP com hífen para números (getAsObject)
- Formatação de CEP adicionando hífen (getAsString)
- Remoção de caracteres especiais e não numéricos
- Validação de tamanho (8 dígitos)
- Tratamento de valores nulos/vazios

#### 7. Testes de Service WebApp (12 testes)

**AuthenticationServiceTest** (7 testes):
- Login com credenciais válidas
- Validação de senha com PasswordEncoder
- Validação de usuário ativo
- Validação de email verificado
- Geração de token JWT
- Tratamento de credenciais inválidas
- Ordem correta de validações

**JwtServiceTest** (5 testes):
- Geração de token com dados do usuário (username, permissions, userId, email)
- Uso de configurações do JwtProperties (issuer, duração)
- Validação de tokens únicos por usuário
- Integração com JwtEncoder do Spring OAuth2

### Por que Testes?

1. **Confiabilidade**: Garantem que o código funciona conforme esperado (193 testes passando)
2. **Refatoração Segura**: Permitem mudanças sem quebrar funcionalidades
3. **Documentação Viva**: Os testes servem como exemplos de uso das APIs
4. **Qualidade**: Forçam design mais limpo e desacoplado
5. **Regressão**: Previnem bugs antigos de voltarem
6. **Cobertura Completa**: 100% dos DAOs, Services, Mappers, DTOs e Security testados

---

## Decisões Técnicas

### Por que Multi-Module Maven?

**Decisão**: Separar o projeto em módulos `core` e `webapp`

**Justificativa**:
- O módulo `core` encapsula toda a lógica de negócio e pode ser reutilizado
- Facilita testes isolados de cada camada
- Permite compilação e deployment independentes
- Segue o princípio de separação de responsabilidades

### Por que Liquibase?

**Decisão**: Utilizar Liquibase para migrations de banco de dados

**Justificativa**:
- **Versionamento**: Todas as mudanças de schema são versionadas no código
- **Rastreabilidade**: Histórico completo de alterações no banco
- **Consistência**: Garante que todos os ambientes (dev, test, prod) tenham o mesmo schema
- **Rollback**: Permite reverter mudanças de forma controlada
- **Automação**: Migrations aplicadas automaticamente no startup

**Estrutura dos Changesets**:
```
liquibase/
├── changelog-master.xml                      # Arquivo principal (inclui todos os changesets)
├── changeset-001-create-table-cep.xml        # Criação da tabela CEP + índices
├── changeset-002-insert-sample-data.xml      # Dados iniciais de CEPs para testes
├── changeset-003-create-indexes.xml          # Índices adicionais para performance
├── changeset-004-audit-columns.xml           # Colunas de auditoria
├── changeset-005-performance-tuning.xml      # Otimizações de performance
├── changeset-006-create-table-usuario.xml    # Criação das tabelas Usuario + Permissões
├── changeset-007-insert-usuarios-default.xml # Usuários padrão (admin e user)
└── changeset-008-indexes-usuario.xml         # Índices para tabela de usuários
```

Cada changeset tem um ID único e é executado apenas uma vez. O Liquibase mantém uma tabela (`databasechangelog`) com o histórico de execução.

### Por que DTO Pattern?

**Decisão**: Usar DTOs separados das Entidades JPA

**Justificativa**:
- **Desacoplamento**: A API não expõe diretamente a estrutura do banco
- **Flexibilidade**: Permite retornar apenas dados necessários
- **Segurança**: Evita lazy loading exceptions e exposição de dados sensíveis
- **Versionamento**: Facilita manter compatibilidade com versões antigas da API

### Por que OAuth2 Resource Server com JWT?

**Decisão**: Implementar autenticação com Spring Security OAuth2 Resource Server e Nimbus JWT

**Justificativa**:
- **Padrão Moderno**: OAuth2 Resource Server é a abordagem recomendada pelo Spring Security 6+
- **Stateless**: Não requer armazenamento de sessão no servidor
- **Escalabilidade**: Facilita balanceamento de carga entre múltiplas instâncias
- **Mobile-Friendly**: Adequado para apps mobile, SPAs e APIs públicas
- **Segurança**: Nimbus JOSE + JWT é uma biblioteca robusta e amplamente testada
- **Manutenibilidade**: Código mais limpo sem necessidade de filtros customizados
- **Padrão de Mercado**: OAuth2 JWT é amplamente utilizado em APIs RESTful modernas

**Implementação**:
- `JwtEncoder` e `JwtDecoder` do Spring OAuth2
- `JwtAuthenticationConverter` customizado para converter authorities do claim "authorities"
- Configuração centralizada em `JwtProperties` com `@ConfigurationProperties`
- Token contém: subject (username), authorities (permissões), userId, email
- Validação automática de usuário ativo e email verificado via `UsuarioAtivoFilter`
- Tratamento de erros 401/403 com JSON customizado no `GlobalExceptionHandler`
- Expiração configurável (padrão: 24 horas)
- Secret configurável via variável de ambiente `JWT_SECRET`

### Por que JSF + PrimeFaces?

**Decisão**: Usar JSF para o frontend web por ser uma ferramenta de meu domínio e a qual pode ser bem utilizada com o framework Spring Boot, com auxílio de JoinFaces.

**Justificativa**:
- **Componentes**: PrimeFaces oferece componentes UI modernos
- **Integração Spring**: JoinFaces integra JSF com Spring Boot
- **Server-Side**: Lógica no servidor facilita manutenção
- **Produtividade**: Desenvolvimento rápido de interfaces complexas

### Por que Paginação?

**Justificativa**:
- **Performance**: Evita carregar grandes volumes de dados
- **Experiência**: Melhora tempo de resposta percebido
- **Escalabilidade**: Aplicação suporta crescimento de dados

### Por que Queries Explícitas?

**Decisão**: Usar `@Query` ao invés de métodos derivados

**Justificativa**:
- **Requisito**: Atende ao requisito de "SQL visível"
- **Controle**: Permite otimizações específicas
- **Clareza**: Facilita entendimento do que está sendo executado
- **Performance**: Possibilita uso de queries nativas quando necessário

### Por que Sistema de Permissões?

**Decisão**: Implementar controle de acesso baseado em roles com @PreAuthorize

**Justificativa**:
- **Segurança em Camadas**: Proteção tanto na API REST quanto no frontend JSF
- **Granularidade**: Controle fino sobre quem pode executar cada operação
- **Auditoria**: Rastreamento de quem fez cada alteração
- **Flexibilidade**: Fácil adicionar novas permissões
- **Spring Security Integration**: Usa recursos nativos do framework

**Implementação**:
- `@PreAuthorize("hasRole('ADMIN')")` nos endpoints administrativos
- `@PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")` para operações do próprio usuário
- Verificação de permissões no JSF usando `#{loginBean.isAdmin}`
- Tabela de junção `usuario_permissao` para relacionamento many-to-many
- Permissões carregadas no token JWT para validação rápida

### Por que Envio de Emails?

**Decisão**: Implementar sistema de emails transacionais com Spring Mail

**Justificativa**:
- **Verificação de Email**: Garante que o email fornecido é válido e pertence ao usuário
- **Segurança**: Previne criação de contas com emails falsos
- **Comunicação**: Notifica usuários sobre mudanças em suas contas
- **Experiência**: Processo de recuperação de senha amigável
- **Auditoria**: Log de todas as comunicações enviadas

**Changesets Implementados**:

**Changeset 001 - Criação da Estrutura Base**
- Cria a tabela `cep` com todos os campos necessários
- Define constraints (NOT NULL, tipos de dados)
- Cria chave primária com sequência auto-incrementada
- Demonstra conhecimento em: DDL, tipos de dados PostgreSQL, constraints

**Changeset 002 - Carga Inicial de Dados**
- Autor: Arthur Madalena
- Insere 5 CEPs de exemplo de diferentes estados (SP, RJ, MG, BA, RS)
- Popula campos de auditoria (datas de cadastro)
- Demonstra conhecimento em: DML, inserção de dados, testes com massa de dados

**Changeset 003 - Índices de Performance**
- Cria índice único para `codigo` (busca por CEP)
- Cria índices para `logradouro`, `cidade`, `uf`, `bairro`
- Cria índice composto para `cidade + uf`
- Implementa rollback completo
- Demonstra conhecimento em: otimização de queries, índices simples e compostos, estratégias de rollback

**Changeset 004 - Colunas de Auditoria**
- Adiciona campos `usuario_cadastro` e `usuario_alteracao`
- Atualiza registros existentes com valor padrão
- Permite rastreabilidade de quem criou/alterou cada registro
- Demonstra conhecimento em: ALTER TABLE, UPDATE com WHERE, auditoria de dados

**Changeset 005 - Performance Tuning e Validações**
- Adiciona constraint CHECK para validar formato do CEP (8 dígitos numéricos)
- Adiciona constraint CHECK para validar UF (2 caracteres maiúsculos)
- Adiciona constraint CHECK para validar código IBGE (7 dígitos ou NULL)
- Cria índices case-insensitive com LOWER() para buscas
- Usa SQL nativo para constraints avançadas
- Demonstra conhecimento em: expressões regulares PostgreSQL, validações em banco, índices funcionais, constraints complexas

**Changeset 006 - Sistema de Usuários**
- Cria tabela `usuario` com campos de autenticação e controle
- Cria tabela `usuario_permissao` para gerenciamento de roles
- Implementa relacionamento @ElementCollection para permissões
- Campos de verificação de email (token, data_expiracao)
- Foreign key com deleteCascade para integridade referencial
- Demonstra conhecimento em: sistema de autenticação, relacionamentos many-to-many, segurança

**Changeset 007 - Carga de Usuários Iniciais**
- Insere usuário **admin** com ROLE_ADMIN e ROLE_USER
- Insere usuário **user** com ROLE_USER
- Senhas criptografadas com BCrypt (`admin123` para ambos)
- **Usuários já com `email_verificado = true`** para uso imediato sem necessidade de verificação
- **Campo `ativo = true`** para acesso direto ao sistema
- Demonstra conhecimento em: hash de senhas, gestão de permissões, dados iniciais

**Changeset 008 - Otimização de Autenticação**
- Índices únicos para username e email (validação rápida, evita duplicatas)
- Índice para token de verificação (lookup eficiente em verificações de email)
- Índice para campo ativo (filtragem rápida de usuários ativos/inativos)
- Índices compostos para tabela de permissões (queries de autorização)
- Demonstra conhecimento em: otimização de login, índices estratégicos, performance de autenticação

### Funcionalidades Adicionais Implementadas

**Gerenciamento de CEPs**:
- DELETE para remover CEPs
- GET por UF, GET por ID
- Pesquisa geral (busca em todos os campos: CEP, Logradouro, Bairro, Cidade, Complemento, UF, IBGE)
- Interface web completa com JSF/PrimeFaces
- Dashboard com estatísticas e gráficos

**Sistema de Usuários e Permissões**:
- Registro de usuários com verificação de email
- Autenticação baseada em banco de dados
- Sistema de permissões (ROLE_USER, ROLE_ADMIN, ROLE_GERENTE)
- Gerenciamento de usuários (ADMIN only)
- Ativação/desativação de contas
- Reset de senha com email
- Controle de acesso baseado em roles (@PreAuthorize)

**Qualidade e Testes**:
- 193 testes unitários cobrindo 100% das camadas principais
- Testes de DAO, Service, Mapper, DTO, Security e Converters
- Coleção Postman completa com 23 requisições
- Tratamento de erros global
- Validações de negócio em múltiplas camadas

**Infraestrutura**:
- Configuração de email (Spring Mail)
- Conversores e formatadores
- Logs estruturados (SLF4J)
- Documentação Swagger completa

### Pontos Fortes da Solução

1. **Arquitetura Limpa**: Separação clara de responsabilidades em módulos Maven
2. **Segurança Avançada**: OAuth2 Resource Server com JwtAuthenticationConverter, controle de permissões, verificação de email, UsuarioAtivoFilter
3. **Testabilidade**: 193 testes unitários com 100% de cobertura das camadas principais (DAOs, Services, Mappers, DTOs, Security, Converters)
4. **Documentação Completa**: Swagger interativo, Postman, README detalhado com guias de execução
5. **Qualidade de Código**: Liquibase migrations, DTOs, tratamento de erros, auditoria, validações em múltiplas camadas
6. **Manutenibilidade**: Código organizado, queries visíveis, logs estruturados, testes como documentação
7. **Escalabilidade**: Stateless JWT, paginação, índices otimizados
8. **UX Moderna**: Interface responsiva, validações, mensagens específicas
9. **Email Integration**: Sistema completo de notificações transacionais
10. **Controle de Acesso**: Sistema robusto de permissões e roles

### Diferenciais Técnicos

**Além dos requisitos obrigatórios, foram implementados**:
- Sistema completo de gerenciamento de usuários e permissões
- Controle de permissões granular com `@PreAuthorize("hasRole('ADMIN')")`
- Verificação de email com token temporário (expiração 24h)
- Auditoria completa (quem criou/alterou, quando)
- 8 changesets Liquibase bem estruturados e documentados
- Índices otimizados (únicos, compostos, case-insensitive, funcionais)
- Constraints de validação em banco (CHECK, UNIQUE, FK)
- Envio de emails transacionais (verificação, boas-vindas, reset senha, alteração permissões)
- Tela administrativa de usuários com filtros e ações em lote
- Tela de gerenciamento de permissões com dropdown inteligente
- Proteções de segurança (admin não pode ser desativado, última permissão não pode ser removida)
- OAuth2 Resource Server (abordagem moderna Spring Security 6+)
- JwtAuthenticationConverter para conversão automática de authorities
- UsuarioAtivoFilter para validação global de usuário ativo e email verificado
- GlobalExceptionHandler com tratamento de 401/403/400/500
- Suporte a formato de CEP com ou sem traço (validação flexível)
- Interface JSF responsiva com PrimeFaces 13.0
- Mensagens de feedback específicas para cada situação

---

## Informações do Projeto

**Projeto**: CepFinder - Sistema de Gerenciamento de CEPs

**Autor**: Arthur Madalena

**Pacote Base**: `br.com.arthur.madalena.cepmanager`

**Tecnologias**: 
- Backend: Java 17, Spring Boot 3, Spring Data JPA, PostgreSQL, Liquibase
- Frontend: JSF 4.0, PrimeFaces 13.0, XHTML/CSS
- Segurança: Spring Security OAuth2, JWT (Nimbus)
- Testes: JUnit 5, Mockito, AssertJ, H2 Database
- Email: Spring Mail

**Arquitetura**: Multi-Module Maven Project (Core + WebApp)

**Testes**: 193 testes unitários (100% de cobertura das camadas principais)

---

## Estatísticas do Projeto

- **Linhas de Código**: 17 entidades/services principais + 138 classes de suporte
- **Testes Unitários**: 193 testes passando (0 falhas)
- **Cobertura de Testes**: 100% DAOs, Services, Mappers, DTOs, Security, Converters
- **Changesets Liquibase**: 8 migrations versionadas
- **Endpoints REST**: 30+ endpoints documentados no Swagger
- **Páginas Web**: 10 telas JSF/PrimeFaces completas
- **Segurança**: 3 níveis de permissões (ROLE_USER, ROLE_ADMIN, ROLE_GERENTE)
- **Performance**: Queries otimizadas com 15+ índices estratégicos

