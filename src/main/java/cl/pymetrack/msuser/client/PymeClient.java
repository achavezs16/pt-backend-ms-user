package cl.pymetrack.msuser.client;

import cl.pymetrack.msuser.exception.PymeServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Component
public class PymeClient {

    private static final Logger logger = LoggerFactory.getLogger(PymeClient.class);
    
    private final WebClient webClient;

    public PymeClient(@Value("${ms-admin.base-url:http://localhost:8080}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    /**
     * Obtiene una PYME por su email para autenticación
     */
    public Optional<PymeData> getPymeByEmail(String email) {
        logger.debug("Buscando PYME con email: {}", email);
        
        try {
            PymeData pyme = webClient.get()
                    .uri("/api/v1/pymes/search/by-email?email={email}", email)
                    .retrieve()
                    .bodyToMono(PymeData.class)
                    .block();
            
            return Optional.ofNullable(pyme);
            
        } catch (WebClientResponseException.NotFound e) {
            logger.debug("PYME no encontrada con email: {}", email);
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Error al buscar PYME por email: {}", email, e);
            throw new PymeServiceException("Error al comunicarse con ms-admin", e);
        }
    }

    /**
     * Cambia la contraseña de una PYME
     */
    public boolean changePassword(Long pymeId, String currentPassword, String newPassword) {
        logger.debug("Cambiando contraseña para PYME: {}", pymeId);
        
        try {
            Map<String, String> request = Map.of(
                "currentPassword", currentPassword,
                "newPassword", newPassword
            );
            
            Boolean result = webClient.post()
                    .uri("/api/v1/pymes/{id}/cambiar-password", pymeId)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
            
            return Boolean.TRUE.equals(result);
            
        } catch (Exception e) {
            logger.error("Error al cambiar contraseña para PYME: {}", pymeId, e);
            throw new PymeServiceException("Error al cambiar contraseña", e);
        }
    }

    /**
     * Registra intento de login fallido
     */
    public void registerFailedLogin(String email) {
        logger.debug("Registrando intento fallido para email: {}", email);
        
        try {
            webClient.post()
                    .uri("/api/v1/pymes/register-failed-login")
                    .bodyValue(Map.of("email", email))
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
                    
        } catch (Exception e) {
            logger.error("Error al registrar intento fallido para email: {}", email, e);
            // No lanzar excepción para no bloquear el flujo de login
        }
    }

    /**
     * Resetear intentos fallidos (login exitoso)
     */
    public void resetFailedAttempts(Long pymeId) {
        logger.debug("Reseteando intentos fallidos para PYME: {}", pymeId);
        
        try {
            webClient.post()
                    .uri("/api/v1/pymes/{id}/reset-attempts", pymeId)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
                    
        } catch (Exception e) {
            logger.error("Error al resetear intentos fallidos para PYME: {}", pymeId, e);
            // No lanzar excepción para no bloquear el flujo de login
        }
    }

    // DTO para representar los datos de PYME recibidos desde ms-admin
    public static class PymeData {
        private Long id;
        private String nombrePyme;
        private String emailContacto;
        private String rutPyme;
        private String password;
        private String tempPassword;
        private LocalDateTime tempPasswordExpiresAt;
        private LocalDateTime passwordChangedAt;
        private LocalDateTime ultimoLogin;
        private Integer intentosFallidos;
        private LocalDateTime cuentaBloqueadaHasta;
        private Boolean activo;
        private String estado;
        private LocalDateTime creadoEn;
        private LocalDateTime actualizadoEn;

        // Getters y Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getNombrePyme() {
            return nombrePyme;
        }

        public void setNombrePyme(String nombrePyme) {
            this.nombrePyme = nombrePyme;
        }

        public String getEmailContacto() {
            return emailContacto;
        }

        public void setEmailContacto(String emailContacto) {
            this.emailContacto = emailContacto;
        }

        public String getRutPyme() {
            return rutPyme;
        }

        public void setRutPyme(String rutPyme) {
            this.rutPyme = rutPyme;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getTempPassword() {
            return tempPassword;
        }

        public void setTempPassword(String tempPassword) {
            this.tempPassword = tempPassword;
        }

        public LocalDateTime getTempPasswordExpiresAt() {
            return tempPasswordExpiresAt;
        }

        public void setTempPasswordExpiresAt(LocalDateTime tempPasswordExpiresAt) {
            this.tempPasswordExpiresAt = tempPasswordExpiresAt;
        }

        public LocalDateTime getPasswordChangedAt() {
            return passwordChangedAt;
        }

        public void setPasswordChangedAt(LocalDateTime passwordChangedAt) {
            this.passwordChangedAt = passwordChangedAt;
        }

        public LocalDateTime getUltimoLogin() {
            return ultimoLogin;
        }

        public void setUltimoLogin(LocalDateTime ultimoLogin) {
            this.ultimoLogin = ultimoLogin;
        }

        public Integer getIntentosFallidos() {
            return intentosFallidos;
        }

        public void setIntentosFallidos(Integer intentosFallidos) {
            this.intentosFallidos = intentosFallidos;
        }

        public LocalDateTime getCuentaBloqueadaHasta() {
            return cuentaBloqueadaHasta;
        }

        public void setCuentaBloqueadaHasta(LocalDateTime cuentaBloqueadaHasta) {
            this.cuentaBloqueadaHasta = cuentaBloqueadaHasta;
        }

        public Boolean getActivo() {
            return activo;
        }

        public void setActivo(Boolean activo) {
            this.activo = activo;
        }

        public String getEstado() {
            return estado;
        }

        public void setEstado(String estado) {
            this.estado = estado;
        }

        public LocalDateTime getCreadoEn() {
            return creadoEn;
        }

        public void setCreadoEn(LocalDateTime creadoEn) {
            this.creadoEn = creadoEn;
        }

        public LocalDateTime getActualizadoEn() {
            return actualizadoEn;
        }

        public void setActualizadoEn(LocalDateTime actualizadoEn) {
            this.actualizadoEn = actualizadoEn;
        }

        // Métodos de utilidad
        public boolean requiresPasswordChange() {
            return "FIRST_LOGIN_REQUIRED".equals(estado) || 
                   (password == null && tempPassword != null);
        }

        public boolean isTempPasswordValid() {
            return tempPassword != null && 
                   tempPasswordExpiresAt != null && 
                   LocalDateTime.now().isBefore(tempPasswordExpiresAt);
        }

        public boolean isAccountLocked() {
            return cuentaBloqueadaHasta != null && 
                   LocalDateTime.now().isBefore(cuentaBloqueadaHasta);
        }

        @Override
        public String toString() {
            return "PymeData{" +
                    "id=" + id +
                    ", nombrePyme='" + nombrePyme + '\'' +
                    ", emailContacto='" + emailContacto + '\'' +
                    ", estado='" + estado + '\'' +
                    ", activo=" + activo +
                    ", requiresPasswordChange=" + requiresPasswordChange() +
                    '}';
        }
    }
}
