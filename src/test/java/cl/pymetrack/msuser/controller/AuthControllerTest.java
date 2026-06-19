package cl.pymetrack.msuser.controller;

import cl.pymetrack.msuser.dto.*;
import cl.pymetrack.msuser.exception.AuthenticationException;
import cl.pymetrack.msuser.exception.InvalidCredentialsException;
import cl.pymetrack.msuser.model.Role;
import cl.pymetrack.msuser.model.User;
import cl.pymetrack.msuser.service.AuthService;
import cl.pymetrack.msuser.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void login_Success() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail("m@t.com");
        req.setPassword("12345678"); // Cumple > 8 caracteres
        when(authService.authenticate(any())).thenReturn(new LoginResponse());

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

@Test
    void register_Success() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("new@t.com");
        req.setNombre("Matías");
        req.setApellido("Suazo");
        req.setPassword("12345678");
        req.setRol(Role.PYME);
        req.setPymeId(1L);

        User mockUser = new User();
        mockUser.setRol(Role.PYME); 
        mockUser.setEmail("new@t.com");
        
        when(userService.createUser(any(), any(), any(), any(), any(), any())).thenReturn(mockUser);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void login_InvalidCredentials() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail("m@t.com");
        req.setPassword("12345678");
        
        when(authService.authenticate(any())).thenThrow(new InvalidCredentialsException("Bad creds"));
        
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void validateToken_Success() throws Exception {
        when(authService.validateToken(any())).thenReturn(new TokenValidationResponse(true, "ok"));
        mockMvc.perform(post("/auth/validate")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk());
    }

    @Test
    void logout_Success() throws Exception {
        mockMvc.perform(post("/auth/logout")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk());
    }

    @Test
    void health_Check() throws Exception {
        mockMvc.perform(get("/auth/health"))
                .andExpect(status().isOk());
    }

    @Test
    void handleAuthenticationException_Test() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail("m@t.com");
        req.setPassword("12345678");

        when(authService.authenticate(any())).thenThrow(new AuthenticationException("Forbidden"));
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }
}