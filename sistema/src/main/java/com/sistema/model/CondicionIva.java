package com.sistema.model;

public enum CondicionIva {

    CONSUMIDOR_FINAL("Consumidor Final"),
    RESPONSABLE_INSCRIPTO("Responsable Inscripto");

    private final String descripcion;

    CondicionIva(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}

