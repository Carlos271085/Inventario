package com.inventario.inventario.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.inventario.inventario.model.Inventario;
import com.inventario.inventario.model.entity.InventarioEntity;
import com.inventario.inventario.repository.InventarioRepository;

import jakarta.transaction.Transactional;

@Service
public class InventarioService {

    @Autowired
    private InventarioRepository inventariorepository;

    @Transactional
    public String crearInventario(Inventario inv) {
        if (inventariorepository.existsById(inv.getIdProducto())) {
            return "El inventario ya existe";
        }
        InventarioEntity inventarioNuevo = mapToEntity(inv);
        inventariorepository.save(inventarioNuevo);
        return "Inventario creado exitosamente";
    }

    public List<Inventario> obtenerInventarioPorUbicacion(String ubicacionBodega) {
        try {
            List<InventarioEntity> inventarios = inventariorepository.findByUbicacionBodega(ubicacionBodega);
            return inventarios.stream()
                    .map(this::mapToModel)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    // Método devuelve una lista de todos los inventarios almacenados.
    public List<Inventario> listarInventarios() {
        try {
            List<InventarioEntity> entidades = inventariorepository.findAll();
            return entidades.stream()
                    .map(entidad -> new Inventario(
                            entidad.getIdProducto(),
                            entidad.getStockDisponible(),
                            entidad.getUbicacionBodega(),
                            entidad.getFechaUltimaActualizacion()))
                    .collect(Collectors.toList());
        } catch (DataAccessException e) {
            return Collections.emptyList(); // Manejar erros de base de datos
        } catch (Exception e) {
            return Collections.emptyList(); // Manejar errores inesperados
        }
    }

    public List<Inventario> obtenerTodosLosInventario() {
        return inventariorepository.findAll().stream()
                .map(this::mapToModel)
                .collect(Collectors.toList());
    }

    // Método Actualizar Solo el Stock disponible de un producto, aunque se
    // modifiquen los otros valores el sistema no realizara los otros cambios.
    @Transactional
    public String actualizarInventario(long idProducto, Inventario inv) {
        try {
            InventarioEntity existente = inventariorepository.findById(idProducto).orElse(null);
            if (existente != null) {
                // Solo actualizar el stock disponible
                existente.setStockDisponible(inv.getStockDisponible());
                // Actualizar la fecha automáticamente
                existente.setFechaUltimaActualizacion(LocalDateTime.now());
                inventariorepository.save(existente);
                return "Stock actualizado correctamente";
            }
            return "Inventario no encontrado";
        } catch (IllegalArgumentException e) {
            return "Error: Stock no válido";
        } catch (Exception e) {
            return "Error al actualizar el stock: " + e.getMessage();
        }
    }

    // Método permite eliminar un inventario por su idProducto.
    @Transactional
    public String eliminarInventario(long idProducto) {
        InventarioEntity existente = inventariorepository.findById(idProducto).orElse(null);
        if (existente != null) {
            inventariorepository.delete(existente);
            return "Inventario eliminado correctamente";
        }
        return "Inventario no encontrado";
    }

    // Métodos auxiliares para mapear entre Inventario e InventarioEntity
    private InventarioEntity mapToEntity(Inventario inv) {
        InventarioEntity entity = new InventarioEntity();
        entity.setIdProducto(inv.getIdProducto());
        entity.setStockDisponible(inv.getStockDisponible());
        entity.setUbicacionBodega(inv.getUbicacionBodega());
        entity.setFechaUltimaActualizacion(inv.getFechaUltimaActualizacion());
        return entity;
    }

    private Inventario mapToModel(InventarioEntity entity) {
        return new Inventario(
                entity.getIdProducto(),
                entity.getStockDisponible(),
                entity.getUbicacionBodega(),
                entity.getFechaUltimaActualizacion()

        );
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
            return false;
        } catch (Exception e) {
            return false;
        }
    }

}
