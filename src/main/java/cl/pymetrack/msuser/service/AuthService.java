package cl.pymetrack.msuser.service;

import cl.pymetrack.msuser.dto.*;
import cl.pymetrack.msuser.exception.InvalidCredentialsException;
import cl.pymetrack.msuser.model.Pyme;
import cl.pymetrack.msuser.model.Role;
import cl.pymetrack.msuser.model.User;
import cl.pymetrack.msuser.repository.PymeRepository;
import cl.pymetrack.msuser.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final PymeRepository pymeRepository;

    public AuthService(
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            UserService userService,
            PymeRepository pymeRepository
    ) {
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.pymeRepository = pymeRepository;
    }

    public LoginResponse authenticate(LoginRequest loginRequest) {
        logger.info("Intento de login para email: {}", loginRequest.getEmail());

        User user = userService.findActiveUserByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Credenciales inválidas"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Credenciales inválidas");
        }

        List<String> permissions = userService.getPermissionsByRole(user.getRol());

        String token = jwtService.generateToken(
                user.getId(),
                user.getEmail(),
                user.getNombre(),
                user.getRol(),
                user.getPymeId(),
                permissions
        );

        String refreshToken = jwtService.generateRefreshToken(user.getEmail(), user.getRol());

        UserInfo userInfo = new UserInfo();
        userInfo.setId(user.getId());
        userInfo.setNombre(user.getFullName());
        userInfo.setEmail(user.getEmail());
        userInfo.setRole(user.getRol().name());
        userInfo.setPermissions(permissions);
        userInfo.setPymeId(user.getPymeId());

        if (user.getPymeId() != null) {
            Optional<Pyme> pymeOpt = pymeRepository.findById(user.getPymeId());
            pymeOpt.ifPresent(pyme -> userInfo.setRutPyme(pyme.getRutPyme()));
        }

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setRefreshToken(refreshToken);
        response.setTokenType("Bearer");
        response.setExpiresIn(jwtService.getExpirationInSeconds());
        response.setExpiresAt(LocalDateTime.now().plusSeconds(jwtService.getExpirationInSeconds()));
        response.setUserInfo(userInfo);
        response.setRequiresPasswordChange(false);
        response.setMessage("Login exitoso");

        return response;
    }

    public LoginResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtService.validateRefreshToken(refreshToken)) {
            throw new InvalidCredentialsException("Refresh token inválido");
        }

        String email = jwtService.extractUsername(refreshToken);

        User user = userService.findActiveUserByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Usuario no encontrado"));

        List<String> permissions = userService.getPermissionsByRole(user.getRol());

        String newToken = jwtService.generateToken(
                user.getId(),
                user.getEmail(),
                user.getNombre(),
                user.getRol(),
                user.getPymeId(),
                permissions
        );

        String newRefreshToken = jwtService.generateRefreshToken(user.getEmail(), user.getRol());

        UserInfo userInfo = new UserInfo();
        userInfo.setId(user.getId());
        userInfo.setNombre(user.getFullName());
        userInfo.setEmail(user.getEmail());
        userInfo.setRole(user.getRol().name());
        userInfo.setPermissions(permissions);
        userInfo.setPymeId(user.getPymeId());

        LoginResponse response = new LoginResponse();
        response.setToken(newToken);
        response.setRefreshToken(newRefreshToken);
        response.setTokenType("Bearer");
        response.setExpiresIn(jwtService.getExpirationInSeconds());
        response.setExpiresAt(LocalDateTime.now().plusSeconds(jwtService.getExpirationInSeconds()));
        response.setUserInfo(userInfo);
        response.setRequiresPasswordChange(false);
        response.setMessage("Token refrescado exitosamente");

        return response;
    }

    public TokenValidationResponse validateToken(String token) {
        try {
            String email = jwtService.extractUsername(token);
            Optional<User> userOpt = userService.findActiveUserByEmail(email);

            if (userOpt.isEmpty()) {
                return new TokenValidationResponse(false, "Usuario no encontrado o inactivo");
            }

            boolean isValid = jwtService.validateToken(token, email);

            if (!isValid) {
                return new TokenValidationResponse(false, "Token inválido o expirado");
            }

            JwtService.UserInfo userInfo = jwtService.extractUserInfo(token);
            return new TokenValidationResponse(true, "Token válido", userInfo);

        } catch (Exception e) {
            logger.warn("Error al validar token: {}", e.getMessage());
            return new TokenValidationResponse(false, "Error al validar token: " + e.getMessage());
        }
    }

    public ChangePasswordResponse changePassword(Long userId, ChangePasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Las contraseñas nuevas no coinciden");
        }

        User user = userService.findActiveUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Contraseña actual incorrecta");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userService.save(user);

        return new ChangePasswordResponse("Contraseña cambiada exitosamente");
    }

    public void logout(String token) {
        logger.info("Logout registrado para token recibido");
    }
}
