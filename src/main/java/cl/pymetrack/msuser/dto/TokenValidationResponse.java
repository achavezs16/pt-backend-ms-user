package cl.pymetrack.msuser.dto;

import cl.pymetrack.msuser.security.JwtService;

public class TokenValidationResponse {

    private Boolean valid;
    private String message;
    private JwtService.UserInfo userInfo;

    public TokenValidationResponse() {}

    public TokenValidationResponse(Boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }

    public TokenValidationResponse(Boolean valid, String message, JwtService.UserInfo userInfo) {
        this.valid = valid;
        this.message = message;
        this.userInfo = userInfo;
    }

    // Getters y Setters
    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public JwtService.UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(JwtService.UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public String toString() {
        return "TokenValidationResponse{" +
                "valid=" + valid +
                ", message='" + message + '\'' +
                ", userInfo=" + userInfo +
                '}';
    }
}
