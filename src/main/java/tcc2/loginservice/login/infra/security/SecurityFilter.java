
package tcc2.loginservice.login.infra.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tcc2.loginservice.login.models.User;
import tcc2.loginservice.login.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class SecurityFilter extends OncePerRequestFilter {
  @Autowired
  TokenService tokenService;
  @Autowired
  UserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    var token = this.recoverToken(request);

    if (token != null) {
      var email = tokenService.validateToken(token);
      UserDetails user = userRepository.findByEmail(email);

      // gera a autenticação do usuario para p spring, passando os dados do user e as
      // roles que ele tem permissão.
      var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
      // Agora o sprig security sabe as permissões
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    filterChain.doFilter(request, response);
  }

  private String recoverToken(HttpServletRequest request) {
    var authHeader = request.getHeader("Authorization");
    System.out.println("Auth Header: " + authHeader); // Log para verificar o cabeçalho

    if (authHeader == null)
      return null;
    return authHeader.replace("Bearer ", "");
  }
}

// O usuário faz uma requisição HTTP para um endpoint da aplicação.
// O filtro intercepta essa requisição.
// O filtro extrai o token JWT do cabeçalho Authorization.
// O filtro valida o token usando o TokenService.
// Se o token for válido, o filtro obtém os detalhes do usuário (como email e
// roles) a partir do banco de dados.
// O filtro cria um objeto de autenticação e o armazena no
// SecurityContextHolder, para que outras partes da aplicação possam usar essa
// informação (como roles para autorização).
// A requisição continua para o endpoint desejado.