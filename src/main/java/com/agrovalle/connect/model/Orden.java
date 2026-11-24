package com.agrovalle.connect.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Orden de compra directa entre un comprador y un agricultor.
 *
 * <p>Un carrito con ofertas de varios agricultores produce una orden por cada uno, de modo que cada
 * agricultor recibe y gestiona únicamente lo suyo.</p>
 */
@Entity
@Table(name = "ordenes")
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_orden")
    private Long id;

    @Column(name = "numero_orden", nullable = false, unique = true, length = 30)
    private String numeroOrden;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoOrden estado;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_alistamiento")
    private LocalDateTime fechaAlistamiento;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_comprador", nullable = false)
    private CompradorComercial comprador;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_agricultor", nullable = false)
    private Agricultor agricultor;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleOrden> detalles = new ArrayList<>();

    @PrePersist
    void antesDePersistir() {
        this.fechaCreacion = LocalDateTime.now();
    }

    /**
     * Agrega un renglón a la orden y mantiene la relación en los dos sentidos.
     *
     * @param detalle renglón a incorporar
     */
    public void agregarDetalle(DetalleOrden detalle) {
        detalle.setOrden(this);
        this.detalles.add(detalle);
    }

    /**
     * Suma el valor de todos los renglones de la orden.
     *
     * @return el total en pesos, con dos decimales
     */
    public BigDecimal getTotal() {
        return detalles.stream()
                .map(DetalleOrden::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroOrden() {
        return numeroOrden;
    }

    public void setNumeroOrden(String numeroOrden) {
        this.numeroOrden = numeroOrden;
    }

    public EstadoOrden getEstado() {
        return estado;
    }

    public void setEstado(EstadoOrden estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaAlistamiento() {
        return fechaAlistamiento;
    }

    public void setFechaAlistamiento(LocalDateTime fechaAlistamiento) {
        this.fechaAlistamiento = fechaAlistamiento;
    }

    public CompradorComercial getComprador() {
        return comprador;
    }

    public void setComprador(CompradorComercial comprador) {
        this.comprador = comprador;
    }

    public Agricultor getAgricultor() {
        return agricultor;
    }

    public void setAgricultor(Agricultor agricultor) {
        this.agricultor = agricultor;
    }

    public List<DetalleOrden> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleOrden> detalles) {
        this.detalles = detalles;
    }
}
