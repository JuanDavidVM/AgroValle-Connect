package com.agrovalle.connect.service.impl;

import com.agrovalle.connect.dto.FincaRequest;
import com.agrovalle.connect.dto.FincaResponse;
import com.agrovalle.connect.exception.ConflictoException;
import com.agrovalle.connect.model.Agricultor;
import com.agrovalle.connect.model.Finca;
import com.agrovalle.connect.repository.FincaRepository;
import com.agrovalle.connect.service.AgricultorService;
import com.agrovalle.connect.service.CatalogoService;
import com.agrovalle.connect.service.FincaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FincaServiceImpl implements FincaService {

    private final FincaRepository fincaRepository;
    private final AgricultorService agricultorService;
    private final CatalogoService catalogoService;

    /**
     * Crea el servicio con sus dependencias, todas inyectadas como abstracciones.
     *
     * @param fincaRepository   repositorio de fincas
     * @param agricultorService caso de uso para resolver al agricultor autenticado
     * @param catalogoService   consulta de municipios del Valle
     */
    public FincaServiceImpl(FincaRepository fincaRepository, AgricultorService agricultorService,
                            CatalogoService catalogoService) {
        this.fincaRepository = fincaRepository;
        this.agricultorService = agricultorService;
        this.catalogoService = catalogoService;
    }

    @Override
    @Transactional
    public FincaResponse registrar(String correoAgricultor, FincaRequest request) {
        Agricultor agricultor = agricultorService.obtenerPorCorreo(correoAgricultor);

        String nombre = request.nombreFinca().trim();
        if (fincaRepository.existsByAgricultorIdAndNombreIgnoreCase(agricultor.getId(), nombre)) {
            throw new ConflictoException("Ya tiene una finca registrada con el nombre " + nombre);
        }

        Finca finca = new Finca();
        finca.setNombre(nombre);
        finca.setMunicipio(catalogoService.obtenerMunicipio(request.municipio()));
        finca.setHectareas(request.hectareas());
        finca.setAgricultor(agricultor);

        return FincaResponse.desde(fincaRepository.save(finca));
    }
}
