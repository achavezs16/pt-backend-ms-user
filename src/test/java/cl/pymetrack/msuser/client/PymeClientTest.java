package cl.pymetrack.msuser.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PymeClientTest {

    @Autowired(required = false)
    private PymeClient pymeClient;

    @Test
    void clientLoads() {
        assertNotNull(pymeClient, "El PymeClient debería cargarse correctamente");
    }
    
    // Si tienes tiempo, este test ejecuta el logger y el try-catch
    @Test
    void testGetPymeByEmail_Empty() {
        // Al pasar un email vacío, forzamos la ejecución del bloque try-catch y el logger
        var result = pymeClient.getPymeByEmail("invalido@test.com");
        assertTrue(result.isEmpty());
    }
}