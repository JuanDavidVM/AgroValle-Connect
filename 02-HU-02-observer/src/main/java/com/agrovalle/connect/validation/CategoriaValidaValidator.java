package com.agrovalle.connect.validation;

import com.agrovalle.connect.model.Categoria;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Valida que un texto pertenezca al catálogo {@link Categoria}.
 */
public class CategoriaValidaValidator implements ConstraintValidator<CategoriaValida, String> {

    @Override
    public boolean isValid(String valor, ConstraintValidatorContext context) {
        return valor == null || valor.isBlank() || Categoria.buscar(valor).isPresent();
    }
}
