package cl.pymetrack.msuser.client;

import cl.pymetrack.msuser.client.PymeClient.PymeData;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class PymeDataTest {

    @Test
    void testPymeDataLogic() {
        PymeData data = new PymeData();
        
        // Prueba: requiresPasswordChange
        data.setEstado("FIRST_LOGIN_REQUIRED");
        assertTrue(data.requiresPasswordChange());
        
        data.setEstado("ACTIVE");
        data.setPassword(null);
        data.setTempPassword("123");
        assertTrue(data.requiresPasswordChange());

        // Prueba: isTempPasswordValid
        data.setTempPassword("123");
        data.setTempPasswordExpiresAt(LocalDateTime.now().plusHours(1));
        assertTrue(data.isTempPasswordValid());

        // Prueba: isAccountLocked
        data.setCuentaBloqueadaHasta(LocalDateTime.now().plusMinutes(10));
        assertTrue(data.isAccountLocked());
    }
}