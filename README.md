# ms-auth - Microservicio de Autenticación

Microservicio Spring Boot para autenticación y gestión de tokens JWT en el sistema PymeTrack.

## 🚀 Características

- ✅ **Autenticación JWT** con tokens de acceso y refresh
- ✅ **Integración con ms-admin** para validación de credenciales
- ✅ **Soporte para contraseñas temporales** (primer login)
- ✅ **Seguridad** con Spring Security y BCrypt
- ✅ **Validación de tokens** y refresh automático
- ✅ **Documentación** con SpringDoc OpenAPI
- ✅ **Logging** estructurado con SLF4J
- ✅ **CORS** configurado para frontend

## 📋 Requisitos

- Java 17+
- Maven 3.8+
- **ms-admin** corriendo en localhost:8080
- Spring Boot 3.2.0

## 🛠️ Configuración

### JWT Configuration
```yaml
jwt:
  secret: mySecretKey123456789012345678901234567890  # 32+ caracteres
  expiration: 86400  # 24 horas en segundos
  refresh-expiration: 604800  # 7 días en segundos
```

### ms-admin Integration
```yaml
ms-admin:
  base-url: http://localhost:8080
```

### Puerto del Servidor
```yaml
server:
  port: 8082
```

## 📚 API Documentation

### Endpoints Principales

#### 1. Login
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "contacto@techstore.cl",
  "password": "Temp123!@#"
}
```

**Respuesta (Primer Login):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400,
  "expiresAt": "2024-05-03T13:45:00",
  "pyme": {
    "id": 1,
    "nombrePyme": "TechStore SPA",
    "emailContacto": "contacto@techstore.cl",
    "rutPyme": "76.123.456-7",
    "requiresPasswordChange": true
  },
  "requiresPasswordChange": true,
  "message": "Debe cambiar su contraseña en el primer inicio de sesión"
}
```

**Respuesta (Login Normal):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400,
  "expiresAt": "2024-05-03T13:45:00",
  "pyme": {
    "id": 1,
    "nombrePyme": "TechStore SPA",
    "emailContacto": "contacto@techstore.cl",
    "rutPyme": "76.123.456-7",
    "requiresPasswordChange": false
  },
  "requiresPasswordChange": false,
  "message": "Login exitoso"
}
```

#### 2. Cambiar Contraseña
```http
POST /api/v1/auth/change-password/1
Content-Type: application/json
Authorization: Bearer <token>

{
  "currentPassword": "Temp123!@#",
  "newPassword": "NuevaPassword123!",
  "confirmPassword": "NuevaPassword123!"
}
```

**Respuesta:**
```json
{
  "success": true,
  "message": "Contraseña cambiada exitosamente"
}
```

#### 3. Refresh Token
```http
POST /api/v1/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

#### 4. Validar Token
```http
POST /api/v1/auth/validate
Authorization: Bearer <token>
```

**Respuesta:**
```json
{
  "valid": true,
  "tokenType": "Bearer",
  "expiresIn": 86400,
  "expiresAt": "2024-05-03T13:45:00",
  "pyme": {
    "id": 1,
    "nombrePyme": "TechStore SPA",
    "emailContacto": "contacto@techstore.cl",
    "rutPyme": "76.123.456-7"
  }
}
```

#### 5. Obtener Información del Usuario
```http
GET /api/v1/auth/me
Authorization: Bearer <token>
```

#### 6. Logout
```http
POST /api/v1/auth/logout
Authorization: Bearer <token>
```

## 🔐 Flujo de Autenticación

### 1. Primer Login (Contraseña Temporal)
```
1. PYME ingresa email + contraseña temporal
2. ms-auth valida con ms-admin
3. ms-auth genera tokens JWT
4. Respuesta indica requiresPasswordChange: true
5. Frontend redirige a página de cambio de contraseña
6. PYME cambia contraseña
7. ms-admin actualiza estado a ACTIVE
8. Se genera nuevo token normal
```

### 2. Login Normal
```
1. PYME ingresa email + contraseña permanente
2. ms-auth valida con ms-admin
3. ms-auth genera tokens JWT
4. Respuesta indica requiresPasswordChange: false
5. Frontend redirige al dashboard
```

### 3. Refresh Token
```
1. Frontend envía refresh token
2. ms-auth valida refresh token
3. ms-auth genera nuevo access token
4. Frontend actualiza localStorage
```

