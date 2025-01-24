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

  @Value("${api.security.token.secret}")
  private String secret;

  // Gera o token de acesso
  public String generateToken(User user) {
    return createToken(user, 2); // Expiração de 2 horas para o token de acesso
  }

  // Gera o refresh token
  public String generateRefreshToken(User user) {
    return createToken(user, 24); // Expiração mais longa, ex.: 24 horas para o refresh token
  }

  private String createToken(User user, int hoursToExpire) {
    try {
      Algorithm algorithm = Algorithm.HMAC256(secret);
      return JWT.create()
          .withIssuer("login-auth-api")
          .withSubject(user.getEmail())
          .withExpiresAt(LocalDateTime.now().plusHours(hoursToExpire).toInstant(ZoneOffset.of("-03:00")))
          .sign(algorithm);
    } catch (JWTCreationException exception) {
      throw new RuntimeException("Error while generating token", exception);
    }
  }

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

}
