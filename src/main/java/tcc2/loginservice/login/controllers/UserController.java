package tcc2.loginservice.login.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import tcc2.loginservice.login.dto.UserSyncDTO;
import tcc2.loginservice.login.models.User;
import tcc2.loginservice.login.models.UserRole;
import tcc2.loginservice.login.repositories.UserRepository;

// Controlador REST para ações sobre o usuário autenticado (consulta, edição, remoção, etc)
@RestController
@RequestMapping("/api/auth/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // Lista todos os usuários com papel de aluno
    @GetMapping("/students")
    public ResponseEntity<List<User>> getStudents() {
        System.out.println("Endpoint /students acessado.");
        List<User> students = userRepository.findByRole(UserRole.ALUNO);
        return ResponseEntity.ok(students);
    }

    // Lista todos os usuários com papel de professor
    @GetMapping("/teachers")
    public ResponseEntity<List<User>> getTeacher() {
        List<User> teachers = userRepository.findByRole(UserRole.PROFESSOR);
        return ResponseEntity.ok(teachers);
    }

    // Lista todos os usuários cadastrados
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    // Busca um usuário pelo seu ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            return ResponseEntity.ok(optionalUser.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuário não encontrado.");
        }
    }

    // Atualiza os dados de um usuário existente e sincroniza a alteração com outros microsserviços
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser,
            HttpServletRequest request) {
        return userRepository.findById(id).map(user -> {
            String oldName = user.getName(); // salva o nome antigo

            user.setName(updatedUser.getName());
            user.setEmail(updatedUser.getEmail());
            user.setRole(updatedUser.getRole());
            userRepository.save(user);

            // Sincroniza com os outros microsserviços
            RestTemplate restTemplate = new RestTemplate();
            String token = request.getHeader("Authorization");

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Cria objeto DTO de sincronização para enviar os dados
            UserSyncDTO syncData = new UserSyncDTO();
            syncData.setId(user.getId());
            syncData.setName(user.getName());
            syncData.setOldName(oldName);
            syncData.setEmail(user.getEmail());
            syncData.setRole(user.getRole().name());

            HttpEntity<UserSyncDTO> entity = new HttpEntity<>(syncData, headers);

            // Endereços dos endpoints dos outros microsserviços que receberão a atualização (passando pela api gateway)
            String universityUrl = "http://localhost:3000/api/university/users/sync";
            String clusteringUrl = "http://localhost:3000/api/cluster/users/sync";

            // Envia a atualização para os outros microsserviços
            try {
                restTemplate.exchange(universityUrl, HttpMethod.PUT, entity, Void.class);
                restTemplate.exchange(clusteringUrl, HttpMethod.PUT, entity, Void.class);
                System.out.println("Usuário " + id + " atualizado nos microsserviços.");
            } catch (Exception e) {
                System.err.println("Erro ao sincronizar atualização do usuário " + id + ": " + e.getMessage());
            }

            return ResponseEntity.ok(user);
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Remove um usuário do sistema e sincroniza a exclusão com outros microsserviços
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, HttpServletRequest request) {
        return userRepository.findById(id).map(user -> {
            userRepository.delete(user);

            // Uso de RestTemplate para enviar requisições para o gateway (3000)
            RestTemplate restTemplate = new RestTemplate();
            String name = user.getName();
            String universityUrl = "http://localhost:3000/api/university/users/remove/" + id + "/" + name;
            String clusteringUrl = "http://localhost:3000/api/cluster/users/remove-preferences/" + id;

            String token = request.getHeader("Authorization");
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            // Envia a requisição de remoção para os outros microsserviços (os ms vão receber da api gateway)
            try {
                restTemplate.exchange(universityUrl, HttpMethod.DELETE, entity, Void.class);
                restTemplate.exchange(clusteringUrl, HttpMethod.DELETE, entity, Void.class);
                System.out.println("Usuário " + id + " removido dos outros microsserviços.");
            } catch (Exception e) {
                System.err.println("Erro ao sincronizar exclusão do usuário " + id + ": " + e.getMessage());
            }

            return ResponseEntity.ok("Usuário deletado com sucesso.");
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado."));
    }

    // Conta quantos usuários existem por papel (admin, aluno, professor)
    @GetMapping("/count-by-role")
    public ResponseEntity<Map<String, Long>> countByRole() {
        Map<String, Long> response = new HashMap<>();
        response.put("admin", userRepository.countByRole(UserRole.ADMIN));
        response.put("student", userRepository.countByRole(UserRole.ALUNO));
        response.put("teacher", userRepository.countByRole(UserRole.PROFESSOR));
        return ResponseEntity.ok(response);
    }
}