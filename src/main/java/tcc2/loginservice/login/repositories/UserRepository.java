package tcc2.loginservice.login.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;

import tcc2.loginservice.login.models.User;
import tcc2.loginservice.login.models.UserRole;

// Interface de repositório para operações no banco de dados da entidade User
// O JpaRepository já fornece métodos CRUD prontos, e os métodos abaixo são buscas personalizadas
public interface UserRepository extends JpaRepository<User, Long> {
  UserDetails findByEmail(String email);

  User findUserByEmail(String email);

  User findByNameIgnoreCase(String name);

  long countByRole(UserRole role);

  // Consulta
  @Query("SELECT u FROM users u WHERE u.role = :role")
  List<User> findByRole(@Param("role") UserRole role);
}
