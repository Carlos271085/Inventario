package com.inventario.inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventario.inventario.model.entity.InventarioEntity;

import java.util.List;
//import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<InventarioEntity, Long> {

    // Verificar existencia por ID del producto
    Boolean existsByUbicacionBodega(String ubicacionBodega);

    // Eliminar por Stock
    void deleteByStockDisponible(int stockDisponible);

    // Buscar por Stock Exacto
    InventarioEntity findBystockDisponible(int stockDisponible);

    // Verificar existencia por Stock
    Boolean existsByStockDisponible(int stockDisponible);

    // Buscar por ID del Producto
    List<InventarioEntity> findByIdInventario(long idInventario);

    // Buscar por ubicación de bodega (contiene palabra clave)
    List<InventarioEntity> findByUbicacionBodega(String ubicacionBodega);

}
