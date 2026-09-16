package co.edu.uniajc.agrovalle.controller;

import co.edu.uniajc.agrovalle.dto.HealthResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador de verificacion de estado del sistema.
 *
 * <p>Unico endpoint habilitado durante el Sprint 0. Su proposito es comprobar que la
 * estructura Maven, el contexto de Spring Boot y la suite de pruebas funcionan de
 * extremo a extremo antes de iniciar el desarrollo funcional (HU-01).</p>
 */
@RestController
@RequestMapping("/api/v1")
public class HealthController {

    private final String applicationVersion;

    /**
     * Crea el controlador inyectando la version declarada en la configuracion.
     *
     * @param applicationVersion version del artefacto
     */
    public HealthController(@Value("${agrovalle.version:0.1.0-SNAPSHOT}") String applicationVersion) {
        this.applicationVersion = applicationVersion;
    }

    /**
     * Reporta el estado operativo de la aplicacion.
     *
     * @return respuesta 200 OK con el estado del servicio
     */
    @GetMapping("/health")
    public ResponseEntity<HealthResponse> health() {
        return ResponseEntity.ok(new HealthResponse("UP", "agrovalle-connect", applicationVersion));
    }
}
