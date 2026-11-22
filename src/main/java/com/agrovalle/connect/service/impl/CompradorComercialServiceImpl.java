package com.agrovalle.connect.service.impl;

import com.agrovalle.connect.dto.CompradorComercialRequest;
import com.agrovalle.connect.dto.CompradorComercialResponse;
import com.agrovalle.connect.exception.ConflictoException;
import com.agrovalle.connect.model.CompradorComercial;
import com.agrovalle.connect.model.Municipio;
import com.agrovalle.connect.model.Rol;
import com.agrovalle.connect.model.Usuario;
import com.agrovalle.connect.repository.CompradorComercialRepository;
import com.agrovalle.connect.service.CatalogoService;
import com.agrovalle.connect.service.CompradorComercialService;
import com.agrovalle.connect.service.UsuarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompradorComercialServiceImpl implements CompradorComercialService {

    private final CompradorComercialRepository compradorRepository;
    private final UsuarioService usuarioService;
    private final CatalogoService catalogoService;

    /**
     * Crea el servicio con sus dependencias, todas inyectadas como abstracciones.
     *
     * @param compradorRepository repositorio de compradores
     * @param usuarioService      caso de uso para crear la cuenta de acceso
     * @param catalogoService     consulta de municipios del Valle
     */
    public CompradorComercialServiceImpl(CompradorComercialRepository compradorRepository,
                                         UsuarioService usuarioService, CatalogoService catalogoService) {
        this.compradorRepository = compradorRepository;
        this.usuarioService = usuarioService;
        this.catalogoService = catalogoService;
    }

    @Override
    @Transactional
    public CompradorComercialResponse registrar(CompradorComercialRequest request) {
        if (compradorRepository.existsByNit(request.nit())) {
            throw new ConflictoException("Ya existe un comprador con el NIT " + request.nit());
        }
        Municipio municipio = catalogoService.obtenerMunicipio(request.municipio());
        Usuario usuario = usuarioService.crear(request.correo(), request.contrasena(), Rol.COMPRADOR);

        CompradorComercial comprador = new CompradorComercial();
        comprador.setRazonSocial(request.razonSocial().trim());
        comprador.setNit(request.nit());
        comprador.setTelefono(request.telefono());
        comprador.setDireccionComercial(request.direccionComercial().trim());
        comprador.setMunicipio(municipio);
        comprador.setUsuario(usuario);

        return CompradorComercialResponse.desde(compradorRepository.save(comprador));
    }
}
