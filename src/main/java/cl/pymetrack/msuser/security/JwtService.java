package cl.pymetrack.msuser.security;

import cl.pymetrack.msuser.model.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${jwt.secret:mySecretKey123456789012345678901234567890}")
    private String secret;

    @Value("${jwt.expiration:86400}") // 24 horas en segundos
    private Long jwtExpiration;

    @Value("${jwt.refresh-expiration:604800}") // 7 días en segundos
    private Long refreshExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Extrae el username (email) del token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae la fecha de expiración del token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrae un claim específico del token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrae todos los claims del token
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            logger.warn("Token JWT expirado: {}", e.getMessage());
            throw new JwtException("Token expirado", e);
        } catch (UnsupportedJwtException e) {
            logger.error("Token JWT no soportado: {}", e.getMessage());
            throw new JwtException("Token no soportado", e);
        } catch (MalformedJwtException e) {
            logger.error("Token JWT malformado: {}", e.getMessage());
            throw new JwtException("Token malformado", e);
        } catch (SecurityException e) {
            logger.error("Error de seguridad en token JWT: {}", e.getMessage());
            throw new JwtException("Error de seguridad en token", e);
        } catch (IllegalArgumentException e) {
            logger.error("Argumento inválido para token JWT: {}", e.getMessage());
            throw new JwtException("Argumento inválido", e);
        }
    }

    /**
     * Verifica si el token está expirado
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Genera un token JWT para un usuario con roles y permisos
     */
    public String generateToken(Long userId, String email, String nombre, Role rol, Long pymeId, List<String> permissions) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("nombre", nombre);
        claims.put("role", rol.name());
        claims.put("permissions", permissions);
        
        if (pymeId != null) {
            claims.put("pymeId", pymeId);
        }
        
        return createToken(claims, email, jwtExpiration);
    }

    /**
     * Genera un refresh token
     */
    public String generateRefreshToken(String email, Role rol) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        claims.put("role", rol.name());
        
        return createToken(claims, email, refreshExpiration);
    }

    /**
     * Crea un token con los claims y expiración especificados
     */
    private String createToken(Map<String, Object> claims, String subject, Long expiration) {
        LocalDateTime now = LocalDateTime.now();
        Date issuedAt = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        Date expirationDate = Date.from(now.plusSeconds(expiration).atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(issuedAt)
                .expiration(expirationDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Valida un token JWT
     */
    public Boolean validateToken(String token, String username) {
        try {
            final String extractedUsername = extractUsername(token);
            return (extractedUsername.equals(username) && !isTokenExpired(token));
        } catch (JwtException e) {
            logger.warn("Token JWT inválido: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Valida un refresh token
     */
    public Boolean validateRefreshToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            String type = claims.get("type", String.class);
            return "refresh".equals(type) && !isTokenExpired(token);
        } catch (JwtException e) {
            logger.warn("Refresh token inválido: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extrae información del usuario desde el token
     */
    public UserInfo extractUserInfo(String token) {
        try {
            Claims claims = extractAllClaims(token);
            
            UserInfo userInfo = new UserInfo();
            userInfo.setUserId(claims.get("userId", Long.class));
            userInfo.setEmail(claims.getSubject());
            userInfo.setNombre(claims.get("nombre", String.class));
            userInfo.setRole(Role.valueOf(claims.get("role", String.class)));
            
            if (claims.containsKey("pymeId")) {
                userInfo.setPymeId(claims.get("pymeId", Long.class));
            }
            
            if (claims.containsKey("permissions")) {
                @SuppressWarnings("unchecked")
                List<String> permissions = (List<String>) claims.get("permissions");
                userInfo.setPermissions(permissions);
            }
            
            return userInfo;
        } catch (JwtException e) {
            logger.error("Error al extraer información del usuario del token: {}", e.getMessage());
            throw new JwtException("Error al extraer información del token", e);
        }
    }

    /**
     * Extrae el rol del usuario desde el token
     */
    public Role extractRole(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return Role.valueOf(claims.get("role", String.class));
        } catch (JwtException e) {
            logger.error("Error al extraer rol del token: {}", e.getMessage());
            throw new JwtException("Error al extraer rol del token", e);
        }
    }

    /**
     * Extrae los permisos del usuario desde el token
     */
    @SuppressWarnings("unchecked")
    public List<String> extractPermissions(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return (List<String>) claims.get("permissions");
        } catch (JwtException e) {
            logger.error("Error al extraer permisos del token: {}", e.getMessage());
            throw new JwtException("Error al extraer permisos del token", e);
        }
    }

    /**
     * Verifica si el token es un refresh token
     */
    public Boolean isRefreshToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            String type = claims.get("type", String.class);
            return "refresh".equals(type);
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * Obtiene el tiempo de expiración en segundos
     */
    public Long getExpirationInSeconds() {
        return jwtExpiration;
    }

    /**
     * Obtiene el tiempo de expiración del refresh token en segundos
     */
    public Long getRefreshExpirationInSeconds() {
        return refreshExpiration;
    }

    /**
     * Obtiene la fecha de expiración del token
     */
    public LocalDateTime getExpirationDate(String token) {
        Date expirationDate = extractExpiration(token);
        return expirationDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    // Clase interna para información de usuario desde token
    public static class UserInfo {
        private Long userId;
        private String email;
        private String nombre;
        private Role role;
        private Long pymeId;
        private List<String> permissions;

        // Getters y Setters
        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public Role getRole() {
            return role;
        }

        public void setRole(Role role) {
            this.role = role;
        }

        public Long getPymeId() {
            return pymeId;
        }

        public void setPymeId(Long pymeId) {
            this.pymeId = pymeId;
        }

        public List<String> getPermissions() {
            return permissions;
        }

        public void setPermissions(List<String> permissions) {
            this.permissions = permissions;
        }

        @Override
        public String toString() {
            return "UserInfo{" +
                    "userId=" + userId +
                    ", email='" + email + '\'' +
                    ", nombre='" + nombre + '\'' +
                    ", role=" + role +
                    ", pymeId=" + pymeId +
                    ", permissions=" + permissions +
                    '}';
        }
    }
}
