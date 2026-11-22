package com.agrovalle.connect.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "compradores_comerciales")
@AttributeOverride(name = "id", column = @Column(name = "id_comprador"))
public class CompradorComercial extends PerfilUsuario {

    @Column(name = "razon_social", nullable = false, length = 150)
    private String razonSocial;

    @Column(nullable = false, unique = true, length = 13)
    private String nit;

    @Column(name = "direccion_comercial", nullable = false, length = 200)
    private String direccionComercial;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_municipio", nullable = false)
    private Municipio municipio;

    @Override
    public String getNombreVisible() {
        return razonSocial;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getDireccionComercial() {
        return direccionComercial;
    }

    public void setDireccionComercial(String direccionComercial) {
        this.direccionComercial = direccionComercial;
    }

    public Municipio getMunicipio() {
        return municipio;
    }

    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
    }
}
