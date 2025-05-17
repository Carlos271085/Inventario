package com.inventario.inventario.model.entity;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

//@EntityScan
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
    private Date fechaUltimaActualizacion;
}   
