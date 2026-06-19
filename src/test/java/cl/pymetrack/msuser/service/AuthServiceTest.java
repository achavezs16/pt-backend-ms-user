package cl.pymetrack.msuser.service;

import cl.pymetrack.msuser.dto.*;
import cl.pymetrack.msuser.exception.InvalidCredentialsException;
import cl.pymetrack.msuser.model.*;
import cl.pymetrack.msuser.repository.PymeRepository;
import cl.pymetrack.msuser.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock private JwtService jwtService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserService userService;
    @Mock private PymeRepository pymeRepository;

    @InjectMocks private AuthService authService;

    @Test
    void authenticate_Success() {
        LoginRequest req = new LoginRequest();
        req.setEmail("m@t.com");
        req.setPassword("12345678");

        User user = new User("m@t.com", "hash", "Mati", Role.PYME);
        user.setId(1L);
        user.setPymeId(1L);

        // Usamos anyString() para que sea flexible y no falle por mismatches
        when(userService.findActiveUserByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(userService.getPermissionsByRole(any())).thenReturn(List.of("READ"));
        when(pymeRepository.findById(anyLong())).thenReturn(Optional.of(new Pyme()));

        LoginResponse res = authService.authenticate(req);
        assertNotNull(res);
        assertEquals("Login exitoso", res.getMessage());
    }

    @Test
    void authenticate_UserNotFound() {
        LoginRequest req = new LoginRequest();
        req.setEmail("m@t.com"); // Email definido!
        
        when(userService.findActiveUserByEmail(anyString())).thenReturn(Optional.empty());
        
        assertThrows(InvalidCredentialsException.class, () -> authService.authenticate(req));
    }

    @Test
    void authenticate_WrongPassword() {
        LoginRequest req = new LoginRequest();
        req.setEmail("m@t.com");
        req.setPassword("wrong");

        User user = new User("m@t.com", "hash", "Mati", Role.PYME);
        when(userService.findActiveUserByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        
        assertThrows(InvalidCredentialsException.class, () -> authService.authenticate(req));
    }

    @Test
    void refreshToken_Success() {
        RefreshTokenRequest req = new RefreshTokenRequest();
        req.setRefreshToken("token");
        User user = new User("m@t.com", "hash", "Mati", Role.PYME);
        
        when(jwtService.validateRefreshToken("token")).thenReturn(true);
        when(jwtService.extractUsername("token")).thenReturn("m@t.com");
        when(userService.findActiveUserByEmail("m@t.com")).thenReturn(Optional.of(user));
        when(userService.getPermissionsByRole(any())).thenReturn(List.of());
        
        LoginResponse res = authService.refreshToken(req);
        assertNotNull(res);
    }
}