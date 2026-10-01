package com.agrovalle.connect.service;

import com.agrovalle.connect.dto.CompradorComercialRequest;
import com.agrovalle.connect.dto.CompradorComercialResponse;
import com.agrovalle.connect.exception.ConflictoException;
import com.agrovalle.connect.model.CompradorComercial;
import com.agrovalle.connect.model.Rol;
import com.agrovalle.connect.model.Usuario;
import com.agrovalle.connect.repository.CompradorComercialRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompradorComercialService {

    private final CompradorComercialRepository compradorRepository;
    private final UsuarioService usuarioService;

    public CompradorComercialService(CompradorComercialRepository compradorRepository,
                                     UsuarioService usuarioService) {
        this.compradorRepository = compradorRepository;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public CompradorComercialResponse registrar(CompradorComercialRequest request) {
        if (compradorRepository.existsByNit(request.nit())) {
            throw new ConflictoException("Ya existe un comprador con el NIT " + request.nit());
        }
        Usuario usuario = usuarioService.crear(request.correo(), request.contrasena(), Rol.COMPRADOR);

        CompradorComercial comprador = new CompradorComercial();
        comprador.setRazonSocial(request.razonSocial().trim());
        comprador.setNit(request.nit());
        comprador.setTelefono(request.telefono());
        comprador.setUsuario(usuario);

        return CompradorComercialResponse.desde(compradorRepository.save(comprador));
    }
}
