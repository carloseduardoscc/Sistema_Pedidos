package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application;

import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.comand.AtualizarDadosUsuarioCommand;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.dto.pedido.AbrirPedidoResponseDTO;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.dto.usuario.UsuarioDTO;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.exception.NaoEncontradoException;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.mapper.PedidoMapper;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.mapper.UsuarioMapper;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.validator.UsuarioValidator;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.repository.PedidoRepository;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.repository.UsuarioRepository;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.repository.specs.UsuarioSpecs;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.Pedido;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.StatusPedido;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.Usuario;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.exception.RegraDeNegocioException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    final private UsuarioRepository repository;
    final private PedidoRepository pedidoRepository;
    final private UsuarioMapper mapper;
    final private PedidoMapper pedidoMapper;
    final private UsuarioValidator validator;

    public UsuarioDTO buscarDetalhes(UUID id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> getUsuarioNaoEncontradoException(id));
        return mapper.toDTO(usuario);
    }

    public Page<UsuarioDTO> pesquisarListagem(String nome, String email, Integer numeroPagina, Integer tamanhoPagina) {
        Specification<Usuario> usuarioSpecs = UsuarioSpecs.initialize();

        if (nome != null) usuarioSpecs = usuarioSpecs.and(UsuarioSpecs.nomeLike(nome));
        if (email != null) usuarioSpecs = usuarioSpecs.and(UsuarioSpecs.emailLike(email));

        PageRequest pageable = PageRequest.of(numeroPagina, tamanhoPagina);

        return repository.findAll(usuarioSpecs, pageable).map(mapper::toDTO);

    }

    public UsuarioDTO buscarDetalhesUsuarioLogado() {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Usuario usuario) {
            return mapper.toDTO(usuario);
        }
        throw new RuntimeException("Usuário armazenado no contexto do security não é uma instância de CustomAuthentication como esperado, é um "
                + SecurityContextHolder.getContext().getAuthentication().getPrincipal().getClass().getName()
        );
    }

    @Transactional
    public void atualizarDados(AtualizarDadosUsuarioCommand dados, UUID id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> getUsuarioNaoEncontradoException(id));

        if (dados.nome() != null && !usuario.getNome().equals(dados.nome())) usuario.setNome(dados.nome());
        if (dados.email() != null && !usuario.getEmail().equals(dados.email())) {
            validator.validarEmailJaExiste(dados.email());
            usuario.setEmail(dados.email());
        }
    }

    public AbrirPedidoResponseDTO abrirNovoPedido(UUID id) {
        Usuario usuario = repository.findById(id).orElseThrow(() -> getUsuarioNaoEncontradoException(id));
        if (pedidoRepository.existsByUsuarioAndStatus(usuario, StatusPedido.PENDENTE)) throw new RegraDeNegocioException("Usuário já tem um pedido pendente");

        Pedido pedido = new Pedido(usuario);
        Pedido pedidoSalvo = pedidoRepository.save(pedido);
        return pedidoMapper.toAbrirPedidoResponseDto(pedidoSalvo);
    }

    @Transactional
    public void desativarUsuario(UUID id){
        Usuario usuario = repository.findById(id).orElseThrow(() -> getUsuarioNaoEncontradoException(id));
        validator.validarDesativacao(usuario);
        usuario.setAtivo(false);
    }

    public Optional<Usuario> obterPorLogin(String login) {
        return repository.findByEmail(login);
    }

    private static @NonNull NaoEncontradoException getUsuarioNaoEncontradoException(UUID id) {
        return new NaoEncontradoException("Não existe usuário com id: " + id.toString());
    }
}