package tcc2.loginservice.login.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import tcc2.loginservice.login.infra.security.TokenService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import tcc2.loginservice.login.dto.EmailDTO;
import tcc2.loginservice.login.dto.LoginRequestDTO;
import tcc2.loginservice.login.dto.RegisterRequestDTO;
import tcc2.loginservice.login.dto.ResetPasswordDTO;
import tcc2.loginservice.login.dto.ResponseDTO;
import tcc2.loginservice.login.models.User;
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
  public ResponseEntity<ResponseDTO> login(@RequestBody @Valid LoginRequestDTO data) {
    // Cria o token de autenticação com base nos dados de login e senha
    var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());

    // Usa o authenticationManager para autenticar o usuário
    var auth = this.authenticationManager.authenticate(usernamePassword);

    var token = tokenService.generateToken((User) auth.getPrincipal());

    // Retorna o token gerado no corpo da resposta
    return ResponseEntity.ok(new ResponseDTO(token));
  }

  @PostMapping("/register")
  public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequestDTO body) {

    if (this.repository.findByEmail(body.email()) != null)
      return ResponseEntity.badRequest().build();

    // criptografa a senha
    String encryptedPassword = new BCryptPasswordEncoder().encode(body.password());
    // gera o novo user
    User newUser = new User(body.email(), encryptedPassword, body.role(), body.name());

    this.repository.save(newUser);

    return ResponseEntity.ok().build();
  }

  // @PostMapping("/reset-password")
  // public ResponseEntity<Void> resetPassword(@RequestBody @Valid
  // ResetPasswordDTO request) {
  // // Verifica se o e-mail está registrado
  // User user = repository.findUserByEmail(request.email());
  // if (user == null) {
  // return ResponseEntity.badRequest().build(); // Retorna 400 se o e-mail não
  // for encontrado
  // }
  // String encryptedPassword = new
  // BCryptPasswordEncoder().encode(request.newsenha());
  // user.setPassword(encryptedPassword);
  // repository.save(user); // Salva as alterações no banco de dados

  // return ResponseEntity.ok().build();

  // }

  @PostMapping("/forgot-password")
  public ResponseEntity<String> resetPassword(@RequestBody EmailDTO emailDTO, HttpServletRequest request) {
    String email = emailDTO.getEmail();

    User user = this.repository.findUserByEmail(email);
    if (user != null) {
      var token = tokenService.generateTokenEmail(email);
      String resetPasswordLink = "http://localhost:3030/reset-password?token=" + token;
      emailService.enviarEmailTexto(email, "Recuperação de senha", resetPasswordLink);
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

  // // Método auxiliar para capturar a URL base do site //
  // public static String getSiteUrl(HttpServletRequest request) {
  // String siteURL = request.getRequestURL().toString();
  // return siteURL.replace(request.getServletPath(), "");
  // }

}
