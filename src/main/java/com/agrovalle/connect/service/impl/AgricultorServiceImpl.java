package com.agrovalle.connect.service.impl;

import com.agrovalle.connect.dto.AgricultorRequest;
import com.agrovalle.connect.dto.AgricultorResponse;
import com.agrovalle.connect.exception.ConflictoException;
import com.agrovalle.connect.exception.RecursoNoEncontradoException;
import com.agrovalle.connect.model.Agricultor;
import com.agrovalle.connect.model.Municipio;
import com.agrovalle.connect.model.Rol;
import com.agrovalle.connect.model.Usuario;
import com.agrovalle.connect.repository.AgricultorRepository;
import com.agrovalle.connect.service.AgricultorService;
import com.agrovalle.connect.service.CatalogoService;
import com.agrovalle.connect.service.UsuarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AgricultorServiceImpl implements AgricultorService {

    private final AgricultorRepository agricultorRepository;
    private final UsuarioService usuarioService;
    private final CatalogoService catalogoService;

    /**
     * Crea el servicio con sus dependencias, todas inyectadas como abstracciones.
     *
     * @param agricultorRepository repositorio de agricultores
     * @param usuarioService       caso de uso para crear la cuenta de acceso
     * @param catalogoService      consulta de municipios del Valle
     */
    public AgricultorServiceImpl(AgricultorRepository agricultorRepository, UsuarioService usuarioService,
                                 CatalogoService catalogoService) {
        this.agricultorRepository = agricultorRepository;
        this.usuarioService = usuarioService;
        this.catalogoService = catalogoService;
    }

    @Override
    @Transactional
    public AgricultorResponse registrar(AgricultorRequest request) {
        if (agricultorRepository.existsByDocumento(request.documento())) {
            throw new ConflictoException("Ya existe un agricultor con el documento " + request.documento());
        }
        Municipio municipio = catalogoService.obtenerMunicipio(request.municipio());
        Usuario usuario = usuarioService.crear(request.correo(), request.contrasena(), Rol.AGRICULTOR);

        Agricultor agricultor = new Agricultor();
        agricultor.setNombreCompleto(request.nombreCompleto().trim());
        agricultor.setDocumento(request.documento());
        agricultor.setTelefono(request.telefono());
        agricultor.setMunicipio(municipio);
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
