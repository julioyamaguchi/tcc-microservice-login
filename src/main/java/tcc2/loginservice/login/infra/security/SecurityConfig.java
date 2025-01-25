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
            .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll() // endpoint aberto para qualquer um fazer
            // requisição
            .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll() // endpoint aberto para qualquer um
            // fazer
            // requisição
            .requestMatchers(HttpMethod.GET, "/api/users/students").permitAll() // Permite acesso público aos alunos

            .requestMatchers(HttpMethod.GET, "/api/auth/listarTodosUsuarios").permitAll() //permite que possa ser realizado a listagem pelo adm

            .requestMatchers(HttpMethod.PUT, "/api/auth/atualizarUsuario/**").permitAll() //permite que possa ser realizado a atualização pelo adm

            .requestMatchers(HttpMethod.DELETE, "/api/auth/deletarUsuario/**").permitAll() //permite que possa ser realizado o delete pelo adm

            .anyRequest().authenticated())
        // filtro que verifica antes o token do usuario e depois passa para as
        // validações acima
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
