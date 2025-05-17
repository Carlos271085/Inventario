package com.inventario.inventario.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inventario.inventario.model.Inventario;
import com.inventario.inventario.service.InventarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/inventarios")
@Tag(name = "Inventario", description = "API para gestionar inventarios")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @Operation(summary = "Crear un nuevo inventario")
    @ApiResponse(responseCode = "200", description = "Inventario creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de inventario inválidos")
    @PostMapping
    public ResponseEntity<String> crearInventario(@Valid @RequestBody Inventario inventario) {
        String resultado = inventarioService.crearInventario(inventario);
        if (resultado.startsWith("Error")) {
            return ResponseEntity.badRequest().body(resultado);
        }
        return ResponseEntity.ok(resultado);
    }

    @Operation(summary = "Obtener inventario por ID")
    @ApiResponse(responseCode = "200", description = "Inventario encontrado")
    @ApiResponse(responseCode = "404", description = "Inventario no encontrado")
    @GetMapping("/{idProducto}")
    public ResponseEntity<Inventario> obtenerInventario(@PathVariable Long idProducto) {
        return Optional.ofNullable(inventarioService.obtenerInventario(idProducto))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar inventario por ID")
    @DeleteMapping("/{idProducto}")
    public ResponseEntity<String> eliminarInventario(@PathVariable Long idProducto) {
        String resultado = inventarioService.eliminarInventario(idProducto);
        if (resultado.contains("eliminado correctamente")) {
            return ResponseEntity.ok(resultado);
        }
        return ResponseEntity.status(404).body(resultado);
    }

    @Operation(summary = "Actualizar inventario existente")
    @PutMapping("/{idProducto}")
    public ResponseEntity<String> actualizarInventario(
            @PathVariable long idProducto,
            @Valid @RequestBody Inventario inventario) {
        String resultado = inventarioService.actualizarInventario(idProducto, inventario);
        if (resultado.contains("actualizado correctamente")) {
            return ResponseEntity.ok(resultado);
        }
        return ResponseEntity.status(404).body(resultado);
    }

    @Operation(summary = "Listar todos los inventarios")
    @GetMapping
    public ResponseEntity<List<Inventario>> listarInventarios() {
        List<Inventario> inventarios = inventarioService.listarInventarios();
        return inventarios.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(inventarios);
    }

    @Operation(summary = "Verificar disponibilidad de stock")
    @GetMapping("/verificar-stock/{idProducto}")
    public ResponseEntity<String> verificarStock(
            @PathVariable long idProducto,
            @RequestParam @Positive int cantidadRequerida) {
        boolean disponible = inventarioService.verificarStock(idProducto, cantidadRequerida);
        return disponible
                ? ResponseEntity.ok("Stock disponible")
                : ResponseEntity.badRequest().body("Stock insuficiente");
    }

    @Operation(summary = "Buscar inventarios por rango de stock")
    @GetMapping("/buscar-por-stock")
    public ResponseEntity<List<Inventario>> buscarPorRangoDeStock(
            @RequestParam @PositiveOrZero int minStock,
            @RequestParam @Positive int maxStock) {
        List<Inventario> inventarios = inventarioService.buscarPorRangoDeStock(minStock, maxStock);
        return inventarios.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(inventarios);
    }
}
