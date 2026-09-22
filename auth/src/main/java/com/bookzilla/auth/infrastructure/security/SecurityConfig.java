package com.bookzilla.auth.infrastructure.security;



import com.bookzilla.auth.application.port.out.UserRepository;
import com.bookzilla.auth.domain.Role;
import com.bookzilla.auth.domain.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collections;
import java.util.Set;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/auth/**"
                        ).permitAll()
                        .requestMatchers("/api/user/**").hasRole(Role.USER.name())
                        .requestMatchers("/api/admin/**").hasRole(Role.ADMIN.name())
                        .anyRequest().authenticated()
                )
                .build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {

        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

                User user = userRepository
                        .findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User with email = " + username + " not found"));

                Set<SimpleGrantedAuthority> roles = Collections.singleton(user.getRole().toAuthority());

                return new org.springframework.security.core.userdetails.User(
                        user.getEmail(),
                        user.getPassword(),
                        roles);
            }
        };

    }

    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            BCryptPasswordEncoder bCryptPasswordEncoder){

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(bCryptPasswordEncoder);

        return new ProviderManager(provider);
    }
}