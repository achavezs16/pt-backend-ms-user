package cl.pymetrack.msuser.service;

import cl.pymetrack.msuser.model.Role;
import cl.pymetrack.msuser.model.User;
import cl.pymetrack.msuser.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@caltias.cl");
        mockUser.setActivo(true);
        mockUser.setRol(Role.PYME);
    }

    // ==========================================
    // TESTS: CREACIÓN Y VALIDACIONES
    // ==========================================

    @Test
    void testCreateUser_Exitoso() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        User savedUser = userService.createUser("test@caltias.cl", "pass123", "Juan", "Perez", Role.PYME, 1L);

        assertNotNull(savedUser);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUser_EmailYaRegistrado_LanzaExcepcion() {
        when(userRepository.existsByEmail("test@caltias.cl")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> 
            userService.createUser("test@caltias.cl", "pass", "N", "A", Role.PYME, 1L)
        );
    }

    // ==========================================
    // TESTS: PERMISOS (BRANCH COVERAGE)
    // ==========================================

    @Test
    void testGetPermissionsByRole() {
        // Probamos todas las ramas del if/else
        assertTrue(userService.getPermissionsByRole(Role.ADMIN).contains("USERS_READ"));
        assertTrue(userService.getPermissionsByRole(Role.PYME).contains("PRODUCTS_WRITE"));
        assertTrue(userService.getPermissionsByRole(Role.REPARTIDOR).contains("DELIVERIES_READ"));
        assertTrue(userService.getPermissionsByRole(null).isEmpty());
    }

    // ==========================================
    // TESTS: MODIFICACIONES Y ESTADOS
    // ==========================================

    @Test
    void testChangeUserRole_Exitoso() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        User updatedUser = userService.changeUserRole(1L, Role.ADMIN);

        assertEquals(Role.ADMIN, updatedUser.getRol());
        verify(userRepository).save(mockUser);
    }

    @Test
    void testToggleUserStatus_Exitoso() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        User updatedUser = userService.toggleUserStatus(1L, false);

        assertFalse(updatedUser.getActivo());
        verify(userRepository).save(mockUser);
    }

    @Test
    void testDeleteUser_Exitoso() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        
        userService.deleteUser(1L);

        assertFalse(mockUser.getActivo());
        verify(userRepository).save(mockUser);
    }

    @Test
    void testOperaciones_UsuarioNoEncontrado_LanzaExcepcion() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.changeUserRole(99L, Role.ADMIN));
        assertThrows(IllegalArgumentException.class, () -> userService.toggleUserStatus(99L, false));
        assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(99L));
    }

    // ==========================================
    // TESTS: FINDERS (REPOSITORIO)
    // ==========================================

    @Test
    void testFindActiveUserById_FiltraInactivos() {
        mockUser.setActivo(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        Optional<User> result = userService.findActiveUserById(1L);

        assertTrue(result.isEmpty());
    }
    
    @Test
    void testHasPermission() {
        mockUser.setRol(Role.REPARTIDOR);
        assertTrue(userService.hasPermission(mockUser, "DELIVERIES_READ"));
        assertFalse(userService.hasPermission(mockUser, "PYME_DASHBOARD_READ"));
    }
}