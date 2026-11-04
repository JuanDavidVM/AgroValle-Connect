package com.agrovalle.connect.service.impl;

import com.agrovalle.connect.dto.AgricultorRequest;
import com.agrovalle.connect.dto.AgricultorResponse;
import com.agrovalle.connect.exception.ConflictoException;
import com.agrovalle.connect.exception.RecursoNoEncontradoException;
import com.agrovalle.connect.model.Agricultor;
import com.agrovalle.connect.model.Rol;
import com.agrovalle.connect.model.Usuario;
import com.agrovalle.connect.repository.AgricultorRepository;
import com.agrovalle.connect.service.AgricultorService;
import com.agrovalle.connect.service.UsuarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AgricultorServiceImpl implements AgricultorService {

    private final AgricultorRepository agricultorRepository;
    private final UsuarioService usuarioService;

    public AgricultorServiceImpl(AgricultorRepository agricultorRepository, UsuarioService usuarioService) {
        this.agricultorRepository = agricultorRepository;
        this.usuarioService = usuarioService;
    }

    @Override
    @Transactional
    public AgricultorResponse registrar(AgricultorRequest request) {
        if (agricultorRepository.existsByDocumento(request.documento())) {
            throw new ConflictoException("Ya existe un agricultor con el documento " + request.documento());
        }
        Usuario usuario = usuarioService.crear(request.correo(), request.contrasena(), Rol.AGRICULTOR);

        Agricultor agricultor = new Agricultor();
        agricultor.setNombreCompleto(request.nombreCompleto().trim());
        agricultor.setDocumento(request.documento());
        agricultor.setTelefono(request.telefono());
        agricultor.setMunicipio(request.municipio().trim());
        agricultor.setUsuario(usuario);

        return AgricultorResponse.desde(agricultorRepository.save(agricultor));
    }

    @Override
    public Agricultor obtenerPorCorreo(String correo) {
        return agricultorRepository.findByUsuarioCorreo(correo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un agricultor asociado al usuario autenticado"));
    }
}
