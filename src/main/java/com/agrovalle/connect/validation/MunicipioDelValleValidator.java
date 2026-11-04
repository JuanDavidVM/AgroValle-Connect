package com.agrovalle.connect.validation;

import com.agrovalle.connect.service.CatalogoService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Valida que un texto pertenezca al catálogo de municipios del Valle del Cauca.
 */
public class MunicipioDelValleValidator implements ConstraintValidator<MunicipioDelValle, String> {

    private final CatalogoService catalogoService;

    public MunicipioDelValleValidator(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @Override
    public boolean isValid(String valor, ConstraintValidatorContext context) {
        return valor == null || valor.isBlank() || catalogoService.buscarMunicipio(valor).isPresent();
    }
}
