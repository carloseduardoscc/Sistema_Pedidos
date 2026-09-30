package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.dto.erro;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

import java.util.List;

@Schema(name = "Resposta de erro", description = "Informa um erro com a requisição")
public record ErroResposta(
        @Schema(description = "Status HTTP", example = "422")
        int status,
        @Schema(description = "Descrição do problema", example = "Erro de validação")
        String mensagem,
        @Schema(description = "Contém os erros encontrados")
        List<CampoErro> erros
) {

    public static ErroResposta respostaPadrao (String mensagem){
        return new ErroResposta(HttpStatus.BAD_REQUEST.value(), mensagem, List.of());
    }

    public static ErroResposta naoEncontrado (String mensagem){
        return new ErroResposta(HttpStatus.NOT_FOUND.value(), mensagem, List.of());
    }

    public static ErroResposta conflito(String mensagem){
        return new ErroResposta(HttpStatus.CONFLICT.value(), mensagem, List.of());
    }
}