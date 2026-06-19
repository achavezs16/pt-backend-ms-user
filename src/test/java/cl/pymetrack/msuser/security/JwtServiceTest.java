package cl.pymetrack.msuser.security;

import cl.pymetrack.msuser.model.Role;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Inyectamos las propiedades manualmente
        ReflectionTestUtils.setField(jwtService, "secret", "mySecretKey123456789012345678901234567890");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 86400L);
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", 604800L);
    }

    @Test
    void shouldGenerateAndExtractTokenData() {
        Long userId = 1L;
        String email = "matias@test.com";
        String nombre = "Matías";
        Role role = Role.PYME; // Corregido: usando un rol válido
        Long pymeId = 100L;
        List<String> permissions = List.of("READ", "WRITE");

        String token = jwtService.generateToken(userId, email, nombre, role, pymeId, permissions);

        assertNotNull(token);
        assertEquals(email, jwtService.extractUsername(token));
        
        JwtService.UserInfo userInfo = jwtService.extractUserInfo(token);
        assertEquals(userId, userInfo.getUserId());
        assertEquals(role, userInfo.getRole());
        assertTrue(userInfo.getPermissions().contains("READ"));
    }

    @Test
    void shouldValidateCorrectToken() {
        String token = jwtService.generateToken(1L, "matias@test.com", "Matías", Role.PYME, 100L, List.of());
        
        assertTrue(jwtService.validateToken(token, "matias@test.com"));
    }

    @Test
    void shouldFailValidationForWrongUser() {
        String token = jwtService.generateToken(1L, "matias@test.com", "Matías", Role.PYME, 100L, List.of());
        
        assertFalse(jwtService.validateToken(token, "otro@test.com"));
    }

    @Test
    void shouldThrowExceptionForMalformedToken() {
        String malformedToken = "ey.eyJhbGciOiJIUzI1NiJ9.invalidSignature";
        
        assertThrows(JwtException.class, () -> {
            jwtService.extractUsername(malformedToken);
        });
    }
}