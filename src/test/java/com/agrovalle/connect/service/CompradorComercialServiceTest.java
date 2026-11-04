package com.agrovalle.connect.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.agrovalle.connect.dto.CompradorComercialRequest;
import com.agrovalle.connect.dto.CompradorComercialResponse;
import com.agrovalle.connect.exception.ConflictoException;
import com.agrovalle.connect.model.CompradorComercial;
import com.agrovalle.connect.model.Municipio;
import com.agrovalle.connect.model.Rol;
import com.agrovalle.connect.model.Usuario;
import com.agrovalle.connect.repository.CompradorComercialRepository;
import com.agrovalle.connect.service.impl.CompradorComercialServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CompradorComercialServiceTest {

    @Mock
    private CompradorComercialRepository compradorRepository;
    @Mock
    private UsuarioService usuarioService;
    @Mock
    private CatalogoService catalogoService;
    @InjectMocks
    private CompradorComercialServiceImpl compradorService;

    private CompradorComercialRequest request() {
        return new CompradorComercialRequest("Supermercados del Valle S.A.S.", "900123456-7",
                "compras@supervalle.com", "3109876543", "Calle 10 # 5-20", "Cali", "Clave12345");
    }

    @Test
    void registrar_datosValidos_persisteYDevuelveRespuesta() {
        Usuario usuario = new Usuario();
        usuario.setCorreo("compras@supervalle.com");
        Municipio cali = new Municipio();
        cali.setNombre("Cali");
        when(catalogoService.obtenerMunicipio("Cali")).thenReturn(cali);
        when(compradorRepository.existsByNit("900123456-7")).thenReturn(false);
        when(usuarioService.crear("compras@supervalle.com", "Clave12345", Rol.COMPRADOR)).thenReturn(usuario);
        when(compradorRepository.save(any(CompradorComercial.class))).thenAnswer(inv -> inv.getArgument(0));

        CompradorComercialResponse respuesta = compradorService.registrar(request());

        assertEquals("900123456-7", respuesta.nit());
        assertEquals("compras@supervalle.com", respuesta.correo());
        assertEquals("Calle 10 # 5-20", respuesta.direccionComercial());
        assertEquals("Cali", respuesta.municipio());
        verify(compradorRepository).save(any(CompradorComercial.class));
    }

    @Test
    void registrar_nitDuplicado_lanzaConflicto() {
        when(compradorRepository.existsByNit("900123456-7")).thenReturn(true);

        assertThrows(ConflictoException.class, () -> compradorService.registrar(request()));

        verify(usuarioService, never()).crear(any(), any(), any());
        verify(compradorRepository, never()).save(any());
    }

    @Test
    void registrar_correoDuplicado_lanzaConflictoYNoGuarda() {
        when(compradorRepository.existsByNit("900123456-7")).thenReturn(false);
        when(usuarioService.crear(eq("compras@supervalle.com"), any(), eq(Rol.COMPRADOR)))
                .thenThrow(new ConflictoException("El correo ya está registrado"));

        assertThrows(ConflictoException.class, () -> compradorService.registrar(request()));

        verify(compradorRepository, never()).save(any());
    }
}
