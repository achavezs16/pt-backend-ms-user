package cl.pymetrack.msuser.controller;

import cl.pymetrack.msuser.model.Role;
import cl.pymetrack.msuser.model.User;
import cl.pymetrack.msuser.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private User createMockUser() {
        User user = new User();
        user.setId(1L);
        user.setNombre("Matías");
        user.setApellido("Suazo");
        user.setEmail("mati@test.com");
        user.setRol(Role.PYME);
        user.setActivo(true);
        user.setPymeId(1L);
        return user;
    }

    @Test
    void getDashboardPyme_Success() throws Exception {
        when(userService.findActiveUsersByPymeId(1L)).thenReturn(List.of(createMockUser()));
        mockMvc.perform(get("/dashboard/pyme/1")).andExpect(status().isOk());
    }

    @Test
    void getDashboardPyme_NotFound() throws Exception {
        when(userService.findActiveUsersByPymeId(99L)).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/dashboard/pyme/99")).andExpect(status().isNotFound());
    }

    @Test
    void getDashboardPyme_Error500() throws Exception {
        when(userService.findActiveUsersByPymeId(1L)).thenThrow(new RuntimeException("DB Error"));
        mockMvc.perform(get("/dashboard/pyme/1")).andExpect(status().isInternalServerError());
    }

    @Test
    void getResumenPyme_Success() throws Exception {
        when(userService.findActiveUsersByPymeId(1L)).thenReturn(List.of(createMockUser()));
        mockMvc.perform(get("/dashboard/pyme/1/resumen")).andExpect(status().isOk());
    }

    @Test
    void getResumenPyme_NotFound() throws Exception {
        when(userService.findActiveUsersByPymeId(99L)).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/dashboard/pyme/99/resumen")).andExpect(status().isNotFound());
    }

    @Test
    void getEstadisticasPyme_Success() throws Exception {
        when(userService.findActiveUsersByPymeId(1L)).thenReturn(List.of(createMockUser()));
        mockMvc.perform(get("/dashboard/pyme/1/estadisticas")).andExpect(status().isOk());
    }

    @Test
    void getProductosPyme_Success() throws Exception {
        mockMvc.perform(get("/dashboard/pyme/1/productos?categoria=test")).andExpect(status().isOk());
    }

    @Test
    void getPedidosPyme_Success() throws Exception {
        mockMvc.perform(get("/dashboard/pyme/1/pedidos?estado=pendiente")).andExpect(status().isOk());
    }
}