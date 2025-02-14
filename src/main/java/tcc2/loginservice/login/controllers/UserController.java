package tcc2.loginservice.login.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
@RequestMapping("/api/auth/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/students")
    public ResponseEntity<List<User>> getStudents() {
        System.out.println("Endpoint /students acessado.");
        // Busca os usuários onde role é ALUNO
        List<User> students = userRepository.findByRole(UserRole.ALUNO);
        return ResponseEntity.ok(students);
    }

    // GET /api/auth/users - Retorna todos os usuários
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    // GET /api/auth/users/{id} - Retorna um usuário específico pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isPresent()){
             return ResponseEntity.ok(optionalUser.get());
        } else {
             return ResponseEntity.status(HttpStatus.NOT_FOUND)
                     .body("Usuário não encontrado.");
        }
    }
    

    // PUT /api/auth/users/{id} - Atualiza um usuário específico
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        return userRepository.findById(id).map(user -> {
            user.setName(updatedUser.getName());
            user.setEmail(updatedUser.getEmail());
            user.setRole(updatedUser.getRole());
            userRepository.save(user);
            return ResponseEntity.ok(user);
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // DELETE /api/auth/users/{id} - Deleta um usuário específico
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return userRepository.findById(id).map(user -> {
            userRepository.delete(user);
            return ResponseEntity.ok("Usuário deletado com sucesso.");
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado."));
    }

}


