package cl.pymetrack.msuser.model;

import jakarta.persistence.*;

@Entity
@Table(name = "role_permissions")
public class RolePermission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "rol")
    private Role rol;
    
    @Column(name = "permission_id")
    private Long permissionId;
    
    // Constructors
    public RolePermission() {}
    
    public RolePermission(Role rol, Long permissionId) {
        this.rol = rol;
        this.permissionId = permissionId;
    }
    
    // Getters and Setters
    public Role getRol() {
        return rol;
    }
    
    public void setRol(Role rol) {
        this.rol = rol;
    }
    
    public Long getPermissionId() {
        return permissionId;
    }
    
    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }
}
