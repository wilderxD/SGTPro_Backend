package com.example.sgtpro.SGTPRO.mapper;

import com.example.sgtpro.SGTPRO.dto.UsuarioDTO;
import com.example.sgtpro.SGTPRO.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {
    
    public Usuario toEntity(UsuarioDTO dto){
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(dto.getIdUsuario());
        usuario.setRol(dto.getRol());
        usuario.setNombreCompleto(dto.getNombreCompleto());
        usuario.setCorreo(dto.getCorreo());
        usuario.setPassword(dto.getPassword());
        return usuario;
    }
    
    public UsuarioDTO toDTO(Usuario usuario){
        return UsuarioDTO.builder()
                .idUsuario(usuario.getIdUsuario())
                .rol(usuario.getRol())
                .nombreCompleto(usuario.getNombreCompleto())
                .correo(usuario.getCorreo())
                .password(usuario.getPassword())
                .build();
    }
    
}
