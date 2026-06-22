package cl.pymetrack.msuser.model;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserConstructorAndGettersSetters() {
        User user = new User("test@caltias.cl", "passwordHashDe60Caracteres...............................", "Matías", Role.ADMIN);
        user.setId(1L);
        user.setApellido("Suazo");
        user.setActivo(true);
        user.setPymeId(100L);

        assertEquals(1L, user.getId());
        assertEquals("test@caltias.cl", user.getEmail());
        assertEquals("test@caltias.cl", user.getUsername());
        assertEquals("Matías", user.getNombre());
        assertEquals("Suazo", user.getApellido());
        assertEquals(Role.ADMIN, user.getRol());
        assertEquals(100L, user.getPymeId());
        assertTrue(user.getActivo());
    }

    @Test
    void testFullName() {
        User user = new User();
        
        // Caso solo nombre
        user.setNombre("Matías");
        user.setApellido(null);
        assertEquals("Matías", user.getFullName());

        // Caso nombre y apellido
        user.setApellido("Suazo");
        assertEquals("Matías Suazo", user.getFullName());
        
        // Caso apellido vacío
        user.setApellido("   ");
        assertEquals("Matías", user.getFullName());
    }

    @Test
    void testUserDetailsMethods() {
        User user = new User("test@caltias.cl", "pass", "Mat", Role.PYME);
        
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
        
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        assertEquals(1, authorities.size());
        assertEquals("ROLE_PYME", authorities.iterator().next().getAuthority());
    }
}