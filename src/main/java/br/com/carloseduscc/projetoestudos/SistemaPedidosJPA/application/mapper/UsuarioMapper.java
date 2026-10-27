package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.mapper;

import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.dto.auth.CadastrarUsuarioDTO;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.dto.usuario.UsuarioDTO;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    UsuarioDTO toDTO(Usuario usuario);

    Usuario fromCommand(CadastrarUsuarioDTO usuario);
}
