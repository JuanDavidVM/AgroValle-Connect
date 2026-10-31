package com.agrovalle.connect.service;

import com.agrovalle.connect.dto.FincaRequest;
import com.agrovalle.connect.dto.FincaResponse;
import com.agrovalle.connect.exception.ConflictoException;
import com.agrovalle.connect.exception.RecursoNoEncontradoException;
import com.agrovalle.connect.model.Agricultor;
import com.agrovalle.connect.model.Finca;
import com.agrovalle.connect.model.Municipio;
import com.agrovalle.connect.repository.AgricultorRepository;
import com.agrovalle.connect.repository.FincaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FincaService {

    private final FincaRepository fincaRepository;
    private final AgricultorRepository agricultorRepository;

    public FincaService(FincaRepository fincaRepository, AgricultorRepository agricultorRepository) {
        this.fincaRepository = fincaRepository;
        this.agricultorRepository = agricultorRepository;
    }

    /**
     * Registra una finca asociada al agricultor autenticado.
     *
     * @param correoAgricultor correo del usuario autenticado (subject del JWT)
     * @param request          datos de la finca
     * @return la finca registrada
     */
    @Transactional
    public FincaResponse registrar(String correoAgricultor, FincaRequest request) {
        Agricultor agricultor = agricultorRepository.findByUsuarioCorreo(correoAgricultor)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un agricultor asociado al usuario autenticado"));

        String nombre = request.nombreFinca().trim();
        if (fincaRepository.existsByAgricultorIdAndNombreIgnoreCase(agricultor.getId(), nombre)) {
            throw new ConflictoException("Ya tiene una finca registrada con el nombre " + nombre);
        }

        Finca finca = new Finca();
        finca.setNombre(nombre);
        finca.setMunicipio(Municipio.desdeNombre(request.municipio()));
        finca.setHectareas(request.hectareas());
        finca.setAgricultor(agricultor);

        return FincaResponse.desde(fincaRepository.save(finca));
    }
}
