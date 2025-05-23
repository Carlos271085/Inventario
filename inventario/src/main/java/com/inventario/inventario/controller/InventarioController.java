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
            // Validar que se proporcionen ambos campos obligatorios
            if (inventario.getStockDisponible() <= 0 &&
                    (inventario.getUbicacionBodega() == null || inventario.getUbicacionBodega().trim().isEmpty())) {
                return ResponseEntity
                        .badRequest()
                        .body(Map.of("error", "El stock disponible y la ubicación de bodega son campos obligatorios"));
            }

            // Validar campos individualmente para mensajes específicos
            if (inventario.getStockDisponible() <= 0) {
                return ResponseEntity
                        .badRequest()
                        .body(Map.of("error", "El stock disponible debe ser mayor a 0"));
            }

            if (inventario.getUbicacionBodega() == null || inventario.getUbicacionBodega().trim().isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body(Map.of("error", "La ubicación de bodega es obligatoria"));
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
    @DeleteMapping("/{idInventario}")
    @Operation(summary = "Eliminar un inventario existente")
    public ResponseEntity<String> eliminarInventario(@PathVariable Long idInventario) {
        String resultado = inventarioService.eliminarInventario(idInventario);
        if (resultado.contains("eliminado correctamente")) {
            return ResponseEntity.ok(resultado);
        }
        return ResponseEntity.status(404).body(resultado);
    }

    // Actualizacion de un Inventario.
    @PutMapping("/{idInventario}")
    @Operation(summary = "Actualizar solo el stock disponible de un inventario")
    @ApiResponse(responseCode = "200", description = "Stock actualizado exitosamente")
    @ApiResponse(responseCode = "404", description = "Inventario no encontrado")
    @ApiResponse(responseCode = "400", description = "Stock inválido")
    public ResponseEntity<Map<String, String>> actualizarInventario(
            @PathVariable long idInventario,
            @Valid @RequestBody Inventario inventario) {
        try {
            // Validar que el stock sea válido
            if (inventario.getStockDisponible() <= 0) {
                return ResponseEntity
                        .badRequest()
                        .body(Map.of("error", "El stock debe ser mayor a 0"));
            }

            String resultado = inventarioService.actualizarInventario(idInventario, inventario);

            if (resultado.equals("Inventario no encontrado")) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Inventario no encontrado"));
            }

            return ResponseEntity.ok(Map.of("mensaje", "Stock actualizado correctamente"));

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al actualizar el stock: " + e.getMessage()));
        }
    }

    // Ubicación de una Bodega.
    @GetMapping("/ubicacion/{ubicacionBodega}")
    @Operation(summary = "Obtener inventarios por ubicación de bodega")
    @ApiResponse(responseCode = "200", description = "Inventarios encontrados")
    @ApiResponse(responseCode = "404", description = "No se encontraron inventarios en esa ubicación")
    public ResponseEntity<?> obtenerInventarioPorUbicacion(@PathVariable String ubicacionBodega) {
        List<Inventario> inventarios = inventarioService.obtenerInventarioPorUbicacion(ubicacionBodega);
        if (inventarios == null || inventarios.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", "No se encontraron inventarios en la ubicación: " + ubicacionBodega));
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
