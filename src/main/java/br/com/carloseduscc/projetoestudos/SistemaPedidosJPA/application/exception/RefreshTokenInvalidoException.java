package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.exception;

public class RefreshTokenInvalidoException extends RuntimeException {
    public RefreshTokenInvalidoException() {
        super("Refresh token inválido, expirado ou revogado");
    }
}
