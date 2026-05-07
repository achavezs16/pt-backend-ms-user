package cl.pymetrack.msuser.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public class LoginResponse {

    private String token;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private UserInfo userInfo;
    private Boolean requiresPasswordChange;
    private String message;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expiresAt;

    public LoginResponse() {}

    // Constructor para compatibilidad temporal
    public LoginResponse(String token, String refreshToken, Long userId, String nombre, String rutPyme) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.userInfo = new UserInfo(userId, nombre, null, null, rutPyme);
        this.message = "Login exitoso";
    }

    public LoginResponse(String token, Long expiresIn, LocalDateTime expiresAt, UserInfo userInfo) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.expiresAt = expiresAt;
        this.userInfo = userInfo;
        this.message = "Login exitoso";
    }

    // Getters y Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public Boolean getRequiresPasswordChange() {
        return requiresPasswordChange;
    }

    public void setRequiresPasswordChange(Boolean requiresPasswordChange) {
        this.requiresPasswordChange = requiresPasswordChange;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    // Métodos de compatibilidad
    public Long getUserId() {
        return userInfo != null ? userInfo.getId() : null;
    }

    public String getNombre() {
        return userInfo != null ? userInfo.getNombre() : null;
    }

    public String getRole() {
        return userInfo != null ? userInfo.getRole() : null;
    }

    public void setRole(String role) {
        if (userInfo != null) {
            userInfo.setRole(role);
        }
    }

    public List<String> getPermissions() {
        return userInfo != null ? userInfo.getPermissions() : null;
    }

    public void setPermissions(List<String> permissions) {
        if (userInfo != null) {
            userInfo.setPermissions(permissions);
        }
    }

    public String getRutPyme() {
        return userInfo != null ? userInfo.getRutPyme() : null;
    }

    public void setRutPyme(String rutPyme) {
        if (userInfo != null) {
            userInfo.setRutPyme(rutPyme);
        }
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "token='" + token + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", tokenType='" + tokenType + '\'' +
                ", expiresIn=" + expiresIn +
                ", userInfo=" + userInfo +
                ", requiresPasswordChange=" + requiresPasswordChange +
                ", message='" + message + '\'' +
                ", expiresAt=" + expiresAt +
                '}';
    }
}
