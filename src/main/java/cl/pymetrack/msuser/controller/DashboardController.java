package cl.pymetrack.msuser.controller;

import cl.pymetrack.msuser.model.User;
import cl.pymetrack.msuser.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dashboard")
@Tag(name = "Dashboard PYME", description = "API para obtener datos del dashboard de una PYME")
public class DashboardController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/pyme/{pymeId}")
    @Operation(summary = "Obtener dashboard completo de PYME", description = "Retorna todos los datos necesarios para el dashboard de una PYME específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dashboard obtenido exitosamente",
                content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "404", description = "PYME no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Map<String, Object>> getDashboardPyme(
            @Parameter(description = "ID de la PYME", required = true)
            @PathVariable Long pymeId) {
        
        logger.info("Obteniendo dashboard para PYME: {}", pymeId);
        
        try {
            // Obtener información de la PYME desde usuarios
            List<User> pymeUsers = userService.findActiveUsersByPymeId(pymeId);
            
            if (pymeUsers.isEmpty()) {
                logger.warn("No se encontraron usuarios para la PYME: {}", pymeId);
                return ResponseEntity.notFound().build();
            }
            
            // El usuario principal de la PYME (primer usuario encontrado)
            User pymePrincipal = pymeUsers.get(0);
            
            // Construir dashboard completo
            Map<String, Object> dashboard = new HashMap<>();
            
            // Información de la PYME
            Map<String, Object> pymeInfo = new HashMap<>();
            pymeInfo.put("id", pymeId);
            pymeInfo.put("nombre", pymePrincipal.getNombre() + " " + pymePrincipal.getApellido());
            pymeInfo.put("email", pymePrincipal.getEmail());
            pymeInfo.put("rol", pymePrincipal.getRol().toString());
            pymeInfo.put("pymeId", pymeId);
            pymeInfo.put("usuariosCount", pymeUsers.size());
            dashboard.put("pymeInfo", pymeInfo);
            
            // Estadísticas básicas (simuladas por ahora)
            Map<String, Object> estadisticas = new HashMap<>();
            estadisticas.put("pedidosTotales", 0);
            estadisticas.put("pedidosHoy", 0);
            estadisticas.put("productosActivos", 0);
            estadisticas.put("stockBajo", 0);
            estadisticas.put("ingresosTotales", 0);
            estadisticas.put("ingresosHoy", 0);
            estadisticas.put("usuariosActivos", pymeUsers.size());
            dashboard.put("estadisticas", estadisticas);
            
            // Lista de usuarios de la PYME
            List<Map<String, Object>> usuariosInfo = pymeUsers.stream()
                    .map(user -> {
                        Map<String, Object> userInfo = new HashMap<>();
                        userInfo.put("id", user.getId());
                        userInfo.put("email", user.getEmail());
                        userInfo.put("nombre", user.getNombre());
                        userInfo.put("apellido", user.getApellido());
                        userInfo.put("rol", user.getRol().toString());
                        userInfo.put("activo", user.getActivo());
                        userInfo.put("pymeId", user.getPymeId());
                        return userInfo;
                    })
                    .collect(Collectors.toList());
            dashboard.put("usuarios", usuariosInfo);
            
            // Productos (simulados por ahora)
            dashboard.put("productos", Collections.emptyList());
            
            // Pedidos (simulados por ahora)
            dashboard.put("pedidos", Collections.emptyList());
            
            // Alertas
            List<Map<String, Object>> alertas = new ArrayList<>();
            if (pymeUsers.size() == 1) {
                Map<String, Object> alerta = new HashMap<>();
                alerta.put("tipo", "info");
                alerta.put("mensaje", "Considera agregar más usuarios a tu PYME");
                alerta.put("prioridad", "baja");
                alertas.add(alerta);
            }
            dashboard.put("alertas", alertas);
            
            // Metadatos
            dashboard.put("ultimaActualizacion", new Date());
            dashboard.put("version", "1.0.0");
            
            logger.info("Dashboard construido exitosamente para PYME: {}", pymeId);
            return ResponseEntity.ok(dashboard);
            
        } catch (Exception e) {
            logger.error("Error al construir dashboard para PYME {}: {}", pymeId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener dashboard", "message", e.getMessage()));
        }
    }

    @GetMapping("/pyme/{pymeId}/resumen")
    @Operation(summary = "Obtener resumen de PYME", description = "Retorna un resumen rápido con información clave de la PYME")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Resumen obtenido exitosamente"),
        @ApiResponse(responseCode = "404", description = "PYME no encontrada")
    })
    public ResponseEntity<Map<String, Object>> getResumenPyme(
            @Parameter(description = "ID de la PYME", required = true)
            @PathVariable Long pymeId) {
        
        logger.info("Obteniendo resumen para PYME: {}", pymeId);
        
        try {
            List<User> pymeUsers = userService.findActiveUsersByPymeId(pymeId);
            
            if (pymeUsers.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Map<String, Object> resumen = new HashMap<>();
            User pymePrincipal = pymeUsers.get(0);
            
            // Información básica
            resumen.put("pyme", Map.of(
                "id", pymeId,
                "nombre", pymePrincipal.getNombre() + " " + pymePrincipal.getApellido(),
                "email", pymePrincipal.getEmail(),
                "rol", pymePrincipal.getRol().toString()
            ));
            
            // Métricas clave
            resumen.put("pedidosTotales", 0);
            resumen.put("productosActivos", 0);
            resumen.put("ingresosHoy", 0);
            resumen.put("usuariosActivos", pymeUsers.size());
            
            // Estado del sistema
            resumen.put("sistema", Map.of(
                "estado", "operativo",
                "servicios", Map.of(
                    "ms-user", "activo",
                    "ms-productos", "pendiente",
                    "ms-pedidos", "pendiente"
                )
            ));
            
            logger.info("Resumen construido exitosamente para PYME: {}", pymeId);
            return ResponseEntity.ok(resumen);
            
        } catch (Exception e) {
            logger.error("Error al obtener resumen para PYME {}: {}", pymeId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener resumen", "message", e.getMessage()));
        }
    }

    @GetMapping("/pyme/{pymeId}/estadisticas")
    @Operation(summary = "Obtener estadísticas de PYME", description = "Retorna estadísticas detalladas de una PYME")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente"),
        @ApiResponse(responseCode = "404", description = "PYME no encontrada")
    })
    public ResponseEntity<Map<String, Object>> getEstadisticasPyme(
            @Parameter(description = "ID de la PYME", required = true)
            @PathVariable Long pymeId) {
        
        logger.info("Obteniendo estadísticas para PYME: {}", pymeId);
        
        try {
            List<User> pymeUsers = userService.findActiveUsersByPymeId(pymeId);
            
            Map<String, Object> estadisticas = new HashMap<>();
            
            // Estadísticas de usuarios
            estadisticas.put("totalUsuarios", pymeUsers.size());
            estadisticas.put("usuariosActivos", pymeUsers.size());
            estadisticas.put("usuariosPorRol", pymeUsers.stream()
                    .collect(Collectors.groupingBy(user -> user.getRol().toString(), 
                            Collectors.counting())));
            
            // Estadísticas de negocio (simuladas)
            estadisticas.put("totalPedidos", 0);
            estadisticas.put("pedidosHoy", 0);
            estadisticas.put("totalProductos", 0);
            estadisticas.put("productosActivos", 0);
            estadisticas.put("ingresosTotales", 0);
            estadisticas.put("ingresosHoy", 0);
            
            // Métricas del sistema
            estadisticas.put("sistema", Map.of(
                "ultimaActualizacion", new Date(),
                "serviciosActivos", 1, // Solo ms-user
                "serviciosTotales", 3
            ));
            
            logger.info("Estadísticas construidas exitosamente para PYME: {}", pymeId);
            return ResponseEntity.ok(estadisticas);
            
        } catch (Exception e) {
            logger.error("Error al obtener estadísticas para PYME {}: {}", pymeId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener estadísticas", "message", e.getMessage()));
        }
    }

    @GetMapping("/pyme/{pymeId}/productos")
    @Operation(summary = "Obtener productos de PYME", description = "Retorna lista de productos de una PYME (placeholder)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Productos obtenidos exitosamente"),
        @ApiResponse(responseCode = "404", description = "PYME no encontrada")
    })
    public ResponseEntity<List<Map<String, Object>>> getProductosPyme(
            @Parameter(description = "ID de la PYME", required = true)
            @PathVariable Long pymeId,
            @Parameter(description = "Categoría para filtrar (opcional)")
            @RequestParam(required = false) String categoria) {
        
        logger.info("Obteniendo productos para PYME: {} (categoría: {})", pymeId, categoria);
        
        try {
            // Por ahora, retornamos lista vacía como placeholder
            // En el futuro, esto se conectará con ms-productos
            List<Map<String, Object>> productos = new ArrayList<>();
            
            // Producto de ejemplo
            Map<String, Object> productoEjemplo = new HashMap<>();
            productoEjemplo.put("id", 1);
            productoEjemplo.put("nombre", "Producto Ejemplo");
            productoEjemplo.put("descripcion", "Este es un producto de ejemplo para PYME " + pymeId);
            productoEjemplo.put("precio", 0);
            productoEjemplo.put("stock", 0);
            productoEjemplo.put("categoria", categoria != null ? categoria : "general");
            productoEjemplo.put("activo", true);
            productoEjemplo.put("pymeId", pymeId);
            productoEjemplo.put("creadoEn", new Date());
            
            productos.add(productoEjemplo);
            
            logger.info("Productos obtenidos exitosamente para PYME: {}", pymeId);
            return ResponseEntity.ok(productos);
            
        } catch (Exception e) {
            logger.error("Error al obtener productos para PYME {}: {}", pymeId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/pyme/{pymeId}/pedidos")
    @Operation(summary = "Obtener pedidos de PYME", description = "Retorna lista de pedidos de una PYME (placeholder)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pedidos obtenidos exitosamente"),
        @ApiResponse(responseCode = "404", description = "PYME no encontrada")
    })
    public ResponseEntity<List<Map<String, Object>>> getPedidosPyme(
            @Parameter(description = "ID de la PYME", required = true)
            @PathVariable Long pymeId,
            @Parameter(description = "Estado para filtrar (opcional)")
            @RequestParam(required = false) String estado) {
        
        logger.info("Obteniendo pedidos para PYME: {} (estado: {})", pymeId, estado);
        
        try {
            // Por ahora, retornamos lista vacía como placeholder
            // En el futuro, esto se conectará con ms-pedidos
            List<Map<String, Object>> pedidos = new ArrayList<>();
            
            // Pedido de ejemplo
            Map<String, Object> pedidoEjemplo = new HashMap<>();
            pedidoEjemplo.put("id", 1);
            pedidoEjemplo.put("numero", "PED-001");
            pedidoEjemplo.put("cliente", "Cliente Ejemplo");
            pedidoEjemplo.put("total", 0);
            pedidoEjemplo.put("estado", estado != null ? estado : "pendiente");
            pedidoEjemplo.put("activo", true);
            pedidoEjemplo.put("pymeId", pymeId);
            pedidoEjemplo.put("creadoEn", new Date());
            
            pedidos.add(pedidoEjemplo);
            
            logger.info("Pedidos obtenidos exitosamente para PYME: {}", pymeId);
            return ResponseEntity.ok(pedidos);
            
        } catch (Exception e) {
            logger.error("Error al obtener pedidos para PYME {}: {}", pymeId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
