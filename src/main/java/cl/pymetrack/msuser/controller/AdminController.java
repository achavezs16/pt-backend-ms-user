package cl.pymetrack.msuser.controller;

import cl.pymetrack.msuser.model.Role;
import cl.pymetrack.msuser.model.User;
import cl.pymetrack.msuser.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/by-role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable Role role) {
        List<User> users = userService.findByRole(role);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> getSystemStats() {
        Map<String, Object> stats = Map.of(
            "totalAdmins", userService.countActiveUsersByRole(Role.ADMIN),
            "totalPymes", userService.countActiveUsersByRole(Role.PYME),
            "totalRepartidores", userService.countActiveUsersByRole(Role.REPARTIDOR)
        );
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/users/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> changeUserRole(@PathVariable Long userId, @RequestBody Map<String, String> request) {
        Role newRole = Role.valueOf(request.get("role"));
        User user = userService.changeUserRole(userId, newRole);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/users/{userId}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> toggleUserStatus(@PathVariable Long userId, @RequestBody Map<String, Boolean> request) {
        boolean activo = request.get("activo");
        User user = userService.toggleUserStatus(userId, activo);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/current-user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findActiveUserByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Map<String, Object> response = Map.of(
            "id", user.getId(),
            "nombre", user.getNombre(),
            "email", user.getEmail(),
            "role", user.getRol().name(),
            "permissions", userService.getPermissionsByRole(user.getRol())
        );
        
        return ResponseEntity.ok(response);
    }
}
