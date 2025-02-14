package tcc2.loginservice.login.dto;

import jakarta.validation.constraints.NotBlank;

public class ResetPasswordDTO {

  @NotBlank(message = "O token é obrigatório")
  private String token;

  @NotBlank(message = "A nova senha é obrigatória")
  private String newPassword;

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getNewPassword() {
    return newPassword;
  }

  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }
}