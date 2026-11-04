package com.agrovalle.connect.dto;

import com.agrovalle.connect.validation.MunicipioDelValle;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CompradorComercialRequest(
        @NotBlank(message = "La razón social es obligatoria")
        @Size(max = 150, message = "La razón social no puede superar 150 caracteres")
        String razonSocial,

        @NotBlank(message = "El NIT es obligatorio")
        @Pattern(regexp = "\\d{9,10}(-\\d)?",
                message = "El NIT debe tener 9 o 10 dígitos, "
                        + "con dígito de verificación opcional (ej. 900123456-7)")
        String nit,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato válido")
        @Size(max = 150, message = "El correo no puede superar 150 caracteres")
        String correo,

        @NotBlank(message = "El teléfono es obligatorio")
        @Pattern(regexp = "\\+?\\d{7,15}", message = "El teléfono debe tener entre 7 y 15 dígitos")
        String telefono,

        @NotBlank(message = "La dirección comercial es obligatoria")
        @Size(max = 200, message = "La dirección comercial no puede superar 200 caracteres")
        String direccionComercial,

        @NotBlank(message = "El municipio es obligatorio")
        @MunicipioDelValle
        String municipio,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
        String contrasena) {
}
