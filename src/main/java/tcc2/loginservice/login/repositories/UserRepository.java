package tcc2.loginservice.login.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;

import tcc2.loginservice.login.models.User;
import tcc2.loginservice.login.models.UserRole;

//para o JpaRepository a entidade que ele vai manipular e o tipo do id
public interface UserRepository extends JpaRepository<User, Long> {
  UserDetails findByEmail(String email);

  User findUserByEmail(String email);

  // Consulta
  @Query("SELECT u FROM users u WHERE u.role = :role")
  List<User> findByRole(@Param("role") UserRole role);  
}
