-- ============================================================================
-- Script para inserir usuários padrão no sistema CepFinder
-- ============================================================================
-- Este script insere 2 usuários no sistema:
--   1. admin (ROLE_ADMIN + ROLE_USER) - senha: admin123
--   2. user  (ROLE_USER)               - senha: admin123
--
-- IMPORTANTE: Execute este script apenas UMA VEZ em um banco de dados limpo!
-- ============================================================================

-- Limpa usuários existentes (se necessário)
DELETE FROM usuario_permissao WHERE usuario_id IN (SELECT id FROM usuario WHERE username IN ('admin', 'user'));
DELETE FROM usuario WHERE username IN ('admin', 'user');

-- Reseta a sequência do usuário
SELECT setval('seq_usuario', 1, false);

-- ============================================================================
-- INSERIR USUÁRIOS
-- ============================================================================

-- Usuário Admin
-- Username: admin
-- Password: admin123
-- Hash BCrypt: $2a$10$5I7yUZ.b7gm.RscZw4I0q.oH2Y/6Dg4c1kL5VNyknl6H/e0G21nBq
INSERT INTO usuario (id, username, email, password, nome_completo, ativo, email_verificado, usuario_cadastro)
VALUES (
    nextval('seq_usuario'),
    'admin',
    'admin@cepfinder.com',
    '$2a$10$5I7yUZ.b7gm.RscZw4I0q.oH2Y/6Dg4c1kL5VNyknl6H/e0G21nBq',
    'Administrador do Sistema',
    true,
    true,
    'sistema'
);

-- Usuário User
-- Username: user
-- Password: admin123
-- Hash BCrypt: $2a$10$5I7yUZ.b7gm.RscZw4I0q.oH2Y/6Dg4c1kL5VNyknl6H/e0G21nBq
INSERT INTO usuario (id, username, email, password, nome_completo, ativo, email_verificado, usuario_cadastro)
VALUES (
    nextval('seq_usuario'),
    'user',
    'user@cepfinder.com',
    '$2a$10$5I7yUZ.b7gm.RscZw4I0q.oH2Y/6Dg4c1kL5VNyknl6H/e0G21nBq',
    'Usuário Padrão',
    true,
    true,
    'sistema'
);

-- ============================================================================
-- INSERIR PERMISSÕES
-- ============================================================================

-- Permissões do Admin (ROLE_ADMIN + ROLE_USER)
INSERT INTO usuario_permissao (usuario_id, permissao)
VALUES (1, 'ROLE_ADMIN');

INSERT INTO usuario_permissao (usuario_id, permissao)
VALUES (1, 'ROLE_USER');

-- Permissões do User (ROLE_USER)
INSERT INTO usuario_permissao (usuario_id, permissao)
VALUES (2, 'ROLE_USER');

-- ============================================================================
-- VERIFICAR INSERÇÃO
-- ============================================================================

-- Listar usuários criados
SELECT u.id, u.username, u.email, u.nome_completo, u.ativo, u.email_verificado,
       string_agg(up.permissao, ', ') as permissoes
FROM usuario u
LEFT JOIN usuario_permissao up ON u.id = up.usuario_id
WHERE u.username IN ('admin', 'user')
GROUP BY u.id, u.username, u.email, u.nome_completo, u.ativo, u.email_verificado
ORDER BY u.id;

-- ============================================================================
-- CREDENCIAIS PARA LOGIN
-- ============================================================================
-- Admin:
--   Username: admin
--   Password: admin123
--   Permissões: ROLE_ADMIN, ROLE_USER
--
-- User:
--   Username: user
--   Password: admin123
--   Permissões: ROLE_USER
-- ============================================================================

