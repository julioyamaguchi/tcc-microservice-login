package tcc2.loginservice.login.infra.security;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;

import tcc2.loginservice.login.models.User;

@Service
public class TokenService {

  // Indica que o valor esta sendo recuperado la do arquivo application.properties
  @Value("${api.security.token.secret}")
  private String secret;

  // metodo de criação de token
  public String generateToken(User user) {
    try {
      Algorithm algorithm = Algorithm.HMAC256(secret);// secret é um parametro passado para o algoritmo de hash
                                                      // incrementar uma segurança a mais, essa secret é unica para
                                                      // nossa aplicação, para conseguir descriptografar a menagem
                                                      // precisamos da secret.

      String token = JWT.create()
          .withIssuer("login-auth-api")// emissor do token (nome da aplicação)
          .withSubject(user.getEmail()) // usuario que recebe o token
          .withExpiresAt(this.genereteExpirationDate()) // tempo de expiração
          .sign(algorithm);
      return token;
    } catch (JWTCreationException exception) {
      throw new RuntimeException("Error while authenticating", exception);
    }
  }

  // validação do token quando o usuario enviar, se for valido retorna o email do
  // cliete para qm chamou
  public String validateToken(String token) {
    try {
      Algorithm algorithm = Algorithm.HMAC256(secret);
      return JWT.require(algorithm)
          .withIssuer("login-auth-api")
          .build()
          .verify(token)
          .getSubject();
    } catch (JWTVerificationException exception) {
      return null;
    }
  }

  // gera um tempo de expiração para o token
  private Instant genereteExpirationDate() {
    return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
  }
}
