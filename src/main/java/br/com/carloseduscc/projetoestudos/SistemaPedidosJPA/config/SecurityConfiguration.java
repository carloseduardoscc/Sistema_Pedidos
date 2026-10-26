package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.config;

import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.api.controller.common.GlobalExceptionHandler;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.dto.erro.ErroResposta;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.security.OAuth2LoginSuccessHandler;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.security.filters.AuthenticationLoggingFilter;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.security.filters.JwtAuthenticationFilter;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.security.filters.RequestLoggingFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.DisableEncodeUrlFilter;
import org.springframework.web.cors.CorsConfiguration;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final GlobalExceptionHandler globalExceptionHandler;
    private final ObjectMapper objectMapper;
    @Value("${spring.security.allowed-origins[0]}")
    private List<String> allowedOrigins;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final AuthenticationLoggingFilter authenticationLoggingFilter;
    private final RequestLoggingFilter requestLoggingFilter;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable())
                )
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(_ -> {
                    var corsConfig = new CorsConfiguration();
                    corsConfig.setAllowedOriginPatterns(allowedOrigins);
                    corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                    corsConfig.setAllowedHeaders(List.of("Content-Type", "X-XSRF-TOKEN"));
                    corsConfig.setAllowCredentials(true);
                    return corsConfig;
                }))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(HttpMethod.POST, "/auth/login").permitAll();
                    auth.requestMatchers(HttpMethod.POST, "/auth/logout").permitAll();
                    auth.requestMatchers(HttpMethod.POST, "/auth/cadastrar").permitAll();
                    auth.requestMatchers(HttpMethod.POST, "/auth/refresh").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/csrf").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/produtos/**").permitAll();
                    auth.anyRequest().authenticated();
                })
                .oauth2Login(oauth -> {
                    oauth.successHandler(oAuth2LoginSuccessHandler);
                    oauth.authorizationEndpoint( endpoint ->{
                        endpoint.authorizationRequestResolver(
                                authorizationRequestResolver(clientRegistrationRepository)
                        );
                    });
                })
                .exceptionHandling(exception->exception
                        .accessDeniedHandler(accessDeniedHandler())
                        .authenticationEntryPoint(authenticationEntryPoint())
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(authenticationLoggingFilter, DisableEncodeUrlFilter.class)
                .addFilterAfter(requestLoggingFilter, AuthorizationFilter.class)
                .build();
    }

    @Bean
    @Profile("test")
    public WebSecurityCustomizer webSecurityCustomizer(){
        return web -> web.ignoring().requestMatchers(
                "/h2-console/**",
                "/v2/api-docs/**",
                "/v3/api-docs/**",
                "/swagger-resources/**",
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/webjars/**"
        );
    }

    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults(){
        return new GrantedAuthorityDefaults("");
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler(){
        return new AccessDeniedHandler() {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                ErroResposta erroResposta = globalExceptionHandler.handleAccessDeniedException(accessDeniedException);
                response.setStatus(erroResposta.status());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding("UTF-8");

                objectMapper.writeValue(response.getWriter(), erroResposta);
            }
        };
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint(){
        return new AuthenticationEntryPoint() {
            @Override
            public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                ErroResposta erroResposta = globalExceptionHandler.handleAuthenticationException(authException);
                response.setStatus(erroResposta.status());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding("UTF-8");

                objectMapper.writeValue(response.getWriter(), erroResposta);
            }
        };
    }

    private OAuth2AuthorizationRequestResolver
    authorizationRequestResolver(
            ClientRegistrationRepository repository) {

        var resolver = new DefaultOAuth2AuthorizationRequestResolver(
                repository,
                "/oauth2/authorization"
        );

        resolver.setAuthorizationRequestCustomizer(
                customizer -> customizer.additionalParameters(
                        params -> params.put(
                                "prompt",
                                "select_account"
                        )
                )
        );

        return resolver;
    }
}
