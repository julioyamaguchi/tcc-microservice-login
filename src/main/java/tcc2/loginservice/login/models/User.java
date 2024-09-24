package tcc2.loginservice.login.models;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "users")
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;
  private String name;
  private String email;
  private String password;
  private Date birthDate;
  private Boolean isActive;
  private UserRole role;

  public User(String email, String password, UserRole role, String name) {
    this.email = email;
    this.password = password;
    this.role = role;
    this.name = name;
  }

  // definição das roles dos usuarios
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    switch (this.role) {
      case ADMIN:
        // O ADMIN recebe tanto ROLE_ADMIN quanto ROLE_USER
        return List.of(
            new SimpleGrantedAuthority("ROLE_ADMIN"),
            new SimpleGrantedAuthority("ROLE_USER"));
      case PROFESSOR:
        // O PROFESSOR recebe tanto ROLE_PROFESSOR quanto ROLE_USER
        return List.of(
            new SimpleGrantedAuthority("ROLE_PROFESSOR"),
            new SimpleGrantedAuthority("ROLE_USER"));
      case ALUNO:
        // O ALUNO recebe ROLE_ALUNO e ROLE_USER
        return List.of(
            new SimpleGrantedAuthority("ROLE_ALUNO"),
            new SimpleGrantedAuthority("ROLE_USER"));
      default:
        // Em caso de algum erro ou papel não definido, por padrão retorno ROLE_USER
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

}
