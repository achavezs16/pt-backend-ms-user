package cl.pymetrack.msuser.service;

import cl.pymetrack.msuser.model.Role;
import cl.pymetrack.msuser.model.User;
import cl.pymetrack.msuser.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(String email, String password, String nombre, String apellido, Role rol, Long pymeId) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("El email ya está registrado: " + email);
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setNombre(nombre);
        user.setApellido(apellido);
        user.setRol(rol);
        user.setPymeId(pymeId);
        user.setActivo(true);

        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findActiveUserByEmail(String email) {
        return userRepository.findActiveUserByEmail(email);
    }

    public Optional<User> findActiveUserById(Long userId) {
        return userRepository.findById(userId)
                .filter(user -> Boolean.TRUE.equals(user.getActivo()));
    }

    public List<User> findByRole(Role rol) {
        return userRepository.findByRol(rol);
    }

    public List<User> findActiveUsersByRole(Role rol) {
        return userRepository.findActiveUsersByRole(rol);
    }

    public List<User> findByPymeId(Long pymeId) {
        return userRepository.findByPymeId(pymeId);
    }

    public List<User> findActiveUsersByPymeId(Long pymeId) {
        return userRepository.findActiveUsersByPymeId(pymeId);
    }

    public List<String> getPermissionsByRole(Role rol) {
        return switch (rol) {
            case ADMIN -> List.of(
                "USERS_READ",
                "USERS_WRITE",
                "PYMES_READ",
                "PYMES_WRITE",
                "PRODUCTS_READ",
                "PRODUCTS_WRITE",
                "ORDERS_READ",
                "ORDERS_WRITE",
                "DASHBOARD_READ"
            );
            case PYME -> List.of(
                "PYME_DASHBOARD_READ",
                "PRODUCTS_READ",
                "PRODUCTS_WRITE",
                "ORDERS_READ",
                "ORDERS_WRITE"
            );
            case REPARTIDOR -> List.of(
                "DELIVERIES_READ",
                "DELIVERIES_UPDATE"
            );
        };
    }

    public User changeUserRole(Long userId, Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + userId));

        user.setRol(newRole);
        return userRepository.save(user);
    }

    public User toggleUserStatus(Long userId, boolean activo) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + userId));

        user.setActivo(activo);
        return userRepository.save(user);
    }

    public boolean hasPermission(User user, String permission) {
        return getPermissionsByRole(user.getRol()).contains(permission);
    }

    public long countActiveUsersByRole(Role rol) {
        return userRepository.countActiveUsersByRole(rol);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + userId));

        user.setActivo(false);
        userRepository.save(user);
    }
}
