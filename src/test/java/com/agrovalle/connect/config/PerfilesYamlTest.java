package com.agrovalle.connect.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * Red de seguridad de los perfiles YAML.
 *
 * <p>El 12 de noviembre entro a main un application-staging.yml con indentacion
 * invalida (AV-001) y con claves que la aplicacion no lee (AV-002). El pipeline
 * siguio en verde porque ninguna prueba toca ese perfil. Estas dos pruebas
 * cierran ese hueco sin necesidad de una base de datos real.
 */
class PerfilesYamlTest {

    private static final String PERFIL_STAGING = "application-staging.yml";

    private final YamlPropertySourceLoader cargador = new YamlPropertySourceLoader();

    private PropertySource<?> cargar(String nombre, Resource recurso) {
        try {
            List<PropertySource<?>> fuentes = cargador.load(nombre, recurso);
            assertFalse(fuentes.isEmpty(), "El archivo " + nombre + " no produjo ninguna propiedad.");
            return fuentes.get(0);
        } catch (IOException | RuntimeException e) {
            return fail("El archivo " + nombre + " no es un YAML valido: " + e.getMessage());
        }
    }

    private void exigeClave(PropertySource<?> fuente, String clave) {
        assertNotNull(fuente.getProperty(clave),
                "El perfil de staging no define '" + clave + "', que la aplicacion si lee.");
    }

    @Test
    void todosLosPerfilesYaml_seParsean() throws IOException {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] recursos = resolver.getResources("classpath*:application-*.yml");
        assertTrue(recursos.length > 0, "No se encontro ningun perfil YAML en el classpath.");
        for (Resource recurso : recursos) {
            assertNotNull(cargar(recurso.getFilename(), recurso));
        }
    }

    @Test
    void perfilStaging_defineLasClavesQueLaAplicacionLee() {
        PropertySource<?> staging = cargar(PERFIL_STAGING, new ClassPathResource(PERFIL_STAGING));
        exigeClave(staging, "spring.datasource.url");
        exigeClave(staging, "spring.datasource.username");
        exigeClave(staging, "spring.datasource.password");
        exigeClave(staging, "app.jwt.secret");
        exigeClave(staging, "app.jwt.expiration-ms");
        exigeClave(staging, "server.port");
    }
}
