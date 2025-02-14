package tcc2.loginservice.login.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

  @Autowired
  private JavaMailSender javaMailSender;

  @Value("${spring.mail.username}")
  private String remetente;

  @Async // Torna o método assíncrono
  public void enviarEmailTexto(String destinatario, String assunto, String mensagem) {
    try {
      SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
      simpleMailMessage.setFrom(remetente);
      simpleMailMessage.setTo(destinatario);
      simpleMailMessage.setSubject(assunto);
      simpleMailMessage.setText(mensagem);
      javaMailSender.send(simpleMailMessage);
      System.out.println("Email enviado com sucesso para: " + destinatario);
    } catch (Exception e) {
      System.err.println("Erro ao tentar enviar email: " + e.getLocalizedMessage());
    }
  }
}
