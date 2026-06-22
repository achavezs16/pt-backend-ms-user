package cl.pymetrack.msuser.controller;

import cl.pymetrack.msuser.dto.*;
import cl.pymetrack.msuser.exception.*;
import cl.pymetrack.msuser.model.Role;
import cl.pymetrack.msuser.model.User;
import cl.pymetrack.msuser.service.AuthService;
import cl.pymetrack.msuser.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock private AuthService authService;
    @Mock private UserService userService;
    @InjectMocks private AuthController authController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(authController)
                .build();
    }

    @Test
    void testLogin_Exitoso() throws Exception {
        LoginResponse mockResponse = new LoginResponse();
        mockResponse.setToken("mocked-token");
        when(authService.authenticate(any(LoginRequest.class))).thenReturn(mockResponse);

        // Password >= 6 caracteres (cumple @Size)
        String jsonRequest = "{\"email\": \"test@test.com\", \"password\": \"12345678\"}";

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk());
    }

    @Test
    void testChangePassword_Exitoso() throws Exception {
        ChangePasswordResponse mockResponse = new ChangePasswordResponse("Contraseña cambiada");
        when(authService.changePassword(eq(1L), any(ChangePasswordRequest.class))).thenReturn(mockResponse);

        // Password >= 8 caracteres (cumple @Size)
        String jsonRequest = "{\"currentPassword\": \"oldpassword\", \"newPassword\": \"newpassword123\", \"confirmPassword\": \"newpassword123\"}";

        mockMvc.perform(post("/auth/change-password/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk());
    }

    @Test
    void testRegister_Exitoso() throws Exception {
        User mockUser = new User();
        mockUser.setEmail("new@test.com");
        // Aseguramos que el usuario mock tenga un rol para evitar el NPE en getAuthorities()
        mockUser.setRol(Role.PYME); 
        
        when(userService.createUser(any(), any(), any(), any(), any(), any())).thenReturn(mockUser);

        String jsonRequest = "{\"email\": \"new@test.com\", \"password\": \"12345678\", \"nombre\": \"Juan\", \"apellido\": \"Perez\", \"rol\": \"PYME\", \"pymeId\": 1}";

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk());
    }

    @Test
    void testHandleInvalidCredentialsException() throws Exception {
        when(authService.authenticate(any(LoginRequest.class))).thenThrow(new InvalidCredentialsException("Credenciales malas"));
        String jsonRequest = "{\"email\": \"test@test.com\", \"password\": \"12345678\"}";

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testHandleIllegalArgumentException() throws Exception {
        when(authService.changePassword(anyLong(), any())).thenThrow(new IllegalArgumentException("No coinciden"));
        String jsonRequest = "{\"currentPassword\": \"oldpass123\", \"newPassword\": \"newpass123\", \"confirmPassword\": \"wrongpass\"}";

        mockMvc.perform(post("/auth/change-password/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testHandleGenericException() throws Exception {
        when(authService.authenticate(any())).thenThrow(new RuntimeException("Error fatal DB"));
        String jsonRequest = "{\"email\": \"test@test.com\", \"password\": \"12345678\"}";

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testRefreshToken_Exitoso() throws Exception {
        LoginResponse mockResponse = new LoginResponse();
        when(authService.refreshToken(any(RefreshTokenRequest.class))).thenReturn(mockResponse);
        String jsonRequest = "{\"refreshToken\": \"some-token\"}";

        mockMvc.perform(post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk());
    }

    @Test
    void testValidateToken_Exitoso() throws Exception {
        TokenValidationResponse mockResponse = new TokenValidationResponse(true, "Válido");
        when(authService.validateToken("real-token")).thenReturn(mockResponse);

        mockMvc.perform(post("/auth/validate")
                .header("Authorization", "Bearer real-token"))
                .andExpect(status().isOk());
    }

    @Test
    void testLogout_Exitoso() throws Exception {
        doNothing().when(authService).logout("real-token");
        mockMvc.perform(post("/auth/logout")
                .header("Authorization", "Bearer real-token"))
                .andExpect(status().isOk());
    }

    @Test
    void testHealth_Exitoso() throws Exception {
        mockMvc.perform(get("/auth/health"))
                .andExpect(status().isOk());
    }
}