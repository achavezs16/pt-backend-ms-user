package cl.pymetrack.msuser.service;

import cl.pymetrack.msuser.model.Role;
import cl.pymetrack.msuser.model.User;
import cl.pymetrack.msuser.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private UserService userService;

    @Test
    void createUser_SuccessAndError() {
        // Test éxito
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hash");
        userService.createUser("m@test.com", "pass", "Mati", "S", Role.PYME, 1L);
        verify(userRepository, times(1)).save(any(User.class));

        // Test error (ya existe)
        when(userRepository.existsByEmail("existente@test.com")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> 
            userService.createUser("existente@test.com", "pass", "Mati", "S", Role.PYME, 1L));
    }

    @Test
    void testGetPermissionsByRole() {
        // Cubrimos todas las ramas de los if/else
        assertFalse(userService.getPermissionsByRole(Role.ADMIN).isEmpty());
        assertFalse(userService.getPermissionsByRole(Role.PYME).isEmpty());
        assertFalse(userService.getPermissionsByRole(Role.REPARTIDOR).isEmpty());
        assertTrue(userService.getPermissionsByRole(null).isEmpty());
    }

    @Test
    void changeUserRole_SuccessAndError() {
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        
        userService.changeUserRole(1L, Role.ADMIN);
        assertEquals(Role.ADMIN, user.getRol());

        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> userService.changeUserRole(99L, Role.ADMIN));
    }

    @Test
    void deleteUser_SuccessAndError() {
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        
        userService.deleteUser(1L);
        assertFalse(user.getActivo());
        verify(userRepository).save(user);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(99L));
    }
}