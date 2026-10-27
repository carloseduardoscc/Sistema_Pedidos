package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.api.controller;

import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.api.controller.common.GenericController;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.UsuarioService;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.comand.AtualizarDadosUsuarioCommand;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.dto.pedido.AbrirPedidoResponseDTO;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.dto.usuario.UsuarioDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping(value = "usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuários")
@Slf4j
public class UsuarioController implements GenericController {

    private final UsuarioService service;

    @PostMapping("{usuarioId}/pedidos")
    @PreAuthorize("@autorizador.podeAbrirNovoPedido(authentication, #usuarioId)")
    @Operation(
            summary = "Abrir novo pedido", description = "Abre um novo pedido para o usuário"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "203", description = "Pedido criado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não foi encontrado", content = @Content()),
            @ApiResponse(responseCode = "400", description = "Violação de regra de negócio: Usuário já possúi pedido pendente", content = @Content())
    })
    public ResponseEntity<AbrirPedidoResponseDTO> abrirNovoPedido(@PathVariable UUID usuarioId) {
        log.debug("Abrindo novo pedido para o usuário de id {}", usuarioId);
        var response = service.abrirNovoPedido(usuarioId);
        URI uri = gerarHeaderLocation(response.id().toString());
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping("{usuarioId}")
    @PreAuthorize("#usuarioId == authentication.principal.id or hasRole('ADMIN')")
    @Operation(
            summary = "Buscar detalhes", description = "Busca os detalhes de um usuário pelo seu Id"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Detalhes encontrados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content())
    })
    public ResponseEntity<UsuarioDTO> buscarDetalhes(@PathVariable UUID usuarioId) {
        log.debug("Buscando detalhes do usuário de ID: {}", usuarioId);
        UsuarioDTO usuarioDTO = service.buscarDetalhes(usuarioId);
        return ResponseEntity.ok(usuarioDTO);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Detalha usuário autenticado", description = "Busca os detalhes do usuário autenticado"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Detalhes encontrados com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não foram fornecidas credenciais", content = @Content())
    })
    public ResponseEntity<UsuarioDTO> me(Authentication authentication) {
        log.debug("Buscando detalhes do usuário: {}", authentication.getName());
        UsuarioDTO usuarioDTO = service.buscarDetalhesUsuarioLogado();
        return ResponseEntity.ok(usuarioDTO);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Pesquisar", description = "Realiza uma pesquisa baseada em parâmetros"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pesquisa realizada com sucesso")
    })
    public ResponseEntity<Page<UsuarioDTO>> pesquisarListagem(
            @RequestParam(value = "nome", required = false) String nome,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "page", defaultValue = "0") Integer numeroPagina,
            @RequestParam(value = "size", defaultValue = "10") Integer tamanhoPagina
    ) {
        log.debug("Pesquisada listagem paginada de usuários");
        Page<UsuarioDTO> pagina = service.pesquisarListagem(nome, email, numeroPagina, tamanhoPagina);
        return ResponseEntity.ok(pagina);
    }

    @PatchMapping("{usuarioId}")
    @PreAuthorize("#usuarioId == authentication.principal.id or hasRole('ADMIN')")
    @Operation(
            summary = "Atualizar dados", description = "Atualiza os dados do usuário"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Dados atualizados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content()),
            @ApiResponse(responseCode = "409", description = "E-mail já existe", content = @Content()),
            @ApiResponse(responseCode = "422", description = "Erro de validação", content = @Content())
    })
    public ResponseEntity<Object> atualizarDados(
            @RequestBody @Valid AtualizarDadosUsuarioCommand cmd,
            @PathVariable UUID usuarioId
    ) {
        log.debug("Atualizando dados do usuário de ID: {}", usuarioId);
        service.atualizarDados(cmd, usuarioId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{usuarioId}")
    @PreAuthorize("#usuarioId == authentication.principal.id")
    @Operation(
            summary = "Desativar usuário", description = "Desativação de conta realizada pelo usuário"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "203", description = "Usuário desativado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Violação de regra de negócio: Usuário ainda contém pedidos enviados, pagos ou pendentes", content = @Content()),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content()),
    })
    public ResponseEntity<Object> desativarUsuario(@PathVariable UUID usuarioId) {
        log.debug("Desativando usuário de ID: {}", usuarioId);
        service.desativarUsuario(usuarioId);
        return ResponseEntity.noContent().build();
    }
}
