package tcc2.loginservice.login.dto;

import tcc2.loginservice.login.models.UserRole;

//dto para registrar ususario
//temos que adicionar data aqui?
public record RegisterRequestDTO(String email, String password, UserRole role, String name) {

}
