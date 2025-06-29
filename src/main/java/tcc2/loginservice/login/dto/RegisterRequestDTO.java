package tcc2.loginservice.login.dto;

import tcc2.loginservice.login.models.UserRole;

//dto para registrar ususario
public record RegisterRequestDTO(String email, String password, UserRole role, String name) {

}