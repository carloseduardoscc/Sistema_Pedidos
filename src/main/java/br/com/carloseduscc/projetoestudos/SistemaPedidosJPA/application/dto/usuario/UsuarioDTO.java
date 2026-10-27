package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.dto.usuario;

import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(name = "Usuário")
public record UsuarioDTO (
        UUID id,
        @Schema(example = "Carlos Eduardo")
        String nome,
        @Schema(example = "carlos.eduardo@email.com")
        String email,
        @Schema(description = "Cargo do usuário", example = "ADMIN")
        Role role,
        @Schema(description = "Status do usuário, se está ativo ou não")
        boolean ativo
) {
}
