package co.edu.uniajc.agrovalle.dto;

/**
 * Contrato de respuesta del endpoint de verificacion de estado.
 *
 * @param status  estado reportado por la aplicacion
 * @param service nombre logico del servicio
 * @param version version del artefacto desplegado
 */
public record HealthResponse(String status, String service, String version) {
}
