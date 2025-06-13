package com.inventario.inventario;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
//import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventario.inventario.controller.InventarioController;
import com.inventario.inventario.model.Inventario;
import com.inventario.inventario.service.InventarioService;

@WebMvcTest(InventarioController.class)
public class InventarioControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private InventarioService inventarioService;

        @Autowired
        private ObjectMapper objectMapper;

        private Inventario inventario;

        @BeforeEach
        void setup() {
                inventario = new Inventario();
                inventario.setIdInventario(1L);
                inventario.setStockDisponible(100);
                inventario.setUbicacionBodega("BODEGA-A");
                inventario.setFechaUltimaActualizacion(LocalDateTime.now());
        }

        @Test
        void testCrearInventarioNuevo() throws Exception {
                // Configurar el mock para simular un inventario nuevo
                when(inventarioService.crearInventario(any(Inventario.class)))
                                .thenReturn("Inventario creado exitosamente");

                mockMvc.perform(post("/api/v1/inventarios")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(inventario)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.mensaje").value("Inventario creado exitosamente"));
        }

        @Test
        void testCrearInventarioExistente() throws Exception {
                // Configurar el mock para simular un inventario que ya existe
                when(inventarioService.crearInventario(any(Inventario.class)))
                                .thenReturn("El inventario ya existe");

                mockMvc.perform(post("/api/v1/inventarios")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(inventario)))
                                .andExpect(status().isConflict())
                                .andExpect(jsonPath("$.error").value(
                                                "No se puede crear el inventario porque ya existe uno registrado con el mismo ID en el sistema"));
        }

        @Test
        void testObtenerTodosLosInventarios() throws Exception {
                // Crear lista de inventarios de prueba
                List<Inventario> inventarios = Arrays.asList(
                                inventario,
                                new Inventario(2L, 200, "BODEGA-B", LocalDateTime.now()));

                // Configurar el mock del servicio
                when(inventarioService.listarInventarios()).thenReturn(inventarios);

                // Ejecutar la prueba
                mockMvc.perform(get("/api/v1/inventarios")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].idInventario").value(1))
                                .andExpect(jsonPath("$[0].ubicacionBodega").value("BODEGA-A"))
                                .andExpect(jsonPath("$[1].idInventario").value(2))
                                .andExpect(jsonPath("$[1].ubicacionBodega").value("BODEGA-B"));
        }

        @Test
        void testObtenerInventariosPorUbicacion() throws Exception {
                // Crear lista de inventarios para una ubicación específica
                List<Inventario> inventariosPorUbicacion = Arrays.asList(inventario);

                // Configurar el mock del servicio
                when(inventarioService.obtenerInventarioPorUbicacion("BODEGA-A"))
                                .thenReturn(inventariosPorUbicacion);

                // Ejecutar la prueba
                mockMvc.perform(get("/api/v1/inventarios/ubicacion/BODEGA-A")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].idInventario").value(1))
                                .andExpect(jsonPath("$[0].ubicacionBodega").value("BODEGA-A"));
        }

        @Test
        void testObtenerInventariosPorUbicacionNoEncontrada() throws Exception {
                // Configurar el mock para retornar lista vacía
                when(inventarioService.obtenerInventarioPorUbicacion("BODEGA-X"))
                                .thenReturn(Collections.emptyList());

                // Ejecutar la prueba
                mockMvc.perform(get("/api/v1/inventarios/ubicacion/BODEGA-X")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.mensaje")
                                                .value("No se encontraron inventarios en la ubicación: BODEGA-X"));
        }

        @Test
        void testEliminarInventarioPorId() throws Exception {
                // Configurar mock para eliminación exitosa
                when(inventarioService.eliminarInventario(1L))
                                .thenReturn("Inventario eliminado correctamente");

                // Probar eliminación exitosa
                mockMvc.perform(delete("/api/v1/inventarios/1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().string("Inventario eliminado correctamente"));

                // Configurar mock para inventario no encontrado
                when(inventarioService.eliminarInventario(99L))
                                .thenReturn("Inventario no encontrado");

                // Probar eliminación de inventario no existente
                mockMvc.perform(delete("/api/v1/inventarios/99")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound())
                                .andExpect(content().string("Inventario no encontrado"));
        }
}