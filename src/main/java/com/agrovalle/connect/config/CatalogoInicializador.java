package com.agrovalle.connect.config;

import com.agrovalle.connect.model.Categoria;
import com.agrovalle.connect.model.Municipio;
import com.agrovalle.connect.repository.CategoriaRepository;
import com.agrovalle.connect.repository.MunicipioRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Siembra los catálogos al arrancar la aplicación: los 42 municipios del Valle del Cauca y las categorías de
 * producto. Es idempotente: solo inserta los registros que todavía no existen.
 */
@Component
public class CatalogoInicializador implements ApplicationRunner {

    static final List<String> MUNICIPIOS = List.of(
            "Alcalá",
            "Andalucía",
            "Ansermanuevo",
            "Argelia",
            "Bolívar",
            "Buenaventura",
            "Buga",
            "Bugalagrande",
            "Caicedonia",
            "Cali",
            "Calima",
            "Candelaria",
            "Cartago",
            "Dagua",
            "El Águila",
            "El Cairo",
            "El Cerrito",
            "El Dovio",
            "Florida",
            "Ginebra",
            "Guacarí",
            "Jamundí",
            "La Cumbre",
            "La Unión",
            "La Victoria",
            "Obando",
            "Palmira",
            "Pradera",
            "Restrepo",
            "Riofrío",
            "Roldanillo",
            "San Pedro",
            "Sevilla",
            "Toro",
            "Trujillo",
            "Tuluá",
            "Ulloa",
            "Versalles",
            "Vijes",
            "Yotoco",
            "Yumbo",
            "Zarzal");

    static final List<String> CATEGORIAS = List.of(
            "Frutas", "Verduras", "Tubérculos", "Granos", "Café", "Hierbas");

    private final MunicipioRepository municipioRepository;
    private final CategoriaRepository categoriaRepository;

    public CatalogoInicializador(MunicipioRepository municipioRepository, CategoriaRepository categoriaRepository) {
        this.municipioRepository = municipioRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        Set<String> municipiosExistentes = municipioRepository.findAll().stream()
                .map(Municipio::getNombre).collect(Collectors.toSet());
        MUNICIPIOS.stream()
                .filter(nombre -> !municipiosExistentes.contains(nombre))
                .forEach(nombre -> {
                    Municipio municipio = new Municipio();
                    municipio.setNombre(nombre);
                    municipioRepository.save(municipio);
                });

        Set<String> categoriasExistentes = categoriaRepository.findAll().stream()
                .map(Categoria::getNombre).collect(Collectors.toSet());
        CATEGORIAS.stream()
                .filter(nombre -> !categoriasExistentes.contains(nombre))
                .forEach(nombre -> {
                    Categoria categoria = new Categoria();
                    categoria.setNombre(nombre);
                    categoriaRepository.save(categoria);
                });
    }
}
