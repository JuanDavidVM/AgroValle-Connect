package com.agrovalle.connect.service.impl;

import com.agrovalle.connect.exception.SolicitudInvalidaException;
import com.agrovalle.connect.model.Categoria;
import com.agrovalle.connect.model.Municipio;
import com.agrovalle.connect.repository.CategoriaRepository;
import com.agrovalle.connect.repository.MunicipioRepository;
import com.agrovalle.connect.service.CatalogoService;
import com.agrovalle.connect.util.TextoNormalizado;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class CatalogoServiceImpl implements CatalogoService {

    private final MunicipioRepository municipioRepository;
    private final CategoriaRepository categoriaRepository;

    public CatalogoServiceImpl(MunicipioRepository municipioRepository, CategoriaRepository categoriaRepository) {
        this.municipioRepository = municipioRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public Optional<Municipio> buscarMunicipio(String nombre) {
        if (nombre == null) {
            return Optional.empty();
        }
        String buscado = TextoNormalizado.de(nombre);
        return municipioRepository.findAll().stream()
                .filter(m -> TextoNormalizado.de(m.getNombre()).equals(buscado))
                .findFirst();
    }

    @Override
    public Municipio obtenerMunicipio(String nombre) {
        return buscarMunicipio(nombre).orElseThrow(
                () -> new SolicitudInvalidaException("El municipio no pertenece al Valle del Cauca: " + nombre));
    }

    @Override
    public Optional<Categoria> buscarCategoria(String nombre) {
        if (nombre == null) {
            return Optional.empty();
        }
        String buscada = TextoNormalizado.de(nombre);
        return categoriaRepository.findAll().stream()
                .filter(c -> TextoNormalizado.de(c.getNombre()).equals(buscada))
                .findFirst();
    }

    @Override
    public Categoria obtenerCategoria(String nombre) {
        return buscarCategoria(nombre).orElseThrow(
                () -> new SolicitudInvalidaException("La categoría no pertenece al catálogo: " + nombre));
    }
}
