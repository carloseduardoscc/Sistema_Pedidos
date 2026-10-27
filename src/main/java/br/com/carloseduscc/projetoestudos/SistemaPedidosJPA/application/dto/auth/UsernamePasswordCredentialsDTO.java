package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Credenciais de autenticação do usuário")
public record UsernamePasswordCredentialsDTO(
        @Schema(description = "Nome de usuário ou email para autenticação", example = "usuario@exemplo.com", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Username é obrigatório")
        String username,
        @Schema(description = "Senha do usuário", example = "senha123", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Password é obrigatório")
        String password
) {
}
