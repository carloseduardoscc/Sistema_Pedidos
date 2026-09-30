package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.api.controller;

import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.api.controller.common.GenericController;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.ProdutoService;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.dto.produto.ProdutoDTO;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.query_filters.RequisicaoFiltroProduto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping(value = "produtos")
@RequiredArgsConstructor
@Tag(name = "Produtos")
@Slf4j
public class ProdutoController implements GenericController {

    private final ProdutoService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LOJISTA')")
    @Operation(
            summary = "Cadastrar produto", description = "Cadastra produto")
    @ApiResponses({
            @ApiResponse(responseCode = "203", description = "Produto cadastrado com sucesso"),
            @ApiResponse(responseCode = "422", description = "Erro de validação", content = @Content())})
    public ResponseEntity<ProdutoDTO> cadastrar(@RequestBody @Valid ProdutoDTO produtoDTO) {
        var prodCadastrado = service.cadastrar(produtoDTO);
        URI location = gerarHeaderLocation(prodCadastrado.id().toString());
        return ResponseEntity.created(location).body(prodCadastrado);
    }

    @GetMapping
    @PreAuthorize("permitAll()")
    @Operation(
            summary = "Listar produtos", description = "Lista produtos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de produtos encontrada com sucesso")})
    public ResponseEntity<Page<ProdutoDTO>> listar(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC)
            @ParameterObject Pageable pageable,
            @ModelAttribute RequisicaoFiltroProduto requisicaoFiltro){
        Page<ProdutoDTO> lista = service.listar(pageable, requisicaoFiltro);
        return ResponseEntity.ok().body(lista);
    }

    @GetMapping("{id}")
    @PreAuthorize("permitAll()")
    @Operation(
            summary = "Buscar produto por ID", description = "Busca produto por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content())
    })
    public ResponseEntity<ProdutoDTO> buscarPorId(@PathVariable UUID id){
        ProdutoDTO dto = service.buscarPorId(id);
        return ResponseEntity.ok().body(dto);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOJISTA')")
    @Operation(
            summary = "Atualizar produto", description = "Atualiza produto por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Produto atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content()),
            @ApiResponse(responseCode = "422", description = "Erro de validação", content = @Content())})

    public ResponseEntity<Void> atualizar(@PathVariable UUID id, @RequestBody ProdutoDTO dto){
        service.atualizar(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOJISTA')")
    @Operation(
            summary = "Deletar produto", description = "Deleta produto por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Produto deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content())})
    public ResponseEntity<Void> deletar(@PathVariable UUID id){
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

}
