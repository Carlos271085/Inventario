package com.inventario.inventario.model;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

//import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Inventario {

    private long idInventario; // Se declara atributo de int a long para manejar valores más grandes

    @PositiveOrZero(message = "El stock no puede ser negativo")
    private int stockDisponible;

    @NotBlank(message = "La ubicación de bodega es obligatoria")
    private String ubicacionBodega;

    private LocalDateTime fechaUltimaActualizacion;

}
