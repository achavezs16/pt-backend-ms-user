package cl.pymetrack.msuser.controller;

import cl.pymetrack.msuser.config.SecurityConfig;
import cl.pymetrack.msuser.model.Pyme;
import cl.pymetrack.msuser.model.Role;
import cl.pymetrack.msuser.model.User;
import cl.pymetrack.msuser.repository.PymeRepository;
import cl.pymetrack.msuser.security.JwtService;
import cl.pymetrack.msuser.service.UserService;
import cl.pymetrack.msuser.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@Import(SecurityConfig.class)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private UserService userService;
    @MockBean private PymeRepository pymeRepository;
    @MockBean private UserDetailsServiceImpl userDetailsService;
    @MockBean private JwtService jwtService;
    @MockBean private PasswordEncoder passwordEncoder;

    private User createValidUser() {
        User user = new User();
        user.setId(1L);
        user.setNombre("Matías");
        user.setEmail("test@caltias.cl");
        user.setRol(Role.PYME); 
        user.setActivo(true);
        user.setPymeId(10L);
        return user;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllUsers_Exitoso() throws Exception {
        when(userService.findAllUsers()).thenReturn(List.of(createValidUser()));
        mockMvc.perform(get("/admin/users")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetUsersByRole_Exitoso() throws Exception {
        when(userService.findByRole(Role.PYME)).thenReturn(List.of(createValidUser()));
        mockMvc.perform(get("/admin/users/by-role/PYME")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetSystemStats_Exitoso() throws Exception {
        when(userService.countActiveUsersByRole(any(Role.class))).thenReturn(5L);
        mockMvc.perform(get("/admin/stats")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreatePymeWithUser_Exitoso() throws Exception {
        when(pymeRepository.existsByRutPyme(anyString())).thenReturn(false);
        when(pymeRepository.existsByEmailContactoPyme(anyString())).thenReturn(false);
        when(pymeRepository.save(any())).thenReturn(new Pyme());
        when(userService.createUser(any(), any(), any(), any(), any(), any())).thenReturn(createValidUser());

        String jsonRequest = "{\"nombrePyme\": \"Test\", \"rutPyme\": \"12345678-9\", \"emailContactoPyme\": \"p@t.com\", \"emailRepresentante\": \"rep@t.com\", \"password\": \"12345678\", \"nombreRepresentante\": \"Juan\", \"apellidoRepresentante\": \"Perez\"}";

        mockMvc.perform(post("/admin/pymes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testChangeUserRole_Exitoso() throws Exception {
        when(userService.changeUserRole(anyLong(), any(Role.class))).thenReturn(createValidUser());
        String jsonRequest = "{\"role\": \"PYME\"}";

        mockMvc.perform(post("/admin/users/1/role")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testToggleUserStatus_Exitoso() throws Exception {
        when(userService.toggleUserStatus(anyLong(), anyBoolean())).thenReturn(createValidUser());
        String jsonRequest = "{\"activo\": true}";

        mockMvc.perform(post("/admin/users/1/toggle-status")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@caltias.cl", roles = "ADMIN")
    void testGetCurrentUser_Exitoso() throws Exception {
        when(userService.findActiveUserByEmail("test@caltias.cl")).thenReturn(Optional.of(createValidUser()));
        when(userService.getPermissionsByRole(Role.PYME)).thenReturn(List.of("READ"));

        mockMvc.perform(get("/admin/current-user")).andExpect(status().isOk());
    }

    @Test
    void testGetStats_SinAutorizacion_Falla() throws Exception {
        // CORRECCIÓN FINAL: Esperamos 403 Forbidden, que es lo que Spring Security responde
        mockMvc.perform(get("/admin/stats")).andExpect(status().isForbidden());
    }
}