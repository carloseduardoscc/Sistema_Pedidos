package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.api.controller;

import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.api.controller.common.GenericController;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.ItemPedidoService;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.comand.AtualizarItemCommand;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.dto.item.ItemDetalhadoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("itens-pedido")
@RequiredArgsConstructor
@Tag(name = "Itens")
@Slf4j
public class ItemPedidoController implements GenericController {

    public final ItemPedidoService service;

    @GetMapping("{itemId}")
    @PreAuthorize("@autorizador.isUsuarioDonoItem(#itemId, authentication.principal.id) or hasRole('ADMIN')")
    @Operation(
            summary = "Buscar detalhes", description = "Busca detalhes do item de um pedido pelo id"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Busca do item realizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Item não encontrado", content = @Content())
    })
    public ResponseEntity<ItemDetalhadoDTO> buscarDetalhes(@PathVariable UUID itemId) {
        log.debug("Buscando detalhes do item de ID: {}", itemId);
        var responseDTO = service.buscarDetalhes(itemId);
        return ResponseEntity.ok(responseDTO);
    }

    @PatchMapping("{itemId}")
    @PreAuthorize("@autorizador.isUsuarioDonoItem(#itemId, authentication.principal.id) or hasRole('ADMIN')")
    @Operation(
            summary = "Atualizar", description = "Atualiza dados do item de um pedido"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "203", description = "Atualização realizada com sucesso"),
            @ApiResponse(responseCode = "422", description = "Erro de validação", content = @Content()),
            @ApiResponse(responseCode = "400", description = "Valor máximo do pedido excedeu 10.000", content = @Content()),
    })
    public ResponseEntity<Void> atualizarItem(@PathVariable UUID itemId, @RequestBody @Valid AtualizarItemCommand dados) {
        log.debug("Atualizando item de ID: {}", itemId);
        service.atualizar(dados, itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{itemId}")
    @PreAuthorize("@autorizador.isUsuarioDonoItem(#itemId, authentication.principal.id) or hasRole('ADMIN')")
    @Operation(
            summary = "Remover", description = "Remove item do pedido"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "203", description = "Item removido com sucesso"),
            @ApiResponse(responseCode = "400", description = "Violação de regra de negócio: Não é possível remover um item de um pedido que não esteja mais pendente", content = @Content()),
            @ApiResponse(responseCode = "404", description = "Item não encontrado", content = @Content())
    })
    public ResponseEntity<Void> removerItem(@PathVariable UUID itemId) {
        log.debug("Removendo item de ID: {}", itemId);
        service.remover(itemId);
        return ResponseEntity.noContent().build();
    }
}
