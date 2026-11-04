package com.agrovalle.connect.validation;

import com.agrovalle.connect.service.CatalogoService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Valida que un texto pertenezca al catálogo de categorías de producto.
 */
public class CategoriaValidaValidator implements ConstraintValidator<CategoriaValida, String> {

    private final CatalogoService catalogoService;

    public CategoriaValidaValidator(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @Override
    public boolean isValid(String valor, ConstraintValidatorContext context) {
        return valor == null || valor.isBlank() || catalogoService.buscarCategoria(valor).isPresent();
    }
}
