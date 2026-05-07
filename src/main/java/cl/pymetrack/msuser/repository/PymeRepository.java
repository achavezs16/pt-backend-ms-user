package cl.pymetrack.msuser.repository;

import cl.pymetrack.msuser.model.Pyme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PymeRepository extends JpaRepository<Pyme, Long> {
    
    Optional<Pyme> findByRutPyme(String rutPyme);
    
    Optional<Pyme> findByEmailContactoPyme(String emailContactoPyme);
    
    boolean existsByRutPyme(String rutPyme);
    
    boolean existsByEmailContactoPyme(String emailContactoPyme);
}
