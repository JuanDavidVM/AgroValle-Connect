package com.agrovalle.connect.validation;

import com.agrovalle.connect.model.Municipio;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Valida que un texto pertenezca al catálogo {@link Municipio}.
 */
public class MunicipioDelValleValidator implements ConstraintValidator<MunicipioDelValle, String> {

    @Override
    public boolean isValid(String valor, ConstraintValidatorContext context) {
        return valor == null || valor.isBlank() || Municipio.buscar(valor).isPresent();
    }
}
