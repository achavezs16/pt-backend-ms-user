package cl.pymetrack.msuser.dto;

public class ChangePasswordResponse {

    private Boolean success;
    private String message;
    private String token;
    private String tokenType = "Bearer";
    private Long expiresIn;

    public ChangePasswordResponse() {}

    public ChangePasswordResponse(String message) {
        this.success = true;
        this.message = message;
    }

    public ChangePasswordResponse(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ChangePasswordResponse(Boolean success, String message, String token, Long expiresIn) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.expiresIn = expiresIn;
    }

    // Getters y Setters
    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    @Override
    public String toString() {
        return "ChangePasswordResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", token='" + token + '\'' +
                ", tokenType='" + tokenType + '\'' +
                ", expiresIn=" + expiresIn +
                '}';
    }
}
