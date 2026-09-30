-- ==========================
-- USUÁRIOS
-- ==========================

INSERT INTO order_management.usuario_tb
(id, ativo, data_cadastro, data_atualizacao, email, nome, senha, role, version)
VALUES ('11111111-1111-1111-1111-111111111111',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        'carlos@email.com',
        'Carlos Eduardo',
        '$2a$10$rZjDpBft/x0vXoJykBwfBuj8.I6BxnfBJWM4W6NldVH5APb8gBN7q',
        'ADMIN',
        0),
       ('22222222-2222-2222-2222-222222222222',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        'maria@email.com',
        'Maria Silva',
        '$2a$10$rZjDpBft/x0vXoJykBwfBuj8.I6BxnfBJWM4W6NldVH5APb8gBN7q',
        'CLIENTE',
        0),
       ('33333333-3333-3333-3333-333333333333',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        'joao@email.com',
        'Joao Santos',
        '$2a$10$rZjDpBft/x0vXoJykBwfBuj8.I6BxnfBJWM4W6NldVH5APb8gBN7q',
        'CLIENTE',
        0),
       ('44444444-4444-4444-4444-444444444444',
        FALSE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        'ana@email.com',
        'Ana Costa',
        '$2a$10$rZjDpBft/x0vXoJykBwfBuj8.I6BxnfBJWM4W6NldVH5APb8gBN7q',
        'CLIENTE',
        0),
       ('55555555-5555-5555-5555-555555555555',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        'pedro.entregador@email.com',
        'Pedro Entregador',
        '$2a$10$rZjDpBft/x0vXoJykBwfBuj8.I6BxnfBJWM4W6NldVH5APb8gBN7q',
        'ENTREGADOR',
        0),
       ('66666666-6666-6666-6666-666666666666',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        'lucas.lojista@email.com',
        'Lucas Lojista',
        '$2a$10$rZjDpBft/x0vXoJykBwfBuj8.I6BxnfBJWM4W6NldVH5APb8gBN7q',
        'LOJISTA',
        0);

-- ==========================
-- PEDIDOS
-- ==========================

INSERT
INTO order_management.pedido_tb
(id, data_cadastro, data_atualizacao, data_hora_pedido, status, version, usuario_id, criado_por)
VALUES ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
        DATEADD('DAY', -5, CURRENT_TIMESTAMP),
        'PAGO',
        0,
        '33333333-3333-3333-3333-333333333333',
        '33333333-3333-3333-3333-333333333333'),

       ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
        DATEADD('DAY', -3, CURRENT_TIMESTAMP),
        'PAGO',
        0,
        '44444444-4444-4444-4444-444444444444',
        '44444444-4444-4444-4444-444444444444'),

       ('cccccccc-cccc-cccc-cccc-cccccccccccc',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
        DATEADD('DAY', -2, CURRENT_TIMESTAMP), 'CANCELADO',
        0,
        '22222222-2222-2222-2222-222222222222',
        '22222222-2222-2222-2222-222222222222'),

       ('dddddddd-dddd-dddd-dddd-dddddddddddd',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
        DATEADD('DAY', -1, CURRENT_TIMESTAMP),
        'ENTREGUE',
        0,
        '33333333-3333-3333-3333-333333333333',
        '33333333-3333-3333-3333-333333333333'),

       ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        'PENDENTE',
        0,
        '33333333-3333-3333-3333-333333333333',
        '33333333-3333-3333-3333-333333333333');

-- ==========================
-- PRODUTOS
-- ==========================

INSERT INTO stock.produto_tb
(id, data_cadastro, data_atualizacao, nome, preco_unitario, version, criado_por)
VALUES ('20000000-0000-0000-0000-000000000001',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        'Notebook Dell',
        3500.00,
        0,
        '11111111-1111-1111-1111-111111111111'),

       ('20000000-0000-0000-0000-000000000002',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        'Mouse Logitech',
        120.00,
        0,
        '11111111-1111-1111-1111-111111111111'),

       ('20000000-0000-0000-0000-000000000003',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        'Teclado Mecanico',
        280.00,
        0,
        '11111111-1111-1111-1111-111111111111'),

       ('20000000-0000-0000-0000-000000000004',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        'Monitor LG 24',
        950.00,
        0,
        '11111111-1111-1111-1111-111111111111'),

       ('20000000-0000-0000-0000-000000000005',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        'Headset HyperX', 420.00, 0,
        '11111111-1111-1111-1111-111111111111'),

       ('20000000-0000-0000-0000-000000000006',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP, 'Cadeira Gamer',
        1800.00,
        0,
        '11111111-1111-1111-1111-111111111111'),


       ('20000000-0000-0000-0000-000000000007',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP, 'Webcam Logitech',
        350.00,
        0,
        '11111111-1111-1111-1111-111111111111'),

       ('20000000-0000-0000-0000-000000000008',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        'Suporte duplo monitor',
        240.00,
        0,
        '11111111-1111-1111-1111-111111111111');

