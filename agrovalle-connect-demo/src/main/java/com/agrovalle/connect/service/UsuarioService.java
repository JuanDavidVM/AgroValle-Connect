package com.agrovalle.connect.service;

import com.agrovalle.connect.exception.ConflictoException;
import com.agrovalle.connect.model.Rol;
import com.agrovalle.connect.model.Usuario;
import com.agrovalle.connect.repository.UsuarioRepository;
import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario crear(String correo, String contrasena, Rol rol) {
        String correoNormalizado = correo.trim().toLowerCase(Locale.ROOT);
        if (usuarioRepository.existsByCorreo(correoNormalizado)) {
            throw new ConflictoException("El correo " + correoNormalizado + " ya está registrado");
        }
        Usuario usuario = new Usuario();
        usuario.setCorreo(correoNormalizado);
        usuario.setPasswordHash(passwordEncoder.encode(contrasena));
        usuario.setRol(rol);
        return usuarioRepository.save(usuario);
    }
}
