package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model;

import java.util.Set;

public enum Role {
    ADMIN,
    CLIENTE,
    ENTREGADOR,
    LOJISTA;

    public static Set<Role> getFuncionarios(){
        return Set.of(ADMIN, ENTREGADOR, LOJISTA);
    }

    public static Role getRolePadrao(){
        return CLIENTE;
    }
}