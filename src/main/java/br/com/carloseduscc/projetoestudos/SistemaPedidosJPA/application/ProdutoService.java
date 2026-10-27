package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application;

import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.dto.produto.ProdutoDTO;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.exception.NaoEncontradoException;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.exception.OperacaoNaoPermitidaException;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.mapper.ProdutoMapper;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.query_filters.RequisicaoFiltroProduto;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.repository.ItemPedidoRepository;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.repository.ProdutoRepository;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProdutoService {

    ProdutoRepository repository;
    ItemPedidoRepository itemRepository;
    ProdutoMapper mapper;

    public ProdutoService(ProdutoRepository repository, ItemPedidoRepository itemRepository, ProdutoMapper mapper) {
        this.repository = repository;
        this.itemRepository = itemRepository;
        this.mapper = mapper;
    }

    public ProdutoDTO cadastrar(ProdutoDTO produtoDTO) {
        Produto produtoParaCadastrar = mapper.fromDtoToProduto(produtoDTO);
        return mapper.toDto(repository.save(produtoParaCadastrar));
    }

    public Page<ProdutoDTO> listar(Pageable pageable, RequisicaoFiltroProduto  requisicaoFiltro) {
        Page<Produto> listaEncontrada = repository.findAll(requisicaoFiltro.toSpecification(), pageable);
        return listaEncontrada.map(mapper::toDto);
    }

    public ProdutoDTO buscarPorId(UUID id) {
        Produto produto = buscarProduto(id);
        return mapper.toDto(produto);
    }

    public Produto atualizar(UUID id, ProdutoDTO produtoDTO) {
        Produto produtoParaAtualizar = buscarProduto(id);

        if (produtoDTO.nome() != null) produtoParaAtualizar.setNome(produtoDTO.nome());
        if (produtoDTO.precoUnitario() != null) produtoParaAtualizar.setPrecoUnitario(produtoDTO.precoUnitario());

        return repository.save(produtoParaAtualizar);
    }

    public void deletar(UUID id) {
        Produto produto = buscarProduto(id);
        if (itemRepository.existsByProduto_Id(produto.getId())) {
            throw new OperacaoNaoPermitidaException("Não é possível deletar o produto pois existem pedidos que contém ele");
        }
        repository.delete(produto);
    }


    // De uso interno
    private Produto buscarProduto(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Produto com Id: " + id.toString() + " não encontrado"));
    }
}