## 🛡️ Seguridad

### JWT Tokens
- **Access Token**: 24 horas de validez
- **Refresh Token**: 7 días de validez
- **Algoritmo**: HS256 (HMAC-SHA256)
- **Claims**: pymeId, email, nombrePyme, rutPyme, role

### Validación de Contraseñas
- **Contraseñas temporales**: Sin encriptar, expiran en 7 días
- **Contraseñas permanentes**: Encriptadas con BCrypt
- **Intentos fallidos**: Máximo 5, bloqueo 30 minutos

### CORS
- **Orígenes permitidos**: Todos (configurable)
- **Métodos**: GET, POST, PUT, DELETE, OPTIONS
- **Headers**: Todos los permitidos
- **Credentials**: Habilitados

## 🚀 Ejecución

### Desarrollo
```bash
mvn spring-boot:run
```

### Producción
```bash
mvn clean package
java -jar target/ms-auth-0.0.1-SNAPSHOT.jar
```

## 📖 Swagger UI

Accede a la documentación interactiva en:
```
http://localhost:8082/swagger-ui.html
```

## 🗂️ Estructura del Proyecto

```
src/main/java/cl/pymetrack/msauth/
├── dto/                    # Data Transfer Objects
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   ├── ChangePasswordRequest.java
│   ├── ChangePasswordResponse.java
│   ├── RefreshTokenRequest.java
│   └── TokenValidationResponse.java
├── client/                 # Cliente de ms-admin
│   └── PymeClient.java
├── security/               # JWT y seguridad
│   └── JwtService.java
├── service/               # Lógica de negocio
│   └── AuthService.java
├── controller/            # Endpoints REST
│   └── AuthController.java
├── config/                # Configuración
│   └── SecurityConfig.java
└── exception/             # Excepciones personalizadas
    ├── AuthenticationException.java
    ├── InvalidCredentialsException.java
    ├── PymeNotFoundException.java
    └── PymeServiceException.java
```

## 🔄 Integración con ms-admin

### Comunicación vía WebClient
```java
// Buscar PYME por email
Optional<PymeData> pyme = pymeClient.getPymeByEmail(email);

// Cambiar contraseña
boolean success = pymeClient.changePassword(pymeId, currentPassword, newPassword);

// Registrar intento fallido
pymeClient.registerFailedLogin(email);

// Resetear intentos fallidos
pymeClient.resetFailedAttempts(pymeId);
```

### Endpoints de ms-admin utilizados
- `GET /api/v1/pymes/search/by-email?email={email}`
- `POST /api/v1/pymes/{id}/cambiar-password`
- `POST /api/v1/pymes/register-failed-login`
- `POST /api/v1/pymes/{id}/reset-attempts`

## 📊 Monitoreo

### Logs
```bash
# Ver logs en tiempo real
tail -f logs/ms-auth.log
```

### Health Check
```bash
curl http://localhost:8082/api/v1/auth/health
```

## 🐛 Troubleshooting

### Error: Token inválido
```json
{
  "timestamp": "2024-05-02T13:45:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Token inválido o expirado"
}
```

**Solución:** Generar nuevo token con refresh token o hacer login nuevamente.

### Error: Credenciales inválidas
```json
{
  "timestamp": "2024-05-02T13:45:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Credenciales inválidas"
}
```

**Solución:** Verificar email y contraseña, o si la cuenta está bloqueada.

### Error: ms-admin no disponible
```json
{
  "timestamp": "2024-05-02T13:45:00",
  "status": 503,
  "error": "Service Unavailable",
  "message": "Error al comunicarse con ms-admin"
}
```

**Solución:** Verificar que ms-admin esté corriendo en localhost:8080.

## 🤝 Próximos Pasos

1. **API Gateway** - Configurar routing para ms-auth
2. **BFF** - Integrar autenticación en endpoints agregados
3. **Frontend** - Implementar flujo de login completo
4. **Tests** - Agregar tests unitarios y de integración

## 📝 Notas de Desarrollo

- **Base de datos**: No requiere base de datos propia (usa ms-admin)
- **Estado**: Stateless (sin sesión en servidor)
- **Escalabilidad**: Horizontalmente escalable
- **Logging**: Nivel DEBUG para entorno de desarrollo
- **Documentación**: OpenAPI/Swagger disponible automáticamente