-- ==========================
-- ITENS PEDIDO
-- ==========================

INSERT INTO order_management.item_pedido_tb
(id, data_cadastro, data_atualizacao, quantidade, version, pedido_id, produto_id, criado_por)
VALUES ('10000000-0000-0000-0000-000000000001',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        1,
        0,
        'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
        '20000000-0000-0000-0000-000000000001',
        '11111111-1111-1111-1111-111111111111'),

       ('10000000-0000-0000-0000-000000000002',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        2,
        0,
        'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
        '20000000-0000-0000-0000-000000000002',
        '11111111-1111-1111-1111-111111111111'),

       ('10000000-0000-0000-0000-000000000003',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        1,
        0,
        'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
        '20000000-0000-0000-0000-000000000003',
        '11111111-1111-1111-1111-111111111111'),

       ('10000000-0000-0000-0000-000000000004',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        2,
        0,
        'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
        '20000000-0000-0000-0000-000000000004',
        '11111111-1111-1111-1111-111111111111'),

       ('10000000-0000-0000-0000-000000000005',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        1,
        0,
        'cccccccc-cccc-cccc-cccc-cccccccccccc',
        '20000000-0000-0000-0000-000000000005',
        '22222222-2222-2222-2222-222222222222'),

       ('10000000-0000-0000-0000-000000000006',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        1,
        0,
        'dddddddd-dddd-dddd-dddd-dddddddddddd',
        '20000000-0000-0000-0000-000000000006',
        '33333333-3333-3333-3333-333333333333'),

       ('10000000-0000-0000-0000-000000000007',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        3,
        0,
        'dddddddd-dddd-dddd-dddd-dddddddddddd',
        '20000000-0000-0000-0000-000000000007',
        '33333333-3333-3333-3333-333333333333'),

       ('10000000-0000-0000-0000-000000000008',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        1,
        0,
        'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee',
        '20000000-0000-0000-0000-000000000008',
        '33333333-3333-3333-3333-333333333333');

-- ==========================
-- ATUALIZAÇÕES DE STATUS DOS PEDIDOS
-- ==========================

INSERT INTO order_management.atualizacao_status_tb
(data_hora, status, pedido_id)
VALUES
        -- Pedido A
        (DATEADD('DAY', -5, CURRENT_TIMESTAMP),
         'PENDENTE',
         'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'),

        (DATEADD('DAY', -4, CURRENT_TIMESTAMP),
         'PAGO',
         'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'),

        -- Pedido B
        (DATEADD('DAY', -3, CURRENT_TIMESTAMP),
         'PENDENTE',
         'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb'),

        (DATEADD('DAY', -2, CURRENT_TIMESTAMP),
         'PAGO',
         'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb'),

        -- Pedido C
        (DATEADD('DAY', -2, CURRENT_TIMESTAMP),
         'PENDENTE',
         'cccccccc-cccc-cccc-cccc-cccccccccccc'),

        (DATEADD('DAY', -1, CURRENT_TIMESTAMP),
         'CANCELADO',
         'cccccccc-cccc-cccc-cccc-cccccccccccc'),

        -- Pedido D
        (DATEADD('DAY', -1, CURRENT_TIMESTAMP),
         'PENDENTE',
         'dddddddd-dddd-dddd-dddd-dddddddddddd'),

        (DATEADD('HOUR', -12, CURRENT_TIMESTAMP),
         'PAGO',
         'dddddddd-dddd-dddd-dddd-dddddddddddd'),

        (DATEADD('HOUR', -6, CURRENT_TIMESTAMP),
         'ENTREGUE',
         'dddddddd-dddd-dddd-dddd-dddddddddddd'),

        -- Pedido E
        (CURRENT_TIMESTAMP,
         'PENDENTE',
         'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee');