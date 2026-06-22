package cl.pymetrack.msuser.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class PasswordHashGeneratorTest {

    @Test
    void testMain() {
        // Ejecutamos el main para cubrir las líneas de código
        assertDoesNotThrow(() -> PasswordHashGenerator.main(new String[]{}));
    }
}