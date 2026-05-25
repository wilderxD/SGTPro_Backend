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
                
        List<UsuarioDTO> listaDTO = usuarioRepository.findAll().stream()
                .map(usuarioMapper::toDTO)
                .collect(Collectors.toList());
        
        return listaDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioDTO> listarUsuariosPaginado(Pageable pageable) {
        
        Page<Usuario> listaUsuarios = usuarioRepository.findAll(pageable);
        
        return listaUsuarios.map(usuarioMapper::toDTO);
    }

    @Override
    @Transactional
    public UsuarioDTO guardarUsuario(UsuarioDTO dto) {    
        if(usuarioRepository.findByCorreo(dto.getCorreo()).isPresent()){
            throw new BadRequestException("El correo: " + dto.getCorreo() + " ya se encuentra registrado en la base de datos pruebe con otro correo.");
        }
        
        Rol rol = rolRepository.findById(dto.getRol().getIdRol()).orElseThrow(() -> new BadRequestException("El rol especificado no existe."));
        
        UsuarioDTO usuarioDTO = UsuarioDTO.builder()
                .rol(rol)
                .correo(dto.getCorreo())
                .nombreCompleto(dto.getNombreCompleto())
                .password(passwordEncoder.encode(dto.getPassword()))
                .build();                
                
        Usuario usuario = usuarioMapper.toEntity(usuarioDTO);
        
        return usuarioMapper.toDTO(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional
    public UsuarioDTO actualizarUsuario(Integer idUsuario, UsuarioDTO dtoEditado) {
        UsuarioDTO usuarioDTO = buscarPorId(idUsuario);
        
        UsuarioDTO usuarioActualizado = UsuarioDTO.builder()
                .idUsuario(idUsuario)
                .rol(dtoEditado.getRol() != null ? dtoEditado.getRol() : usuarioDTO.getRol())
                .nombreCompleto(dtoEditado.getNombreCompleto() != null ? dtoEditado.getNombreCompleto() : usuarioDTO.getNombreCompleto())
                .correo(dtoEditado.getCorreo() != null ? dtoEditado.getCorreo() : usuarioDTO.getCorreo())
                .password(usuarioDTO.getPassword())
                .build();
        
        return usuarioMapper.toDTO(usuarioRepository.save(usuarioMapper.toEntity(usuarioActualizado)));
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO buscarPorId(Integer id) {
        return usuarioMapper.toDTO(usuarioRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Usuario no encoantrado con id: " + id)));
    }

    @Override
    public void eliminarPorID(Integer id) {
        if(!usuarioRepository.existsById(id)){
            throw new RuntimeException("No se puede eliminar: El usuario con id " + id + " no existe en la base de datos.");
        }
        usuarioRepository.deleteById(id);
    }
    
}
