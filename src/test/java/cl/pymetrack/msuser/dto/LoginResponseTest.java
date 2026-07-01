package cl.pymetrack.msuser.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoginResponseTest {

    @Mock
    private UserInfo userInfo;

    @Test
    void testConstructorsAndGettersSetters() {
        LoginResponse response = new LoginResponse();
        response.setToken("token123");
        response.setRefreshToken("ref123");
        response.setTokenType("Bearer");
        response.setExpiresIn(3600L);
        response.setExpiresAt(LocalDateTime.now());
        response.setMessage("Éxito");
        response.setRequiresPasswordChange(false);
        response.setUserInfo(userInfo);

        assertEquals("token123", response.getToken());
        assertEquals("ref123", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(3600L, response.getExpiresIn());
        assertNotNull(response.getExpiresAt());
        assertEquals("Éxito", response.getMessage());
        assertFalse(response.getRequiresPasswordChange());
        assertEquals(userInfo, response.getUserInfo());
    }

    @Test
    void testParameterizedConstructors() {
        // Constructor compatibilidad
        LoginResponse res1 = new LoginResponse("t", "rt", 1L, "Nombre", "123-k");
        assertEquals("t", res1.getToken());
        
        // Constructor principal
        LoginResponse res2 = new LoginResponse("t", 3600L, LocalDateTime.now(), userInfo);
        assertEquals("t", res2.getToken());
        assertEquals(3600L, res2.getExpiresIn());
    }

    @Test
    void testDelegationMethodsWithUserInfo() {
        LoginResponse response = new LoginResponse();
        response.setUserInfo(userInfo);
        
        when(userInfo.getId()).thenReturn(1L);
        when(userInfo.getNombre()).thenReturn("Matías");
        when(userInfo.getRole()).thenReturn("ADMIN");
        when(userInfo.getPermissions()).thenReturn(List.of("READ"));
        when(userInfo.getRutPyme()).thenReturn("123-K");

        assertEquals(1L, response.getUserId());
        assertEquals("Matías", response.getNombre());
        assertEquals("ADMIN", response.getRole());
        assertEquals(List.of("READ"), response.getPermissions());
        assertEquals("123-K", response.getRutPyme());

        response.setRole("USER");
        verify(userInfo).setRole("USER");
        
        response.setPermissions(List.of("WRITE"));
        verify(userInfo).setPermissions(anyList());
        
        response.setRutPyme("999-K");
        verify(userInfo).setRutPyme("999-K");
    }

    @Test
    void testDelegationMethodsWithNullUserInfo() {
        LoginResponse response = new LoginResponse();
        response.setUserInfo(null);

        assertNull(response.getUserId());
        assertNull(response.getNombre());
        assertNull(response.getRole());
        assertNull(response.getPermissions());
        assertNull(response.getRutPyme());
        
        // Verificar que no lance NullPointerException al llamar setters con userInfo null
        assertDoesNotThrow(() -> response.setRole("ADMIN"));
        assertDoesNotThrow(() -> response.setPermissions(List.of()));
        assertDoesNotThrow(() -> response.setRutPyme("123"));
    }

    @Test
    void testToString() {
        LoginResponse response = new LoginResponse();
        assertNotNull(response.toString());
    }
}