package cl.pymetrack.msuser.controller;

import cl.pymetrack.msuser.dto.CreatePymeAdminRequest;
import cl.pymetrack.msuser.model.Pyme;
import cl.pymetrack.msuser.model.Role;
import cl.pymetrack.msuser.model.User;
import cl.pymetrack.msuser.repository.PymeRepository;
import cl.pymetrack.msuser.service.UserService;
import jakarta.validation.Valid;
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

    @Autowired
    private PymeRepository pymeRepository;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @GetMapping("/users/by-role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable Role role) {
        return ResponseEntity.ok(userService.findByRole(role));
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

    @PostMapping("/pymes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createPymeWithUser(
            @Valid @RequestBody CreatePymeAdminRequest request) {

        if (pymeRepository.existsByRutPyme(request.getRutPyme())) {
            throw new IllegalArgumentException(
                    "Ya existe una PYME con RUT: " + request.getRutPyme()
            );
        }

        if (pymeRepository.existsByEmailContactoPyme(request.getEmailContactoPyme())) {
            throw new IllegalArgumentException(
                    "Ya existe una PYME con email: " + request.getEmailContactoPyme()
            );
        }

        Pyme pyme = new Pyme();
        pyme.setNombrePyme(request.getNombrePyme());
        pyme.setRutPyme(request.getRutPyme());
        pyme.setEmailContactoPyme(request.getEmailContactoPyme());
        pyme.setTelefonoContactoPyme(request.getTelefonoContactoPyme());
        pyme.setDireccionSucursalPyme(request.getDireccionSucursalPyme());
        pyme.setComunaSucursalPyme(request.getComunaSucursalPyme());
        pyme.setRegionSucursalPyme(request.getRegionSucursalPyme());
        pyme.setActivo(true);

        Pyme pymeGuardada = pymeRepository.save(pyme);

        User usuarioPyme = userService.createUser(
                request.getEmailRepresentante(),
                request.getPassword(),
                request.getNombreRepresentante(),
                request.getApellidoRepresentante(),
                Role.PYME,
                pymeGuardada.getId()
        );

        return ResponseEntity.ok(
                Map.of(
                        "pyme", pymeGuardada,
                        "usuario", usuarioPyme
                )
        );
    }

    @PostMapping("/users/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> changeUserRole(
            @PathVariable Long userId,
            @RequestBody Map<String, String> request) {

        Role newRole = Role.valueOf(request.get("role"));
        User user = userService.changeUserRole(userId, newRole);

        return ResponseEntity.ok(user);
    }

    @PostMapping("/users/{userId}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> toggleUserStatus(
            @PathVariable Long userId,
            @RequestBody Map<String, Boolean> request) {

        boolean activo = request.get("activo");
        User user = userService.toggleUserStatus(userId, activo);

        return ResponseEntity.ok(user);
    }

    @GetMapping("/current-user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userService.findActiveUserByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Map<String, Object> response = Map.of(
                "id", user.getId(),
                "nombre", user.getNombre(),
                "email", user.getEmail(),
                "role", user.getRol().name(),
                "permissions", userService.getPermissionsByRole(user.getRol()),
                "pymeId", user.getPymeId()
        );

        return ResponseEntity.ok(response);
    }
}