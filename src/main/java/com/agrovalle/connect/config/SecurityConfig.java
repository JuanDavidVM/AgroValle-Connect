package com.agrovalle.connect.config;

import com.agrovalle.connect.security.JwtAuthenticationFilter;
import com.agrovalle.connect.security.JwtService;
import com.agrovalle.connect.security.RestAccessDeniedHandler;
import com.agrovalle.connect.security.RestAuthenticationEntryPoint;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configuración de seguridad web: API sin estado, autenticación JWT, CORS y
 * exposición de la documentación OpenAPI según el entorno.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] RUTAS_SWAGGER = {
        "/swagger-ui.html",
        "/swagger-ui/**",
        "/v3/api-docs/**"
    };

    private static final long CORS_MAX_AGE_SEGUNDOS = 3600L;

    /**
     * Define las reglas de seguridad: API sin estado, CORS, rutas públicas y autenticación JWT.
     *
     * @param http                 configurador de seguridad HTTP
     * @param jwtService           servicio de tokens
     * @param userDetailsService   servicio de carga de usuarios
     * @param entryPoint           manejador de 401
     * @param accessDeniedHandler  manejador de 403
     * @param swaggerPublico       si la documentación OpenAPI se expone sin autenticación
     * @return la cadena de filtros configurada
     * @throws Exception si la configuración falla
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtService jwtService,
                                                   UserDetailsService userDetailsService,
                                                   RestAuthenticationEntryPoint entryPoint,
                                                   RestAccessDeniedHandler accessDeniedHandler,
                                                   @Value("${app.swagger.publico:true}") boolean swaggerPublico)
            throws Exception {
        http
                // Spring Security localiza por nombre el bean 'corsConfigurationSource'.
                // No se inyecta por tipo porque HandlerMappingIntrospector también
                // implementa CorsConfigurationSource y la inyección sería ambigua.
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    // El preflight CORS viaja sin credenciales por definición: si se exigiera
                    // autenticación, ningún navegador podría llegar a enviar el token.
                    auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
                    auth.requestMatchers(HttpMethod.POST,
                            "/api/v1/agricultores",
                            "/api/v1/compradores",
                            "/api/v1/auth/login").permitAll();
                    if (swaggerPublico) {
                        auth.requestMatchers(RUTAS_SWAGGER).permitAll();
                    }
                    auth.requestMatchers("/error").permitAll()
                            .requestMatchers(HttpMethod.POST, "/api/v1/fincas").hasRole("AGRICULTOR")
                            .requestMatchers(HttpMethod.GET, "/api/v1/productos", "/api/v1/productos/*").permitAll()
                            .requestMatchers(HttpMethod.POST, "/api/v1/productos").hasRole("AGRICULTOR")
                            .anyRequest().authenticated();
                })
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(entryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .addFilterBefore(new JwtAuthenticationFilter(jwtService, userDetailsService),
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * Orígenes autorizados para consumir la API desde un navegador (AV-020).
     *
     * <p>El nombre del bean importa: Spring Security lo busca exactamente como
     * 'corsConfigurationSource'. La lista llega por configuración para que cada
     * entorno declare la suya: localhost en desarrollo, el dominio real del
     * frontend en staging.
     *
     * @param origenes lista de orígenes permitidos
     * @return la fuente de configuración CORS aplicada a /api/**
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origins}") List<String> origenes) {
        CorsConfiguration configuracion = new CorsConfiguration();
        configuracion.setAllowedOrigins(origenes);
        configuracion.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuracion.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuracion.setExposedHeaders(List.of("Location"));
        // La API es sin estado y autentica con Bearer, no con cookies.
        configuracion.setAllowCredentials(false);
        configuracion.setMaxAge(CORS_MAX_AGE_SEGUNDOS);

        UrlBasedCorsConfigurationSource fuente = new UrlBasedCorsConfigurationSource();
        fuente.registerCorsConfiguration("/api/**", configuracion);
        return fuente;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
