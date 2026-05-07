package cl.pymetrack.msuser.dto;

import java.util.List;

public class UserInfo {
    
    private Long id;
    private String nombre;
    private String email;
    private String role;
    private List<String> permissions;
    private String rutPyme;
    private Long pymeId;

    public UserInfo() {}

    public UserInfo(Long id, String nombre, String email, String role, String rutPyme) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.role = role;
        this.rutPyme = rutPyme;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public String getRutPyme() {
        return rutPyme;
    }

    public void setRutPyme(String rutPyme) {
        this.rutPyme = rutPyme;
    }

    public Long getPymeId() {
        return pymeId;
    }

    public void setPymeId(Long pymeId) {
        this.pymeId = pymeId;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", permissions=" + permissions +
                ", rutPyme='" + rutPyme + '\'' +
                ", pymeId=" + pymeId +
                '}';
    }
}
