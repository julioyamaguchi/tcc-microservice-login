package tcc2.loginservice.login.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // indica que é uma classe de configuração para o spring
@EnableWebSecurity // indica a configuração manual do websecurity
public class SecurityConfig {

  @Autowired
  SecurityFilter securityFilter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/auth/reset-password").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/auth/users/students").authenticated()

            .requestMatchers(HttpMethod.GET, "/api/auth/users").authenticated() // retrona todos os usuários
            .requestMatchers(HttpMethod.GET, "/api/auth/users/students").authenticated() // retorna todos os alunos
            .requestMatchers(HttpMethod.GET, "/api/auth/users/teachers").authenticated() // retorna todos os alunos
            .requestMatchers(HttpMethod.PUT, "/api/auth/users/**").authenticated() // atualiza um usuário
            .requestMatchers(HttpMethod.DELETE, "/api/auth/users/**").authenticated() // deleta um usuário
            .requestMatchers(HttpMethod.POST, "/auth/forgot-password").permitAll()
            .requestMatchers(HttpMethod.POST, "api/auth/forgot-password").permitAll()
            .requestMatchers(HttpMethod.POST, "api/auth/reset-password").permitAll()

            .anyRequest().authenticated())
        .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
      throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }
}
