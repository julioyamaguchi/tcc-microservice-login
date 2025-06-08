package tcc2.loginservice.login.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import tcc2.loginservice.login.infra.security.TokenService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import tcc2.loginservice.login.dto.EmailDTO;
import tcc2.loginservice.login.dto.LoginRequestDTO;
import tcc2.loginservice.login.dto.RegisterRequestDTO;
import tcc2.loginservice.login.dto.ResetPasswordDTO;
import tcc2.loginservice.login.dto.ResponseDTO;
import tcc2.loginservice.login.dto.ResponseErrorDTO;
import tcc2.loginservice.login.models.User;
import tcc2.loginservice.login.models.UserRole;
import tcc2.loginservice.login.repositories.UserRepository;
import tcc2.loginservice.login.services.EmailService;
import tcc2.loginservice.login.services.UserService;

@RestController
@RequestMapping("api/auth")
public class AuthController {

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private UserRepository repository;

  @Autowired
  private TokenService tokenService;

  @Autowired
  private UserService userService;

  @Autowired
  private EmailService emailService;

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDTO data) {

    try {
      User user = (User) repository.findByEmail(data.email());
      if (user == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ResponseErrorDTO(HttpStatus.UNAUTHORIZED.name(), "Email não cadastrado."));

      }

      // Cria o token de autenticação com base nos dados de login e senha
      var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());

      // Usa o authenticationManager para autenticar o usuário
      var auth = this.authenticationManager.authenticate(usernamePassword);

      var token = tokenService.generateToken((User) auth.getPrincipal());
      var refreshToken = tokenService.generateRefreshToken((User) auth.getPrincipal());

      // Retorna o token gerado no corpo da resposta
      return ResponseEntity.ok(new ResponseDTO(token, refreshToken, user));

    } catch (BadCredentialsException ex) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new ResponseErrorDTO(HttpStatus.UNAUTHORIZED.name(), "Email ou senha incorretos."));
    }
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody @Valid RegisterRequestDTO body) {
    if (repository.findByEmail(body.email()) != null) {
      // Utiliza o ResponseErrorDTO para padronizar a mensagem de erro
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new ResponseErrorDTO(HttpStatus.BAD_REQUEST.name(), "E-mail já cadastrado."));
    }

    // Verifica se o nome de usuário já está cadastrado (ignora maiúsculas,
    // minúsculas e espaços)
    if (repository.findByNameIgnoreCase(body.name().trim()) != null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new ResponseErrorDTO(HttpStatus.BAD_REQUEST.name(), "Nome de usuário já cadastrado."));
    }

    try {
      // Converte o role recebido para minúsculas antes de validar
      UserRole role = UserRole.valueOf(body.role().name().toUpperCase());

      // Criptografa a senha
      String encryptedPassword = new BCryptPasswordEncoder().encode(body.password());

      // Cria o novo usuário
      User newUser = new User(body.email(), encryptedPassword, role, body.name());
      repository.save(newUser);

      return ResponseEntity.ok().body("Usuário registrado com sucesso.");
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body("Tipo de usuário inválido. Use: 'admin', 'aluno' ou 'professor'.");
    }
  }

  @PostMapping("/forgot-password")
  public ResponseEntity<String> resetPassword(@RequestBody EmailDTO emailDTO, HttpServletRequest request) {
    String email = emailDTO.getEmail();

    User user = this.repository.findUserByEmail(email);
    if (user != null) {
      var token = tokenService.generateTokenEmail(email);
      String resetPasswordLink = "http://localhost:3030/reset-password?token=" + token;
      emailService.enviarEmailTexto(email, "🏛️ ConectaTCC - Recuperação de senha", resetPasswordLink);
      return ResponseEntity.ok().build();

    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @PostMapping("/reset-password")
  public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordDTO request) {
    try {
      // Verifica o token enviado
      String token = request.getToken();
      String email = tokenService.validateToken(token); // Garante que o token é válido

      // Busca o usuário pelo e-mail (extraído do token)
      User user = repository.findUserByEmail(email);
      if (user == null) {
        return ResponseEntity.notFound().build();
      }

      // Atualiza a senha
      userService.updatePassword(user, request.getNewPassword());

      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().build(); // Token inválido ou outro erro
    }
  }

  @PostMapping("/refresh-token")
  public ResponseEntity refreshToken(@RequestBody String refreshToken) {
    // Valida o refresh token
    String email = tokenService.validateToken(refreshToken);

    if (email == null) {
      return ResponseEntity.status(403).body("Invalid refresh token");
    }

    // Gera novos tokens
    User user = (User) repository.findByEmail(email);
    String newToken = tokenService.generateToken(user);
    String newRefreshToken = tokenService.generateRefreshToken(user);

    return ResponseEntity.ok(new ResponseDTO(newToken, newRefreshToken, user));
  }
}
