package cl.pymetrack.msuser.controller;

import cl.pymetrack.msuser.dto.*;
import cl.pymetrack.msuser.model.User;
import cl.pymetrack.msuser.service.AuthService;
import cl.pymetrack.msuser.service.UserService;
import cl.pymetrack.msuser.exception.InvalidCredentialsException;
import cl.pymetrack.msuser.exception.AuthenticationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticación", description = "API para autenticación y gestión de tokens JWT")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y retorna tokens JWT con roles y permisos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login exitoso",
                content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
        @ApiResponse(responseCode = "403", description = "Cuenta inactiva o bloqueada"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Petición de login para email: {}", loginRequest.getEmail());
        
        LoginResponse response = authService.authenticate(loginRequest);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password/{userId}")
    @Operation(summary = "Cambiar contraseña", description = "Cambia la contraseña de un usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contraseña cambiada exitosamente",
                content = @Content(schema = @Schema(implementation = ChangePasswordResponse.class))),
        @ApiResponse(responseCode = "400", description = "Contraseñas no coinciden"),
        @ApiResponse(responseCode = "401", description = "Contraseña actual incorrecta"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<ChangePasswordResponse> changePassword(
            @Parameter(description = "ID del usuario", required = true)
            @PathVariable Long userId,
            @Valid @RequestBody ChangePasswordRequest request) {
        logger.info("Petición de cambio de contraseña para usuario: {}", userId);
        
        ChangePasswordResponse response = authService.changePassword(userId, request);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refrescar token", description = "Genera un nuevo token JWT usando un refresh token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token refrescado exitosamente",
                content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "401", description = "Refresh token inválido o expirado")
    })
    public ResponseEntity<LoginResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        logger.info("Petición de refresh token");
        
        LoginResponse response = authService.refreshToken(request);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar usuario", description = "Registra un nuevo usuario en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Email ya registrado")
    })
    public ResponseEntity<User> register(@Valid @RequestBody RegisterRequest registerRequest) {
        logger.info("Petición de registro para email: {}", registerRequest.getEmail());
        
        User createdUser = userService.createUser(
            registerRequest.getEmail(),
            registerRequest.getPassword(),
            registerRequest.getNombre(),
            registerRequest.getApellido(),
            registerRequest.getRol(),
            registerRequest.getPymeId()
        );
        
        return ResponseEntity.ok(createdUser);
    }

    @PostMapping("/validate")
    @Operation(summary = "Validar token", description = "Valida un token JWT y retorna información del usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token válido",
                content = @Content(schema = @Schema(implementation = TokenValidationResponse.class))),
        @ApiResponse(responseCode = "401", description = "Token inválido o expirado")
    })
    public ResponseEntity<TokenValidationResponse> validateToken(@RequestHeader("Authorization") String authHeader) {
        logger.info("Petición de validación de token");
        
        // Extraer token del header "Bearer token"
        String token = authHeader.replace("Bearer ", "");
        
        TokenValidationResponse response = authService.validateToken(token);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión", description = "Cierra la sesión de un usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sesión cerrada exitosamente")
    })
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
        logger.info("Petición de logout");
        
        // Extraer token del header "Bearer token"
        String token = authHeader.replace("Bearer ", "");
        
        authService.logout(token);
        
        return ResponseEntity.ok().build();
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Verifica que el servicio esté funcionando")
    @ApiResponse(responseCode = "200", description = "Servicio funcionando correctamente")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("ms-auth service is running");
    }

    // Manejo de excepciones
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
        logger.warn("Credenciales inválidas: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        logger.warn("Error de autenticación: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(HttpStatus.FORBIDDEN.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn("Argumento inválido: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        logger.error("Error interno del servidor", ex);
        ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error interno del servidor");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
