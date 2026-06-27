package com.example.sgtpro.SGTPRO.mapper;

import com.example.sgtpro.SGTPRO.dto.RolDTO;
import com.example.sgtpro.SGTPRO.dto.UsuarioDTO;
import com.example.sgtpro.SGTPRO.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {
    
    public Usuario toEntity(UsuarioDTO dto){
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(dto.getIdUsuario());
        usuario.setNombreCompleto(dto.getNombreCompleto());
        usuario.setCorreo(dto.getCorreo());
        return usuario;
    }
    
    public UsuarioDTO toDTO(Usuario usuario){
        RolDTO rolDTO = usuario.getRol() != null
                ? new RolDTO(usuario.getRol().getIdRol(), usuario.getRol().getNombre(), usuario.getRol().getDescripcion())
                : null;
        return UsuarioDTO.builder()
                .idUsuario(usuario.getIdUsuario())
                .rol(rolDTO)
                .nombreCompleto(usuario.getNombreCompleto())
                .correo(usuario.getCorreo())
                .build();
    }
    
}
