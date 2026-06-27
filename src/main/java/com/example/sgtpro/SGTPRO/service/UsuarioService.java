package com.example.sgtpro.SGTPRO.service;

import com.example.sgtpro.SGTPRO.dto.UsuarioDTO;
import com.example.sgtpro.SGTPRO.entity.Rol;
import com.example.sgtpro.SGTPRO.entity.Usuario;
import com.example.sgtpro.SGTPRO.exception.BadRequestException;
import com.example.sgtpro.SGTPRO.exception.ResourceNotFoundException;
import com.example.sgtpro.SGTPRO.mapper.UsuarioMapper;
import com.example.sgtpro.SGTPRO.repository.RolRepository;
import com.example.sgtpro.SGTPRO.repository.UsuarioRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService implements IUsuarioService{
    
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    
    
    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper, RolRepository rolRepository, PasswordEncoder passwordEncoder){
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarUsuarios() {
                
        List<UsuarioDTO> listaDTO = usuarioRepository.findAllActivos().stream()
                .map(usuarioMapper::toDTO)
                .collect(Collectors.toList());
        
        return listaDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioDTO> listarUsuariosPaginado(String search, Pageable pageable) {
        
        Page<Usuario> listaUsuarios = usuarioRepository.findFiltered(search, pageable);
        
        return listaUsuarios.map(usuarioMapper::toDTO);
    }

    @Override
    @Transactional
    public UsuarioDTO guardarUsuario(UsuarioDTO dto) {    
        if(usuarioRepository.findByCorreo(dto.getCorreo()).isPresent()){
            throw new BadRequestException("El correo: " + dto.getCorreo() + " ya se encuentra registrado en la base de datos pruebe con otro correo.");
        }
        
        Rol rol = rolRepository.findById(dto.getRol().getIdRol()).orElseThrow(() -> new BadRequestException("El rol especificado no existe."));
        
        Usuario usuario = usuarioMapper.toEntity(dto);
        usuario.setRol(rol);
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        
        return usuarioMapper.toDTO(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional
    public UsuarioDTO actualizarUsuario(Integer idUsuario, UsuarioDTO dtoEditado) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + idUsuario));
        
        if (dtoEditado.getRol() != null) {
            Rol rol = rolRepository.findById(dtoEditado.getRol().getIdRol())
                    .orElseThrow(() -> new BadRequestException("El rol especificado no existe."));
            usuario.setRol(rol);
        }
        if (dtoEditado.getNombreCompleto() != null) {
            usuario.setNombreCompleto(dtoEditado.getNombreCompleto());
        }
        if (dtoEditado.getCorreo() != null) {
            if (!dtoEditado.getCorreo().equals(usuario.getCorreo())
                    && usuarioRepository.findByCorreo(dtoEditado.getCorreo()).isPresent()) {
                throw new BadRequestException("El correo " + dtoEditado.getCorreo() + " ya está registrado por otro usuario.");
            }
            usuario.setCorreo(dtoEditado.getCorreo());
        }
        
        return usuarioMapper.toDTO(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO buscarPorId(Integer id) {
        return usuarioMapper.toDTO(usuarioRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id)));
    }

    @Override
    @Transactional
    public void eliminarPorID(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede eliminar: El usuario con id " + id + " no existe en la base de datos."));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarUsuariosPorRol(String nombreRol) {
        return usuarioRepository.findByRolNombre(nombreRol).stream()
                .map(usuarioMapper::toDTO)
                .toList();
    }
    
}
