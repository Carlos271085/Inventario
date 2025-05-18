package com.inventario.inventario.model.entity;

import java.time.LocalDateTime;

import org.springframework.boot.autoconfigure.domain.EntityScan;

import jakarta.persistence.*;
import lombok.Data;

@EntityScan
@Entity
@Table(name = "inventario")
@Data
public class InventarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idProducto;

    @Column(name = "stockDisponible")
    private int stockDisponible;

    @Column(name = "ubicacionBodega")
    private String ubicacionBodega;

    @Column(name = "fechaUltimaActualizacion")
    // @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime fechaUltimaActualizacion;
}
