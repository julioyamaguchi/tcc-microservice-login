package tcc2.loginservice.login.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import tcc2.loginservice.login.infra.security.TokenService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import tcc2.loginservice.login.dto.LoginRequestDTO;
import tcc2.loginservice.login.dto.RegisterRequestDTO;
import tcc2.loginservice.login.dto.ResetPasswordDTO;
import tcc2.loginservice.login.dto.ResponseDTO;
import tcc2.loginservice.login.models.User;
import tcc2.loginservice.login.models.UserRole;
import tcc2.loginservice.login.repositories.UserRepository;

@RestController
@RequestMapping("api/auth")
public class AuthController {

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private UserRepository repository;

  @Autowired
  private TokenService tokenService;

  @PostMapping("/login")
  public ResponseEntity<ResponseDTO> login(@RequestBody @Valid LoginRequestDTO data) {
    // Cria o token de autenticação com base nos dados de login e senha
    var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());

    // Usa o authenticationManager para autenticar o usuário
    var auth = this.authenticationManager.authenticate(usernamePassword);

    var token = tokenService.generateToken((User) auth.getPrincipal());
    var refreshToken = tokenService.generateRefreshToken((User) auth.getPrincipal());
    User user = (User) repository.findByEmail(data.email());

    // Retorna o token gerado no corpo da resposta
    return ResponseEntity.ok(new ResponseDTO(token, refreshToken, user));
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody @Valid RegisterRequestDTO body) {
    if (repository.findByEmail(body.email()) != null) {
      return ResponseEntity.badRequest().body("E-mail já cadastrado.");
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

  @PostMapping("/reset-password")
  public ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordDTO request) {
    // Verifica se o e-mail está registrado
    User user = repository.findUserByEmail(request.email());
    if (user == null) {
      return ResponseEntity.badRequest().build(); // Retorna 400 se o e-mail não for encontrado
    }
    String encryptedPassword = new BCryptPasswordEncoder().encode(request.newsenha());
    user.setPassword(encryptedPassword);
    repository.save(user); // Salva as alterações no banco de dados

    return ResponseEntity.ok().build();
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
