package tcc2.loginservice.login.services;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import tcc2.loginservice.login.models.User;
import tcc2.loginservice.login.repositories.UserRepository;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  public void updatePassword(User user, String newPassword) {
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    String encodePassword = passwordEncoder.encode(newPassword);
    user.setPassword(encodePassword);

    userRepository.save(user);

  }
}