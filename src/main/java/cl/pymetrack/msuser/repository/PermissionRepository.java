package cl.pymetrack.msuser.repository;

import cl.pymetrack.msuser.model.Permission;
import cl.pymetrack.msuser.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    
    Optional<Permission> findByNombre(String nombre);
    
    boolean existsByNombre(String nombre);
    
    @Query("SELECT p FROM Permission p JOIN RolePermission rp ON p.id = rp.permissionId WHERE rp.rol = :rol")
    List<Permission> findByRole(@Param("rol") Role rol);
    
    @Query("SELECT p.nombre FROM Permission p JOIN RolePermission rp ON p.id = rp.permissionId WHERE rp.rol = :rol")
    List<String> findPermissionNamesByRole(@Param("rol") Role rol);
}
