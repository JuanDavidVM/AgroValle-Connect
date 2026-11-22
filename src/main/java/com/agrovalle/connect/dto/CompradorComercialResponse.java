package com.agrovalle.connect.dto;

import com.agrovalle.connect.model.CompradorComercial;
import java.time.LocalDateTime;

public record CompradorComercialResponse(
        Long id,
        String razonSocial,
        String nit,
        String correo,
        String telefono,
        String direccionComercial,
        String municipio,
        LocalDateTime fechaRegistro) {

    /**
     * Construye la respuesta a partir del comprador persistido.
     *
     * @param c comprador registrado
     * @return la respuesta lista para serializar
     */
    public static CompradorComercialResponse desde(CompradorComercial c) {
        return new CompradorComercialResponse(c.getId(), c.getRazonSocial(), c.getNit(),
                c.getUsuario().getCorreo(), c.getTelefono(), c.getDireccionComercial(),
                c.getMunicipio().getNombre(), c.getFechaRegistro());
    }
}
