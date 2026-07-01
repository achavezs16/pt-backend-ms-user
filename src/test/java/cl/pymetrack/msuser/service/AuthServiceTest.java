package cl.pymetrack.msuser.service;

import cl.pymetrack.msuser.dto.ChangePasswordRequest;
import cl.pymetrack.msuser.dto.LoginRequest;
import cl.pymetrack.msuser.dto.LoginResponse;
import cl.pymetrack.msuser.dto.RefreshTokenRequest;
import cl.pymetrack.msuser.dto.TokenValidationResponse;
import cl.pymetrack.msuser.exception.InvalidCredentialsException;
import cl.pymetrack.msuser.model.Pyme;
import cl.pymetrack.msuser.model.Role;
import cl.pymetrack.msuser.model.User;
import cl.pymetrack.msuser.repository.PymeRepository;
import cl.pymetrack.msuser.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserService userService;

    @Mock
    private PymeRepository pymeRepository;

    @InjectMocks
    private AuthService authService;

    private User mockUser;
    private final String TEST_EMAIL = "matias@caltias.cl";
    private final String ENCODED_PASS = "hashed_password";

    @BeforeEach
    void setUp() {
        // Configuramos un mock de User estricto para evitar NullPointerExceptions
        mockUser = mock(User.class);
        lenient().when(mockUser.getId()).thenReturn(1L);
        lenient().when(mockUser.getEmail()).thenReturn(TEST_EMAIL);
        lenient().when(mockUser.getNombre()).thenReturn("Matías");
        lenient().when(mockUser.getFullName()).thenReturn("Matías Suazo");
        lenient().when(mockUser.getPassword()).thenReturn(ENCODED_PASS);
        lenient().when(mockUser.getRol()).thenReturn(Role.values()[0]); // Truco Senior para Enums
    }

    // ==========================================
    // TESTS: AUTHENTICATE (LOGIN)
    // ==========================================

    @Test
    void testAuthenticate_ExitoConPyme() {
        LoginRequest request = mock(LoginRequest.class);
        when(request.getEmail()).thenReturn(TEST_EMAIL);
        when(request.getPassword()).thenReturn("12345");

        when(userService.findActiveUserByEmail(TEST_EMAIL)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("12345", ENCODED_PASS)).thenReturn(true);
        when(userService.getPermissionsByRole(any())).thenReturn(Arrays.asList("READ", "WRITE"));
        when(jwtService.generateToken(any(), any(), any(), any(), any(), any())).thenReturn("mocked-jwt-token");
        when(jwtService.generateRefreshToken(anyString(), any())).thenReturn("mocked-refresh-token");
        when(jwtService.getExpirationInSeconds()).thenReturn(3600L);

        // Simulamos que el usuario pertenece a una PYME
        when(mockUser.getPymeId()).thenReturn(10L);
        Pyme mockPyme = mock(Pyme.class);
        when(mockPyme.getRutPyme()).thenReturn("76.543.210-K");
        when(pymeRepository.findById(10L)).thenReturn(Optional.of(mockPyme));

        LoginResponse response = authService.authenticate(request);

        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getToken());
        assertEquals("Login exitoso", response.getMessage());
        verify(pymeRepository, times(1)).findById(10L);
    }

    @Test
    void testAuthenticate_ExitoSinPyme() {
        LoginRequest request = mock(LoginRequest.class);
        when(request.getEmail()).thenReturn(TEST_EMAIL);
        when(request.getPassword()).thenReturn("12345");

        when(userService.findActiveUserByEmail(TEST_EMAIL)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        
        // Simulamos un usuario administrador sin PYME asignada
        when(mockUser.getPymeId()).thenReturn(null);

        LoginResponse response = authService.authenticate(request);

        assertNotNull(response);
        verify(pymeRepository, never()).findById(anyLong());
    }

    @Test
    void testAuthenticate_UsuarioNoEncontrado() {
        LoginRequest request = mock(LoginRequest.class);
        when(request.getEmail()).thenReturn("fantasma@caltias.cl");

        when(userService.findActiveUserByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authService.authenticate(request));
    }

    @Test
    void testAuthenticate_PasswordIncorrecto() {
        LoginRequest request = mock(LoginRequest.class);
        when(request.getEmail()).thenReturn(TEST_EMAIL);
        when(request.getPassword()).thenReturn("clave-mala");

        when(userService.findActiveUserByEmail(TEST_EMAIL)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("clave-mala", ENCODED_PASS)).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.authenticate(request));
    }

    // ==========================================
    // TESTS: REFRESH TOKEN
    // ==========================================

    @Test
    void testRefreshToken_Exitoso() {
        RefreshTokenRequest request = mock(RefreshTokenRequest.class);
        when(request.getRefreshToken()).thenReturn("valid-refresh-token");

        when(jwtService.validateRefreshToken("valid-refresh-token")).thenReturn(true);
        when(jwtService.extractUsername("valid-refresh-token")).thenReturn(TEST_EMAIL);
        when(userService.findActiveUserByEmail(TEST_EMAIL)).thenReturn(Optional.of(mockUser));
        when(jwtService.getExpirationInSeconds()).thenReturn(3600L);

        LoginResponse response = authService.refreshToken(request);

        assertNotNull(response);
        assertEquals("Token refrescado exitosamente", response.getMessage());
    }

    @Test
    void testRefreshToken_TokenInvalido() {
        RefreshTokenRequest request = mock(RefreshTokenRequest.class);
        when(request.getRefreshToken()).thenReturn("bad-token");

        when(jwtService.validateRefreshToken("bad-token")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.refreshToken(request));
    }

    @Test
    void testRefreshToken_UsuarioNoEncontrado() {
        RefreshTokenRequest request = mock(RefreshTokenRequest.class);
        when(request.getRefreshToken()).thenReturn("valid-token-but-deleted-user");

        when(jwtService.validateRefreshToken(anyString())).thenReturn(true);
        when(jwtService.extractUsername(anyString())).thenReturn("borrado@caltias.cl");
        when(userService.findActiveUserByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authService.refreshToken(request));
    }

    // ==========================================
    // TESTS: VALIDATE TOKEN
    // ==========================================

    @Test
    void testValidateToken_Exitoso() {
        when(jwtService.extractUsername("good-token")).thenReturn(TEST_EMAIL);
        when(userService.findActiveUserByEmail(TEST_EMAIL)).thenReturn(Optional.of(mockUser));
        when(jwtService.validateToken("good-token", TEST_EMAIL)).thenReturn(true);
        
        JwtService.UserInfo mockUserInfo = mock(JwtService.UserInfo.class);
        when(jwtService.extractUserInfo("good-token")).thenReturn(mockUserInfo);

        TokenValidationResponse response = authService.validateToken("good-token");

        // CORRECCIÓN EXACTA: getValid() en lugar de isValid()
        assertTrue(response.getValid());
        assertEquals("Token válido", response.getMessage());
    }

    @Test
    void testValidateToken_UsuarioInactivoONoEncontrado() {
        when(jwtService.extractUsername("token")).thenReturn(TEST_EMAIL);
        when(userService.findActiveUserByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        TokenValidationResponse response = authService.validateToken("token");

        // CORRECCIÓN EXACTA: getValid()
        assertFalse(response.getValid());
        assertEquals("Usuario no encontrado o inactivo", response.getMessage());
    }

    @Test
    void testValidateToken_TokenInvalidoOExpirado() {
        when(jwtService.extractUsername("expired-token")).thenReturn(TEST_EMAIL);
        when(userService.findActiveUserByEmail(TEST_EMAIL)).thenReturn(Optional.of(mockUser));
        when(jwtService.validateToken("expired-token", TEST_EMAIL)).thenReturn(false);

        TokenValidationResponse response = authService.validateToken("expired-token");

        // CORRECCIÓN EXACTA: getValid()
        assertFalse(response.getValid());
        assertEquals("Token inválido o expirado", response.getMessage());
    }

    @Test
    void testValidateToken_LanzaExcepcion() {
        when(jwtService.extractUsername(anyString())).thenThrow(new RuntimeException("Firma alterada"));

        TokenValidationResponse response = authService.validateToken("hacked-token");

        // CORRECCIÓN EXACTA: getValid()
        assertFalse(response.getValid());
        assertTrue(response.getMessage().contains("Error al validar token"));
    }

    // ==========================================
    // TESTS: CHANGE PASSWORD
    // ==========================================

    @Test
    void testChangePassword_Exitoso() {
        ChangePasswordRequest request = mock(ChangePasswordRequest.class);
        when(request.getCurrentPassword()).thenReturn("old-pass");
        when(request.getNewPassword()).thenReturn("new-pass");
        when(request.getConfirmPassword()).thenReturn("new-pass");

        when(userService.findActiveUserById(1L)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("old-pass", ENCODED_PASS)).thenReturn(true);
        when(passwordEncoder.encode("new-pass")).thenReturn("new-hashed-pass");

        var response = authService.changePassword(1L, request);

        assertEquals("Contraseña cambiada exitosamente", response.getMessage());
        verify(userService, times(1)).save(mockUser);
        verify(mockUser, times(1)).setPassword("new-hashed-pass");
    }

    @Test
    void testChangePassword_PasswordsNoCoinciden() {
        ChangePasswordRequest request = mock(ChangePasswordRequest.class);
        when(request.getNewPassword()).thenReturn("new-pass");
        when(request.getConfirmPassword()).thenReturn("different-pass");

        assertThrows(IllegalArgumentException.class, () -> authService.changePassword(1L, request));
    }

    @Test
    void testChangePassword_UsuarioNoEncontrado() {
        ChangePasswordRequest request = mock(ChangePasswordRequest.class);
        when(request.getNewPassword()).thenReturn("new-pass");
        when(request.getConfirmPassword()).thenReturn("new-pass");

        when(userService.findActiveUserById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> authService.changePassword(99L, request));
    }

    @Test
    void testChangePassword_PasswordActualIncorrecto() {
        ChangePasswordRequest request = mock(ChangePasswordRequest.class);
        when(request.getCurrentPassword()).thenReturn("wrong-old-pass");
        when(request.getNewPassword()).thenReturn("new-pass");
        when(request.getConfirmPassword()).thenReturn("new-pass");

        when(userService.findActiveUserById(1L)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("wrong-old-pass", ENCODED_PASS)).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.changePassword(1L, request));
    }

    // ==========================================
    // TESTS: LOGOUT
    // ==========================================

    @Test
    void testLogout() {
        // Al ser un método void que solo hace logging, verificamos que no lance excepciones
        assertDoesNotThrow(() -> authService.logout("some-token"));
    }
}