package cl.pymetrack.msuser.client;

import cl.pymetrack.msuser.exception.PymeServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PymeClientTest {

    private PymeClient pymeClient;

    @BeforeEach
    void setUp() {
        // Inicializamos con un puerto que no existe para probar los catch sin red
        pymeClient = new PymeClient("http://localhost:9999");
    }

    @Test
    void testPymeDataLogic() {
        PymeClient.PymeData data = new PymeClient.PymeData();
        
        // Cubrimos lógica de requiresPasswordChange
        data.setEstado("FIRST_LOGIN_REQUIRED");
        assertTrue(data.requiresPasswordChange());
        
        data.setEstado("ACTIVE");
        data.setPassword(null);
        data.setTempPassword("temp");
        assertTrue(data.requiresPasswordChange());

        // Cubrimos isTempPasswordValid
        data.setTempPasswordExpiresAt(LocalDateTime.now().plusHours(1));
        assertTrue(data.isTempPasswordValid());
        
        // Cubrimos isAccountLocked
        data.setCuentaBloqueadaHasta(LocalDateTime.now().plusHours(1));
        assertTrue(data.isAccountLocked());
        
        // Cubrimos toString y setters básicos
        data.setNombrePyme("PymeTest");
        data.setEmailContacto("test@test.com");
        data.setEstado("ACTIVE");
        assertNotNull(data.toString());
        assertEquals("PymeTest", data.getNombrePyme());
    }

    @Test
    void testGetPymeByEmail_ErrorHandling() {
        // Al intentar conectar a localhost:9999 (que no tiene nada), 
        // el WebClient lanzará error y nuestro catch cubrirá la lógica de PymeServiceException
        assertThrows(PymeServiceException.class, () -> {
            pymeClient.getPymeByEmail("test@test.com");
        });
    }

    @Test
    void testChangePassword_ErrorHandling() {
        assertThrows(PymeServiceException.class, () -> {
            pymeClient.changePassword(1L, "old", "new");
        });
    }

    @Test
    void testFailedLoginAndReset_NoLanzaExcepcion() {
        // Estos métodos tienen un catch vacío, probamos que no rompan el flujo
        assertDoesNotThrow(() -> pymeClient.registerFailedLogin("test@test.com"));
        assertDoesNotThrow(() -> pymeClient.resetFailedAttempts(1L));
    }
}