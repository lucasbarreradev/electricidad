package com.sistema.dto;

import com.sistema.model.FormaPago;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class VentaForm {
    private Long clienteId; // opcional
    private FormaPago formaPago;
    private String nota;

    private List<VentaItemForm> items = new ArrayList<>();
}
