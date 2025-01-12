package tcc2.loginservice.login.models;

public enum UserRole {
  ADMIN("admin"),
  ALUNO("aluno"),
  PROFESSOR("professor");

  private String role;

  UserRole(String role) {
    this.role = role;
  }

  public String getRole() {
    return role;
  }

  public static UserRole fromString(String role) {
    for (UserRole userRole : UserRole.values()) {
      if (userRole.getRole().equalsIgnoreCase(role)) {
        return userRole;
      }
    }
    throw new IllegalArgumentException("Invalid user role: " + role);
  }
}
