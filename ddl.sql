
    create table order_management.item_pedido_tb (
        preco_unitario numeric(18,2) not null,
        quantidade integer not null,
        data_atualizacao timestamp(6),
        data_cadastro timestamp(6),
        version bigint,
        id uuid not null,
        id_usuario uuid,
        pedido_id uuid,
        nome_produto varchar(100) not null,
        primary key (id),
        check (quantidade > 0 AND preco_unitario >= 0)
    );

    create table order_management.pedido_tb (
        data_atualizacao timestamp(6),
        data_cadastro timestamp(6),
        data_hora_pedido timestamp(6),
        version bigint,
        id uuid not null,
        id_usuario uuid,
        usuario_id uuid,
        status enum ('CANCELADO','ENTREGUE','ENVIADO','PAGO','PENDENTE') not null,
        primary key (id)
    );

    create table order_management.usuario_tb (
        ativo boolean,
        data_atualizacao timestamp(6),
        data_cadastro timestamp(6),
        version bigint,
        id uuid not null,
        nome varchar(100) not null,
        email varchar(254) unique,
        senha varchar(255),
        role enum ('ADMIN','CLIENTE','ENTREGADOR','LOJISTA') array,
        primary key (id)
    );

    alter table if exists order_management.item_pedido_tb 
       add constraint FK8h2huo9bce2vibitvhiawh923 
       foreign key (pedido_id) 
       references order_management.pedido_tb;

    alter table if exists order_management.pedido_tb 
       add constraint FKlriq8p9l12jrnvy2ghnjcf7gj 
       foreign key (usuario_id) 
       references order_management.usuario_tb;
