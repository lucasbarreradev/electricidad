package com.sistema.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class VentaItemForm {
    private Long productoId;
    private Integer cantidad;
    private Double precioUnitario;
    private Double descuentoPct;
}
