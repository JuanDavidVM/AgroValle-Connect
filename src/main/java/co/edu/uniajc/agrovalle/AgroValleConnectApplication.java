package co.edu.uniajc.agrovalle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicacion AgroValle Connect.
 *
 * <p>Plataforma web que conecta la oferta agricola de las fincas del Valle del Cauca
 * con la demanda comercial urbana, eliminando la intermediacion innecesaria.</p>
 */
@SpringBootApplication
public class AgroValleConnectApplication {

    /**
     * Arranca el contexto de Spring Boot.
     *
     * @param args argumentos de linea de comandos
     */
    public static void main(String[] args) {
        SpringApplication.run(AgroValleConnectApplication.class, args);
    }
}
