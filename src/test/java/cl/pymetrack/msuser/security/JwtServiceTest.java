package cl.pymetrack.msuser.security;

import cl.pymetrack.msuser.model.Role;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    private JwtService jwtService;
    private final String SECRET = "1234567890123456789012345678901234567890"; // 40 chars para HS256
    private final String EMAIL = "matias@caltias.cl";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Inyectamos las configuraciones manualmente
        ReflectionTestUtils.setField(jwtService, "secret", SECRET);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3600L);
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", 604800L);
    }

    @Test
    void testGenerateAndValidateToken_Exitoso() {
        List<String> perms = Arrays.asList("READ", "WRITE");
        String token = jwtService.generateToken(1L, EMAIL, "Matías", Role.PYME, 10L, perms);

        assertNotNull(token);
        assertTrue(jwtService.validateToken(token, EMAIL));
        assertEquals(EMAIL, jwtService.extractUsername(token));
        assertEquals(Role.PYME, jwtService.extractRole(token));
    }

    @Test
    void testExtractUserInfo_Exitoso() {
        List<String> perms = Arrays.asList("ADMIN");
        String token = jwtService.generateToken(1L, EMAIL, "Matías", Role.ADMIN, 10L, perms);

        JwtService.UserInfo info = jwtService.extractUserInfo(token);

        assertEquals(1L, info.getUserId());
        assertEquals(EMAIL, info.getEmail());
        assertEquals(Role.ADMIN, info.getRole());
        assertEquals(10L, info.getPymeId());
        assertTrue(info.getPermissions().contains("ADMIN"));
    }

    @Test
    void testRefreshToken_Flow() {
        String refreshToken = jwtService.generateRefreshToken(EMAIL, Role.REPARTIDOR);

        assertNotNull(refreshToken);
        assertTrue(jwtService.validateRefreshToken(refreshToken));
        assertTrue(jwtService.isRefreshToken(refreshToken));
    }

    @Test
    void testValidateToken_TokenInvalido() {
        // Token malformado
        assertFalse(jwtService.validateToken("esto-no-es-un-token", EMAIL));
    }

    @Test
    void testGetExpirationMethods() {
        assertEquals(3600L, jwtService.getExpirationInSeconds());
        assertEquals(604800L, jwtService.getRefreshExpirationInSeconds());
    }

    @Test
    void testExtractExpirationDate() {
        String token = jwtService.generateToken(1L, EMAIL, "Matías", Role.PYME, 10L, List.of());
        LocalDateTime expiration = jwtService.getExpirationDate(token);
        
        assertNotNull(expiration);
        assertTrue(expiration.isAfter(LocalDateTime.now()));
    }
}