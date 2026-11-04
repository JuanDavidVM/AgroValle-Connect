package com.agrovalle.connect.service;

import com.agrovalle.connect.model.Rol;
import com.agrovalle.connect.model.Usuario;

/**
 * Casos de uso sobre las cuentas de acceso.
 */
public interface UsuarioService {

    /**
     * Crea un usuario con la contraseña cifrada, rechazando correos duplicados.
     *
     * @param correo     correo del usuario
     * @param contrasena contraseña en texto plano
     * @param rol        rol asignado
     * @return el usuario persistido
     */
    Usuario crear(String correo, String contrasena, Rol rol);
}
