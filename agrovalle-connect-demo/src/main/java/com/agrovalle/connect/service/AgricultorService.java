package com.agrovalle.connect.service;

import com.agrovalle.connect.dto.AgricultorRequest;
import com.agrovalle.connect.dto.AgricultorResponse;
import com.agrovalle.connect.exception.ConflictoException;
import com.agrovalle.connect.model.Agricultor;
import com.agrovalle.connect.model.Rol;
import com.agrovalle.connect.model.Usuario;
import com.agrovalle.connect.repository.AgricultorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AgricultorService {

    private final AgricultorRepository agricultorRepository;
    private final UsuarioService usuarioService;

    public AgricultorService(AgricultorRepository agricultorRepository, UsuarioService usuarioService) {
        this.agricultorRepository = agricultorRepository;
        this.usuarioService = usuarioService;
    }

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
}
