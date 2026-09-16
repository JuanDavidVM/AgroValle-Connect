package co.edu.uniajc.agrovalle;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class AgroValleConnectApplicationTests {

    @Test
    @DisplayName("El contexto de Spring Boot carga correctamente")
    void contextLoads() {
        // La ausencia de excepciones al levantar el contexto valida el cableado del proyecto.
    }
}
