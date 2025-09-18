package org.burgas.entityreflection.config;

import lombok.RequiredArgsConstructor;
import org.burgas.entityreflection.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.burgas.entityreflection.entity.identity.Authority.ADMIN;
import static org.burgas.entityreflection.entity.identity.Authority.USER;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(this.customUserDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(this.passwordEncoder);
        return new ProviderManager(daoAuthenticationProvider);
    }

    @Bean
    public XorCsrfTokenRequestAttributeHandler xorCsrfTokenRequestAttributeHandler() {
        return new XorCsrfTokenRequestAttributeHandler();
    }

    @Bean
    public UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource() {
        return new UrlBasedCorsConfigurationSource();
    }

    @Bean
    public RequestAttributeSecurityContextRepository requestAttributeSecurityContextRepository() {
        return new RequestAttributeSecurityContextRepository();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.csrfTokenRequestHandler(this.xorCsrfTokenRequestAttributeHandler()))
                .cors(cors -> cors.configurationSource(this.urlBasedCorsConfigurationSource()))
                .httpBasic(
                        httpBasic -> httpBasic.securityContextRepository(
                                this.requestAttributeSecurityContextRepository()
                        )
                )
                .authenticationManager(this.authenticationManager())
                .authorizeHttpRequests(
                        requests -> requests

                                .requestMatchers(
                                        "/api/v1/security/csrf-token",

                                        "/api/v1/companies", "/api/v1/companies/by-id",

                                        "/api/v1/identities/create"
                                )
                                .permitAll()

                                .requestMatchers(
                                        "/api/v1/companies/create-update", "/api/v1/companies/delete",
                                        "/api/v1/companies/add-identities", "/api/v1/companies/add-identity",

                                        "/api/v1/identities", "/api/v1/identities/by-id",
                                        "/api/v1/identities/update", "/api/v1/identities/delete",
                                        "/api/v1/identities/add-machines", "/api/v1/identities/add-machine",

                                        "/api/v1/machines", "/api/v1/machines/by-id",
                                        "/api/v1/machines/create-update", "/api/v1/machines/delete",
                                        "/api/v1/machines/add-identities", "/api/v1/machines/add-identity",

                                        "/api/v1/wallets/by-identity", "/api/v1/wallets/by-id",
                                        "/api/v1/wallets/create-update", "/api/v1/wallets/delete",
                                        "/api/v1/wallets/deposit", "/api/v1/wallets/withdraw", "/api/v1/wallets/transfer",

                                        "/api/v1/operations/by-id", "/api/v1/operations/by-sender-wallet",
                                        "/api/v1/operations/by-receiver-wallet", "/api/v1/operations/delete"
                                )
                                .hasAnyAuthority(ADMIN.getAuthority(), USER.getAuthority())
                )
                .build();
    }
}
