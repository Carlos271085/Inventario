package com.inventario.inventario;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.inventario.inventario.model.Inventario;
import com.inventario.inventario.model.entity.InventarioEntity;
import com.inventario.inventario.repository.InventarioRepository;
import com.inventario.inventario.service.InventarioService;

public class InventarioTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @InjectMocks
    private InventarioService inventarioService;

    private Inventario inventario;
    private InventarioEntity inventarioEntity; // Corregido el nombre de la variable

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Crear el modelo Inventario
        inventario = new Inventario(1L, 100, "Viña del Mar", LocalDateTime.now());

        // Crear la entidad InventarioEntity
        inventarioEntity = new InventarioEntity(); // Corregido el nombre de la variable
        inventarioEntity.setIdInventario(1L);
        inventarioEntity.setStockDisponible(100);
        inventarioEntity.setUbicacionBodega("Viña del Mar");
        inventarioEntity.setFechaUltimaActualizacion(LocalDateTime.now());
    }

    @Test
    public void testCrearInventario_nuevo() {
        // Configurar el comportamiento del mock
        when(inventarioRepository.existsById(1L)).thenReturn(false); // Corregido el método existsById
        when(inventarioRepository.save(any(InventarioEntity.class))).thenReturn(inventarioEntity);

        // Ejecutar el método a probar
        String result = inventarioService.crearInventario(inventario);

        // Verificar el resultado
        assertEquals("Inventario creado exitosamente", result);
    }

    @Test
    public void testCrearInventaro_existe() {
        when(inventarioRepository.existsById(inventario.getIdInventario())).thenReturn(true);

        String result = inventarioService.crearInventario(inventario);
        assertEquals("El inventario ya existe", result);
    }

    @Test
    public void testBorrarInventario() {
        // Configurar el mock para simular que el inventario existe
        when(inventarioRepository.existsById(1L)).thenReturn(true);
        
        // Configurar el mock para devolver el inventario antes de eliminarlo
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventarioEntity));
        
        // Configurar el comportamiento del método deleteById
        doNothing().when(inventarioRepository).deleteById(1L);
        
        // Ejecutar el método a probar
        String result = inventarioService.eliminarInventario(1L);
        
        // Verificar el resultado
        assertEquals("Inventario eliminado correctamente", result);
    }

    @Test
    public void testBorrarInventarioNoExistente() {
        // Configurar el mock para simular que el inventario no existe
        when(inventarioRepository.existsById(99L)).thenReturn(false);
        
        // Ejecutar el método a probar
        String result = inventarioService.eliminarInventario(99L);
        
        // Verificar el resultado
        assertEquals("Inventario no encontrado", result);
    }
}