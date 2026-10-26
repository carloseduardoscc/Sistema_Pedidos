package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.dto.auth;

import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(name = "Requisição para cadastrar usuário")
public record CadastrarUsuarioDTO
        (
                @NotBlank(message = "campo obrigatório")
                @Size(max = 100, message = "campo precisa ter menos que 100 caracteres")
                @Size(min = 2, message = "campo precisa ter mais que 2 caracteres")
                @Schema(minLength = 2, maxLength = 100, example = "Carlos Eduardo")
                String nome,
                @Email(message = "E-mail inválido")
                @NotBlank(message = "campo obrigatório")
                @Schema(example = "carlos.eduardo@email.com")
                String email,
                @NotBlank(message = "campo obrigatório")
                @Size(min = 6, message = "a senha deve conter no mínimo 6 caracteres")
                @Size(max= 300, message = "a senha deve ter menos que 300 caracteres")
                @Schema(minLength = 6, maxLength = 300, example = "123456")
                String senha,
                @NotNull(message = "campo obrigatório")
                @Schema(description = "Cargo do novo usuário")
                Role role
        ) {
}
