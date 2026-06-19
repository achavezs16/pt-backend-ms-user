package cl.pymetrack.msuser.controller;

import cl.pymetrack.msuser.model.Role;
import cl.pymetrack.msuser.model.User;
import cl.pymetrack.msuser.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void getAllUsers_Success() throws Exception {
        User user = new User();
        user.setNombre("Matías");
        user.setRol(Role.PYME); // Asignamos rol para evitar NPE
        when(userService.findAllUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Matías"));
    }

    @Test
    void getSystemStats_Success() throws Exception {
        when(userService.countActiveUsersByRole(Role.ADMIN)).thenReturn(1L);
        when(userService.countActiveUsersByRole(Role.PYME)).thenReturn(2L);
        when(userService.countActiveUsersByRole(Role.REPARTIDOR)).thenReturn(3L);

        mockMvc.perform(get("/admin/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAdmins").value(1))
                .andExpect(jsonPath("$.totalPymes").value(2))
                .andExpect(jsonPath("$.totalRepartidores").value(3));
    }

    @Test
    void changeUserRole_Success() throws Exception {
        User user = new User();
        user.setRol(Role.ADMIN); // Asignamos rol
        
        when(userService.changeUserRole(eq(1L), any(Role.class))).thenReturn(user);

        mockMvc.perform(post("/admin/users/1/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("role", "ADMIN"))))
                .andExpect(status().isOk());
    }

    @Test
    void toggleUserStatus_Success() throws Exception {
        User user = new User();
        user.setActivo(true);
        user.setRol(Role.PYME); // Asignamos rol
        
        when(userService.toggleUserStatus(eq(1L), eq(true))).thenReturn(user);

        mockMvc.perform(post("/admin/users/1/toggle-status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("activo", true))))
                .andExpect(status().isOk());
    }
}