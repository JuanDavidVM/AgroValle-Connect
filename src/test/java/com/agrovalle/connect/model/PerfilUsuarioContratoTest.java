package com.agrovalle.connect.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Verifica el principio de sustitución de Liskov: todos los subtipos de {@link PerfilUsuario}
 * cumplen el mismo contrato y se pueden usar donde se espera el supertipo.
 */
class PerfilUsuarioContratoTest {

    private static Agricultor agricultor() {
        Agricultor agricultor = new Agricultor();
        agricultor.setNombreCompleto("María Gómez");
        agricultor.setTelefono("3001234567");
        return agricultor;
    }

    private static CompradorComercial comprador() {
        CompradorComercial comprador = new CompradorComercial();
        comprador.setRazonSocial("Supermercados del Valle S.A.S.");
        comprador.setTelefono("3109876543");
        return comprador;
    }

    static Stream<PerfilUsuario> perfiles() {
        return Stream.of(agricultor(), comprador());
    }

    @ParameterizedTest
    @MethodSource("perfiles")
    void nombreVisible_nuncaEsVacio_enCualquierSubtipo(PerfilUsuario perfil) {
        assertFalse(perfil.getNombreVisible().isBlank());
    }

    @ParameterizedTest
    @MethodSource("perfiles")
    void usuarioYTelefono_seManejanDesdeElSupertipo(PerfilUsuario perfil) {
        Usuario usuario = new Usuario();
        usuario.setCorreo("titular@correo.com");

        perfil.setUsuario(usuario);

        assertEquals("titular@correo.com", perfil.getUsuario().getCorreo());
        assertFalse(perfil.getTelefono().isBlank());
    }

    @Test
    void nombreVisible_delAgricultor_esSuNombreCompleto() {
        assertEquals("María Gómez", agricultor().getNombreVisible());
    }

    @Test
    void nombreVisible_delComprador_esSuRazonSocial() {
        assertEquals("Supermercados del Valle S.A.S.", comprador().getNombreVisible());
    }

    @Test
    void ambosSubtipos_sustituyenAlSupertipoEnLaMismaOperacion() {
        List<PerfilUsuario> perfiles = List.of(agricultor(), comprador());

        List<String> nombres = perfiles.stream().map(PerfilUsuario::getNombreVisible).toList();

        assertEquals(List.of("María Gómez", "Supermercados del Valle S.A.S."), nombres);
    }
}
