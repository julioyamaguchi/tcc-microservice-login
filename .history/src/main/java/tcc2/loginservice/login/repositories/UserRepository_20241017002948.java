package tcc2.loginservice.login.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import tcc2.loginservice.login.models.User;

//para o JpaRepository a entidade que ele vai manipular e o tipo do id
public interface UserRepository extends JpaRepository<User, UUID> {
  UserDetails findByEmail(String email);

}
