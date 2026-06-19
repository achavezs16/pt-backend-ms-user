package cl.pymetrack.msuser.model;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    void testConstructors() {
        User u1 = new User();
        assertNotNull(u1);

        User u2 = new User("mati@test.com", "pass12345678", "Mati", Role.PYME);
        assertEquals("mati@test.com", u2.getEmail());
        assertEquals("Mati", u2.getNombre());
        assertEquals(Role.PYME, u2.getRol());
        assertTrue(u2.isEnabled());
    }

    @Test
    void testGettersAndSetters() {
        User u = new User();
        u.setId(1L);
        u.setEmail("test@test.com");
        u.setPassword("pass12345678");
        u.setNombre("Mati");
        u.setApellido("Suazo");
        u.setRol(Role.ADMIN);
        u.setPymeId(99L);
        u.setActivo(false);

        assertEquals(1L, u.getId());
        assertEquals("test@test.com", u.getEmail());
        assertEquals("pass12345678", u.getPassword());
        assertEquals("Mati", u.getNombre());
        assertEquals("Suazo", u.getApellido());
        assertEquals(Role.ADMIN, u.getRol());
        assertEquals(99L, u.getPymeId());
        assertFalse(u.getActivo());
    }

    @Test
    void testUserDetailsInterface() {
        User u = new User();
        u.setRol(Role.PYME);
        u.setEmail("mati@test.com");
        u.setActivo(true);

        assertEquals("mati@test.com", u.getUsername());
        
        // Verifica la lógica de Authorities
        Collection<? extends GrantedAuthority> auths = u.getAuthorities();
        assertFalse(auths.isEmpty());
        assertEquals("ROLE_PYME", auths.iterator().next().getAuthority());

        assertTrue(u.isAccountNonExpired());
        assertTrue(u.isAccountNonLocked());
        assertTrue(u.isCredentialsNonExpired());
        assertTrue(u.isEnabled());
    }

    @Test
    void testGetFullName() {
        User u = new User();
        u.setNombre("Mati");
        
        // Caso 1: Sin apellido
        assertEquals("Mati", u.getFullName());

        // Caso 2: Con apellido
        u.setApellido("Suazo");
        assertEquals("Mati Suazo", u.getFullName());
        
        // Caso 3: Apellido con espacios (rama lógica del if)
        u.setApellido("   ");
        assertEquals("Mati", u.getFullName());
    }
}