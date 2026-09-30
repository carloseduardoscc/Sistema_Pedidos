package br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.config;

import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.api.controller.common.GlobalExceptionHandler;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.application.dto.erro.ErroResposta;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.security.AuthenticationLoggingFilter;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.security.LoginSocialSuccessHandler;
import br.com.carloseduscc.projetoestudos.SistemaPedidosJPA.infra.security.RequestLoggingFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.session.DisableEncodeUrlFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.filter.CharacterEncodingFilter;
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

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            LoginSocialSuccessHandler loginSocialSuccessHandler,
            AuthenticationLoggingFilter authenticationLoggingFilter,
            RequestLoggingFilter requestLoggingFilter
    ) {
        return http
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable())
                )
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfig = new CorsConfiguration();
                    corsConfig.setAllowedOriginPatterns(allowedOrigins);
                    corsConfig.setAllowedMethods(List.of("*"));
                    corsConfig.setAllowedHeaders(List.of("*"));
                    corsConfig.setAllowCredentials(true);
                    return corsConfig;
                }))
                .httpBasic(Customizer.withDefaults())
                .formLogin(configurer -> {
                    configurer.loginPage("/login").permitAll();
                })
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(HttpMethod.POST, "/usuarios").permitAll();
                    auth.requestMatchers(HttpMethod.POST, "/login").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/produtos/**").permitAll();
                    auth.anyRequest().authenticated();
                })
                .oauth2Login(oauth -> {
                    oauth.loginPage("/login");
                    oauth.successHandler(loginSocialSuccessHandler);
                })
                .exceptionHandling(exception->exception
                        .accessDeniedHandler(accessDeniedHandler())
                        .authenticationEntryPoint(authenticationEntryPoint())
                )
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
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
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
}
