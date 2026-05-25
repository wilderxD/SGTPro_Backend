package com.example.sgtpro.SGTPRO.service;

import com.example.sgtpro.SGTPRO.dto.UsuarioDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IUsuarioService {
    public List<UsuarioDTO> listarUsuarios();
    public Page<UsuarioDTO> listarUsuariosPaginado(Pageable pageable);
    public UsuarioDTO guardarUsuario(UsuarioDTO dto);
    public UsuarioDTO actualizarUsuario(Integer idUsuario, UsuarioDTO dtoEditado);
    public UsuarioDTO buscarPorId(Integer id);
    public void eliminarPorID(Integer id);
}
