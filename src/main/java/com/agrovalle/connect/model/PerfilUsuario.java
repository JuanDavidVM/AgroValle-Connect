package com.agrovalle.connect.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;

/**
 * Perfil de negocio asociado a una cuenta de acceso ({@link Usuario}).
 *
 * <p>Es el supertipo común de {@link Agricultor} y {@link CompradorComercial}. Cumple el principio de
 * sustitución de Liskov: cualquier código que reciba un {@code PerfilUsuario} funciona igual con
 * cualquiera de sus subtipos.</p>
 *
 * <p>Contrato que deben respetar los subtipos:</p>
 * <ul>
 *   <li>{@link #getNombreVisible()} nunca devuelve un texto vacío en un perfil completo.</li>
 *   <li>Los subtipos no agregan precondiciones a las operaciones heredadas.</li>
 * </ul>
 *
 * <p>Al ser una {@code @MappedSuperclass}, los campos comunes se guardan en la tabla de cada subtipo y
 * no se crea ninguna tabla adicional.</p>
 */
@MappedSuperclass
public abstract class PerfilUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String telefono;

    @OneToOne(optional = false)
    @JoinColumn(name = "id_usuario", nullable = false, unique = true)
    private Usuario usuario;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @PrePersist
    void antesDePersistir() {
        this.fechaRegistro = LocalDateTime.now();
    }

    /**
     * Nombre con el que se identifica al titular del perfil ante otros usuarios.
     *
     * @return el nombre visible, nunca vacío en un perfil completo
     */
    public abstract String getNombreVisible();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }
}
