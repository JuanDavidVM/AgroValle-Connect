package com.agrovalle.connect.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.agrovalle.connect.dto.AgricultorRequest;
import com.agrovalle.connect.dto.AgricultorResponse;
import com.agrovalle.connect.exception.ConflictoException;
import com.agrovalle.connect.model.Agricultor;
import com.agrovalle.connect.model.Rol;
import com.agrovalle.connect.model.Usuario;
import com.agrovalle.connect.repository.AgricultorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AgricultorServiceTest {

    @Mock
    private AgricultorRepository agricultorRepository;
    @Mock
    private UsuarioService usuarioService;
    @InjectMocks
    private AgricultorService agricultorService;

    private AgricultorRequest request() {
        return new AgricultorRequest("María Gómez", "1144123456", "maria@correo.com",
                "3001234567", "Palmira", "Clave12345");
    }

    @Test
    void registrar_datosValidos_persisteYDevuelveRespuesta() {
        Usuario usuario = new Usuario();
        usuario.setCorreo("maria@correo.com");
        when(agricultorRepository.existsByDocumento("1144123456")).thenReturn(false);
        when(usuarioService.crear("maria@correo.com", "Clave12345", Rol.AGRICULTOR)).thenReturn(usuario);
        when(agricultorRepository.save(any(Agricultor.class))).thenAnswer(inv -> inv.getArgument(0));

        AgricultorResponse respuesta = agricultorService.registrar(request());

        assertEquals("1144123456", respuesta.documento());
        assertEquals("maria@correo.com", respuesta.correo());
        assertEquals("Palmira", respuesta.municipio());
        verify(agricultorRepository).save(any(Agricultor.class));
    }

    @Test
    void registrar_documentoDuplicado_lanzaConflicto() {
        when(agricultorRepository.existsByDocumento("1144123456")).thenReturn(true);

        assertThrows(ConflictoException.class, () -> agricultorService.registrar(request()));

        verify(usuarioService, never()).crear(any(), any(), any());
        verify(agricultorRepository, never()).save(any());
    }

    @Test
    void registrar_correoDuplicado_lanzaConflictoYNoGuarda() {
        when(agricultorRepository.existsByDocumento("1144123456")).thenReturn(false);
        when(usuarioService.crear(eq("maria@correo.com"), any(), eq(Rol.AGRICULTOR)))
                .thenThrow(new ConflictoException("El correo ya está registrado"));

        assertThrows(ConflictoException.class, () -> agricultorService.registrar(request()));

        verify(agricultorRepository, never()).save(any());
    }
}
