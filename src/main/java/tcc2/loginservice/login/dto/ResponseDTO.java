package tcc2.loginservice.login.dto;

import tcc2.loginservice.login.models.User;

public record ResponseDTO(String token, String refreshToken, User user) {

}
