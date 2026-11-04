package com.agrovalle.connect.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Exige que el texto corresponda a un municipio del catálogo del Valle del Cauca.
 *
 * <p>Los valores nulos o vacíos se consideran válidos: de ellos se encarga {@code @NotBlank}.</p>
 */
@Documented
@Constraint(validatedBy = MunicipioDelValleValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE,
        ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MunicipioDelValle {

    /**
     * Mensaje que se devuelve cuando el municipio no pertenece al catálogo.
     *
     * @return el mensaje de error
     */
    String message() default "El municipio no pertenece al Valle del Cauca";

    /**
     * Grupos de validación (requerido por Bean Validation).
     *
     * @return los grupos
     */
    Class<?>[] groups() default {};

    /**
     * Carga útil asociada a la restricción (requerido por Bean Validation).
     *
     * @return la carga útil
     */
    Class<? extends Payload>[] payload() default {};
}
