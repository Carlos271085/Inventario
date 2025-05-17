package com.inventario.inventario.model;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Inventario {

    private long idProducto; //Se declara atributo de int a long para manejar valores más grandes
    private int stockDisponible;
    private String ubicacionBodega;
    private Date fechaUltimaActualizacion;



}
