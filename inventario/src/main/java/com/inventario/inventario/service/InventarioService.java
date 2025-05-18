package com.inventario.inventario.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.inventario.inventario.model.Inventario;
import com.inventario.inventario.model.entity.InventarioEntity;
import com.inventario.inventario.repository.InventarioRepository;

@Service

public class InventarioService {
    @Autowired
    private InventarioRepository inventariorepository;

    public String crearInventario(Inventario inv) {
        try {
            // Validar que el inventario no sea nulo y tenga datos válidos
            if (inv == null || inv.getIdProducto() <= 0 || inv.getStockDisponible() < 0
                    || inv.getUbicacionBodega() == null) {
                return "Error: Inventario no válido";
            }

            Boolean estado = inventariorepository.existsById(inv.getIdProducto());
            if (!estado) {
                InventarioEntity inventarioNuevo = new InventarioEntity();
                inventarioNuevo.setIdProducto(inv.getIdProducto());
                inventarioNuevo.setStockDisponible(inv.getStockDisponible());
                inventarioNuevo.setUbicacionBodega(inv.getUbicacionBodega());
                // Si no se proporciona fecha, usar la fecha actual
                inventarioNuevo.setFechaUltimaActualizacion(
                        inv.getFechaUltimaActualizacion() != null ? inv.getFechaUltimaActualizacion()
                                : LocalDateTime.now());
                inventariorepository.save(inventarioNuevo);
                return "Inventario creado correctamente";
            }
            return "El inventario ya existe";
        } catch (Exception e) {
            return "Error al crear el inventario: " + e.getMessage();
        }

    }

    // Método permite buscar un inventario específico por su idProducto
    public Inventario obtenerInventario(long idProducto) {
        try {
            return inventariorepository.findById(idProducto)
                    .map(inventario -> new Inventario(
                            inventario.getIdProducto(),
                            inventario.getStockDisponible(),
                            inventario.getUbicacionBodega(),
                            inventario.getFechaUltimaActualizacion()))
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    // Método permite actualizar todos los campos de un inventario existente.
    public String actualizarInventario(long idProducto, Inventario inv) {
        try {
            // Buscar inventario existente por ID
            InventarioEntity existente = inventariorepository.findById(idProducto).orElse(null);
            if (existente != null) {
                existente.setStockDisponible(inv.getStockDisponible());
                existente.setUbicacionBodega(inv.getUbicacionBodega());
                existente.setFechaUltimaActualizacion(inv.getFechaUltimaActualizacion());
                inventariorepository.save(existente);
                return "Inventario actualizado correctamente";
            }
            return "Inventario no encontrado";
        } catch (DataAccessException e) {
            return "Error al acceder a la base de datos para el producto con ID " + idProducto + ": " + e.getMessage();
        } catch (Exception e) {
            return "Error inesperado al actualizar el inventario para el producto con ID " + idProducto + ": "
                    + e.getMessage();
        }
    }

    // Método permite eliminar un inventario por su idProducto
    public String eliminarInventario(long idProducto) {
        try {
            if (inventariorepository.existsById(idProducto)) {
                inventariorepository.deleteById(idProducto);
                return "Inventario eliminado correctamente";
            }
            return "Inventario no encontrado";
        } catch (DataAccessException e) {
            return "Error al acceder a la base de datos para el producto con ID " + idProducto + ": " + e.getMessage();
        } catch (Exception e) {
            return "Error inesperado al eliminar el inventario para el producto con ID " + idProducto + ": "
                    + e.getMessage();
        }
    }

    // Método devuelve una lista de todos los inventarios almacenados.
    public List<Inventario> listarInventarios() {
        try {
            List<InventarioEntity> entidades = inventariorepository.findAll();
            return entidades.stream().map(entidad -> new Inventario(
                    entidad.getIdProducto(),
                    entidad.getStockDisponible(),
                    entidad.getUbicacionBodega(),
                    entidad.getFechaUltimaActualizacion())).collect(Collectors.toList());
        } catch (DataAccessException e) {
            return Collections.emptyList(); // Manejar erros de base de datos
        } catch (Exception e) {
            return Collections.emptyList(); // Manejar errores inesperados
        }
    }

    // Método permite actualizar solo algunos campos de un inventario.
    public String actualizarParcialInventario(long idProducto, Map<String, Object> campos) {
        try {
            InventarioEntity existente = inventariorepository.findById(idProducto).orElse(null);
            if (existente != null) {
                if (campos.containsKey("stockDisponible")) {
                    try {
                        existente.setStockDisponible(((Number) campos.get("stockDisponible")).intValue());
                    } catch (ClassCastException e) {
                        return "Error: stockDisponible debe ser un número";
                    }
                }
                if (campos.containsKey("ubicacionBodega")) {
                    existente.setUbicacionBodega((String) campos.get("ubicacionBodega"));
                }
                if (campos.containsKey("fechaUltimaActualizacion")) {
                    try {
                        if (campos.get("fechaUltimaActualizacion") instanceof String) {
                            String fechaStr = (String) campos.get("fechaUltimaActualizacion");
                            LocalDateTime fecha = LocalDateTime.parse(fechaStr, DateTimeFormatter.ISO_DATE_TIME);
                            existente.setFechaUltimaActualizacion(fecha);
                        } else if (campos.get("fechaUltimaActualizacion") instanceof LocalDateTime) {
                            existente.setFechaUltimaActualizacion(
                                    (LocalDateTime) campos.get("fechaUltimaActualizacion"));
                        } else {
                            return "Error: formato de fecha no válido";
                        }
                    } catch (DateTimeParseException e) {
                        return "Error: formato de fecha inválido. Use el formato ISO (yyyy-MM-ddTHH:mm:ss)";
                    }
                }
                inventariorepository.save(existente);
                return "Inventario actualizado parcialmente";
            }
            return "Inventario no encontrado";

        } catch (DataAccessException e) {
            return "Error al acceder a la base de datos para el producto con ID " + idProducto + ": " + e.getMessage();

        } catch (Exception e) {
            return "Error inesperado al actualizar parcialmente el inventario para el producto con ID " + idProducto
                    + ": " + e.getMessage();
        }

    }

    // Método permite verificar si hay suficiente Stock disponible de un producto.
    public boolean verificarStock(long idProducto, int cantidadRequerida) {
        try {
            InventarioEntity inventario = inventariorepository.findById(idProducto).orElse(null);
            if (inventario != null) {
                return inventario.getStockDisponible() >= cantidadRequerida;
            }
            return false;
        } catch (DataAccessException e) {
            return false; // Maneja errores de base de datos
        } catch (Exception e) {
            return false; // Maneja errores inesperados
        }
    }

    public List<Inventario> buscarPorRangoDeStock(int minStock, int maxStock) {
        try {
            // Validar que el rango sea válido
            if (minStock < 0 || maxStock < 0 || minStock > maxStock) {
                return Collections.emptyList();
            }

            List<InventarioEntity> entidades = inventariorepository.findByStockDisponibleBetween(minStock, maxStock);
            return entidades.stream()
                    .map(entidad -> new Inventario(
                            entidad.getIdProducto(),
                            entidad.getStockDisponible(),
                            entidad.getUbicacionBodega(),
                            entidad.getFechaUltimaActualizacion()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

}
