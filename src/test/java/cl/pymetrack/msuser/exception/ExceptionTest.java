package cl.pymetrack.msuser.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ExceptionTest {

    @Test
    void testAuthenticationException() {
        assertNotNull(new AuthenticationException("test"));
        assertNotNull(new AuthenticationException("test", new RuntimeException("causa")));
    }

    @Test
    void testInvalidCredentialsException() {
        assertNotNull(new InvalidCredentialsException("test"));
        assertNotNull(new InvalidCredentialsException("test", new RuntimeException("causa")));
    }

    @Test
    void testPymeNotFoundException() {
        assertNotNull(new PymeNotFoundException("test"));
        assertNotNull(new PymeNotFoundException("test", new RuntimeException("causa")));
    }

    @Test
    void testPymeServiceException() {
        assertNotNull(new PymeServiceException("test"));
        assertNotNull(new PymeServiceException("test", new RuntimeException("causa")));
    }
}