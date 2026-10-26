package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.api.controller.common;

import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.exception.RefreshTokenInvalidoException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;

public interface GenericController {

    default URI gerarHeaderLocation (String id){
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        return uri;
    }

    default String obterRefreshToken (HttpServletRequest request){
        if (request.getCookies() == null) {
            throw new RefreshTokenInvalidoException();
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> "refresh_token".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElseThrow(RefreshTokenInvalidoException::new);
    }

    default void setRefreshTokenCookie(HttpServletResponse response, String refreshToken, long refreshTokenExpirationTime){
        long refreshTokenExpirationTimeInSeconds = refreshTokenExpirationTime / 1000;
        ResponseCookie refreshCookie = ResponseCookie
                .from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(refreshTokenExpirationTimeInSeconds)
                .build();
        response.addHeader(
                HttpHeaders.SET_COOKIE,
                refreshCookie.toString()
        );
    }

    default void setAccessTokenCookie(HttpServletResponse response, String accessToken, long accessTokenExpirationTime) {
        long accessTokenExpirationTimeInSeconds = accessTokenExpirationTime / 1000;
        ResponseCookie accessCookie = ResponseCookie
                .from("access_token", accessToken)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(accessTokenExpirationTimeInSeconds)
                .build();
        response.addHeader(
                HttpHeaders.SET_COOKIE,
                accessCookie.toString()
        );
    }

    default void invalidateAccessTokenCookie(HttpServletResponse response) {
        response.addHeader(
                HttpHeaders.SET_COOKIE,
                ResponseCookie
                        .from("access_token", "")
                        .httpOnly(true)
                        .secure(false)
                        .sameSite("Lax")
                        .path("/")
                        .maxAge(0)
                        .build().toString()
        );
    }

    default void invalidateRefreshTokenCookie(HttpServletResponse response) {
        response.addHeader(
                HttpHeaders.SET_COOKIE,
                ResponseCookie
                        .from("refresh_token", "")
                        .httpOnly(true)
                        .secure(false)
                        .sameSite("Lax")
                        .path("/")
                        .maxAge(0)
                        .build().toString()
        );
    }
}
