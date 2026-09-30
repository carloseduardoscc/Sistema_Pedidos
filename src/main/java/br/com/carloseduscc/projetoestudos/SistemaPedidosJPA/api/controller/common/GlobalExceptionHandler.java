package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.api.controller.common;

import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.dto.erro.CampoErro;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.dto.erro.ErroResposta;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.exception.NaoEncontradoException;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.exception.OperacaoNaoPermitidaException;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.exception.RegistroDuplicadoException;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.model.exception.RegraDeNegocioException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.core.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErroResposta handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.info("Erro do cliente: {}", e.getMessage());
        return new ErroResposta(
                HttpStatus.BAD_REQUEST.value(),
                "Erro na requisiçao",
                List.of(new CampoErro("Causa", e.getMessage()))
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErroResposta handleAuthenticationException(AuthenticationException e) {
        log.info("Erro por falta de autenticação: {}", e.getMessage());
        return new ErroResposta(
                HttpStatus.UNAUTHORIZED.value(),
                "Não autenticado / autenticação falhou",
                List.of(new CampoErro("Causa", e.getMessage()))
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErroResposta handleAccessDeniedException(AccessDeniedException e) {
        log.info("Erro de acesso negado / permissões insuficientes: {}", e.getMessage());
        return new ErroResposta(
                HttpStatus.FORBIDDEN.value(),
                "Acesso negado",
                List.of(new CampoErro("Causa", e.getMessage()))
        );
    }

    @ExceptionHandler(NaoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErroResposta handleNaoEncontradoException(NaoEncontradoException e) {
        log.info("Erro de recurso não encontrado: {}", e.getMessage());
        return new ErroResposta(
                HttpStatus.NOT_FOUND.value(),
                "Não encontrado",
                List.of(new CampoErro("Causa", e.getMessage()))
        );
    }

    @ExceptionHandler(OperacaoNaoPermitidaException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ErroResposta handleOperacaoNaoPermitidaException(OperacaoNaoPermitidaException ex) {
        log.info("Erro de operação não permitida: {}",ex.getMessage());
        return new ErroResposta(
                HttpStatus.CONFLICT.value(),
                "Operação não permitida",
                List.of(new CampoErro("Causa", ex.getMessage()))
        );
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ErroResposta handleRegraDeNegocioException(RegraDeNegocioException ex) {
        log.info("Erro de violação de política interna: {}",ex.getMessage());
        return new ErroResposta(
                HttpStatus.CONFLICT.value(),
                "Violação de política interna",
                List.of(new CampoErro("Causa", ex.getMessage()))
        );
    }

    @ExceptionHandler(RegistroDuplicadoException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ErroResposta handleRegistroDuplicadoException(RegistroDuplicadoException ex) {
        log.info("Erro de registro duplicado: {}", ex.getMessage());
        return new ErroResposta(
                HttpStatus.CONFLICT.value(),
                "Registro duplicado",
                List.of(new CampoErro("Causa", ex.getMessage()))
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    ErroResposta handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.info("Erro de validação: {}",e.getMessage());
        return new ErroResposta(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Erro de validação",
                e.getFieldErrors().stream()
                        .map(fe -> new CampoErro(fe.getField(), fe.getDefaultMessage()))
                        .collect(Collectors.toCollection(ArrayList::new))
        );
    }

    @ExceptionHandler(PropertyReferenceException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    ErroResposta handlePropertyReferenceException(PropertyReferenceException e) {
        log.info("Erro de validação: {}",e.getMessage());
        return new ErroResposta(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Parâmetro inválido",
                List.of(new CampoErro("Causa", e.getMessage()))
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    ErroResposta handleIllegalArgumentException(IllegalArgumentException e) {
        log.info("Erro de validação: {}",e.getMessage());
        return new ErroResposta(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Parâmetro inválido",
                List.of(new CampoErro("Causa", e.getMessage()))
        );
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    ErroResposta handleRuntimeException(RuntimeException e) {
        log.error("Erro interno inesperado", e);
        return new ErroResposta(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Ocorreu um erro inesperado, entre em contato com a administração.",
                List.of()
        );
    }
}