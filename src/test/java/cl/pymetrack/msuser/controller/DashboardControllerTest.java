package cl.pymetrack.msuser.controller;

import cl.pymetrack.msuser.model.Role;
import cl.pymetrack.msuser.model.User;
import cl.pymetrack.msuser.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

// Importaciones estáticas explícitas para evitar errores de compilación
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class DashboardControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private DashboardController dashboardController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(dashboardController).build();
    }

    private User createFullUser() {
        User user = new User();
        user.setId(1L);
        user.setNombre("Matías");
        user.setApellido("Suazo");
        user.setEmail("matias@pymetrack.cl");
        user.setRol(Role.PYME);
        user.setActivo(true);
        user.setPymeId(10L);
        return user;
    }

    @Test
    void testGetDashboardPyme_Exitoso() throws Exception {
        when(userService.findActiveUsersByPymeId(10L)).thenReturn(List.of(createFullUser()));
        
        mockMvc.perform(get("/dashboard/pyme/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pymeInfo.id").value(10))
                .andExpect(jsonPath("$.usuarios[0].nombre").value("Matías"));
    }

    @Test
    void testGetDashboardPyme_NotFound() throws Exception {
        when(userService.findActiveUsersByPymeId(99L)).thenReturn(Collections.emptyList());
        
        mockMvc.perform(get("/dashboard/pyme/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetResumenPyme_Exitoso() throws Exception {
        when(userService.findActiveUsersByPymeId(10L)).thenReturn(List.of(createFullUser()));
        
        mockMvc.perform(get("/dashboard/pyme/10/resumen"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pyme.nombre").value("Matías Suazo"));
    }

    @Test
    void testGetEstadisticasPyme_Exitoso() throws Exception {
        when(userService.findActiveUsersByPymeId(10L)).thenReturn(List.of(createFullUser()));
        
        mockMvc.perform(get("/dashboard/pyme/10/estadisticas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsuarios").value(1));
    }

    @Test
    void testGetProductosPyme() throws Exception {
        mockMvc.perform(get("/dashboard/pyme/10/productos?categoria=test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoria").value("test"));
    }

    @Test
    void testGetPedidosPyme() throws Exception {
        mockMvc.perform(get("/dashboard/pyme/10/pedidos?estado=pendiente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("pendiente"));
    }
    
    @Test
    void testGetDashboardPyme_ErrorServidor() throws Exception {
        // Usamos anyLong() explícito que ahora sí está importado
        when(userService.findActiveUsersByPymeId(anyLong())).thenThrow(new RuntimeException("DB Error"));
        
        mockMvc.perform(get("/dashboard/pyme/10"))
                .andExpect(status().isInternalServerError());
    }
}