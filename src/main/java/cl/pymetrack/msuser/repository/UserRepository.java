package cl.pymetrack.msuser.repository;

import cl.pymetrack.msuser.model.User;
import cl.pymetrack.msuser.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    List<User> findByRol(Role rol);
    
    List<User> findByPymeId(Long pymeId);
    
    @Query("SELECT u FROM User u WHERE u.rol = :rol AND u.activo = true")
    List<User> findActiveUsersByRole(@Param("rol") Role rol);
    
    @Query("SELECT u FROM User u WHERE u.pymeId = :pymeId AND u.activo = true")
    List<User> findActiveUsersByPymeId(@Param("pymeId") Long pymeId);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.rol = :rol AND u.activo = true")
    long countActiveUsersByRole(@Param("rol") Role rol);
    
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.activo = true")
    Optional<User> findActiveUserByEmail(@Param("email") String email);
}
