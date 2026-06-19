package cl.pymetrack.msuser.dto;

import cl.pymetrack.msuser.model.Role;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class DtoTest {

    @Test
    void testChangePasswordDtos() {
        ChangePasswordRequest req = new ChangePasswordRequest();
        req.setNewPassword("12345678");
        req.setConfirmPassword("12345678");
        req.setCurrentPassword("old");
        assertEquals("12345678", req.getNewPassword());
        assertEquals("12345678", req.getConfirmPassword());
        assertEquals("old", req.getCurrentPassword());
        
        ChangePasswordResponse res = new ChangePasswordResponse("ok");
        assertEquals("ok", res.getMessage());
    }

    @Test
    void testErrorResponse() {
        ErrorResponse err = new ErrorResponse(400, "error");
        assertEquals(400, err.getStatus());
        assertEquals("error", err.getMessage());
    }

    @Test
    void testLoginDtos() {
        LoginRequest req = new LoginRequest();
        req.setEmail("m@test.com");
        req.setPassword("pass");
        assertEquals("m@test.com", req.getEmail());
        assertEquals("pass", req.getPassword());

        LoginResponse res = new LoginResponse();
        res.setToken("token");
        assertEquals("token", res.getToken());
    }

    @Test
    void testRefreshTokenRequest() {
        RefreshTokenRequest req = new RefreshTokenRequest();
        req.setRefreshToken("ref");
        assertEquals("ref", req.getRefreshToken());
    }

    @Test
    void testRegisterRequest() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("m@test.com");
        req.setRol(Role.PYME);
        assertEquals(Role.PYME, req.getRol());
    }

    @Test
    void testValidationAndUserInfo() {
        // CORRECCIÓN: Usamos getValid() tal como está definido en tu DTO
        TokenValidationResponse val = new TokenValidationResponse(true, "ok");
        assertTrue(val.getValid()); 
        assertEquals("ok", val.getMessage());

        UserInfo info = new UserInfo();
        info.setEmail("m@test.com");
        info.setPermissions(List.of("READ"));
        assertEquals("m@test.com", info.getEmail());
        assertEquals(1, info.getPermissions().size());
    }
}