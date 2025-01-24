package tcc2.loginservice.login.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tcc2.loginservice.login.models.User;
import tcc2.loginservice.login.models.UserRole;
import tcc2.loginservice.login.repositories.UserRepository;

// @RestController
// @RequestMapping("/user")
// public class UserController {
//   @GetMapping
//   public ResponseEntity<String> getUser() {
//     return ResponseEntity.ok("sucesso!");
//   }
// }

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/students")
    public ResponseEntity<List<User>> getStudents() {
        // Busca os usuários onde role é ALUNO
        List<User> students = userRepository.findByRole(UserRole.ALUNO);
        return ResponseEntity.ok(students);
    }
}


