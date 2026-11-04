package com.agrovalle.connect.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Exige que el texto corresponda a una categoría del catálogo de productos.
 *
 * <p>Los valores nulos o vacíos se consideran válidos: de ellos se encarga {@code @NotBlank}.</p>
 */
@Documented
@Constraint(validatedBy = CategoriaValidaValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE,
        ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CategoriaValida {

    /**
     * Mensaje que se devuelve cuando la categoría no pertenece al catálogo.
     *
     * @return el mensaje de error
     */
    String message() default "La categoría no pertenece al catálogo de productos";

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
