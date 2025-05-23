package com.inventario.inventario.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventario.inventario.model.Inventario;
import com.inventario.inventario.service.InventarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1/inventarios")
// @Tag(name = "Inventario", description = "API para gestionar inventarios")
public class InventarioController {

@Autowired
    private InventarioService inventarioService;

    // Creación de un nuevo Inventario.
    @PostMapping
    @Operation(summary = "Crear un nuevo inventario")
    @ApiResponse(responseCode = "201", description = "Inventario creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de inventario inválidos o incompletos")
    @ApiResponse(responseCode = "409", description = "El inventario ya existe")
    public ResponseEntity<Map<String, String>> crearInventario(@Valid @RequestBody Inventario inventario) {
        try {
            // Validar campos obligatorios de forma individual
            StringBuilder mensajeError = new StringBuilder("Campos obligatorios faltantes: ");
            boolean hayErrores = false;

            if (inventario.getStockDisponible() <= 0) {
                mensajeError.append("stock disponible debe ser mayor a 0");
                hayErrores = true;
            }
            
            if (inventario.getUbicacionBodega() == null || inventario.getUbicacionBodega().trim().isEmpty()) {
                if (hayErrores) {
                    mensajeError.append(" y ");
                }
                mensajeError.append("ubicación de bodega");
                hayErrores = true;
            }

            if (hayErrores) {
                return ResponseEntity
                        .badRequest()
                        .body(Map.of("error", mensajeError.toString()));
            }

            // Establecer fecha actual automáticamente
            inventario.setFechaUltimaActualizacion(LocalDateTime.now());
            
            String resultado = inventarioService.crearInventario(inventario);
            
            if (resultado.contains("ya existe")) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(Map.of("error", resultado));
            }
            
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of("mensaje", resultado));
                    
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al crear el inventario: " + e.getMessage()));
        }
    }

    // Eliminación de un Inventario.
    @DeleteMapping("/{idProducto}")
    @Operation(summary = "Eliminar un inventario existente")
    public ResponseEntity<String> eliminarInventario(@PathVariable Long idProducto) {
        String resultado = inventarioService.eliminarInventario(idProducto);
        if (resultado.contains("eliminado correctamente")) {
            return ResponseEntity.ok(resultado);
        }
        return ResponseEntity.status(404).body(resultado);
    }

    // Actualizacion de un Inventario.
    @PutMapping("/{idProducto}")
    @Operation(summary = "Actualizar stock disponible en un inventario existente")
    @ApiResponse(responseCode = "200", description = "Inventario actualizado exitosamente")
    @ApiResponse(responseCode = "404", description = "Inventario no encontrado")
    @ApiResponse(responseCode = "400", description = "Datos de inventario inválidos")
    public ResponseEntity<Map<String, String>> actualizarInventario(
            @PathVariable long idProducto,
            @Valid @RequestBody Inventario inventario) {
        try {
            // Validar que el ID del path coincida con el ID del objeto
            if (inventario.getIdProducto() != idProducto) {
                return ResponseEntity
                        .badRequest()
                        .body(Map.of("error", "El ID del path no coincide con el ID del inventario"));
            }

            String resultado = inventarioService.actualizarInventario(idProducto, inventario);

            if (resultado.equals("Inventario no encontrado")) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Inventario no encontrado"));
            }

            return ResponseEntity.ok(Map.of("mensaje", "Inventario actualizado exitosamente"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al actualizar el inventario: " + e.getMessage()));
        }
    }

    // Ubicación de una Bodega.
    @GetMapping("/ubicacion/{ubicacionBodega}")
    @Operation(summary = "Obtener inventarios por ubicación de bodega")
    @ApiResponse(responseCode = "200", description = "Inventarios encontrados")
    @ApiResponse(responseCode = "404", description = "No se encontraron inventarios en esa ubicación")
    public ResponseEntity<List<Inventario>> obtenerInventarioPorUbicacion(@PathVariable String ubicacionBodega) {
        List<Inventario> inventarios = inventarioService.obtenerInventarioPorUbicacion(ubicacionBodega);
        if (inventarios == null || inventarios.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(inventarios);
    }

    // Obtener todos los Inventarios.
    @GetMapping
    @Operation(summary = "Obtiene todos los inventarios")
    public ResponseEntity<List<Inventario>> listarInventarios() {
        List<Inventario> inventarios = inventarioService.listarInventarios();
        return ResponseEntity.ok(inventarios);
    }

}
