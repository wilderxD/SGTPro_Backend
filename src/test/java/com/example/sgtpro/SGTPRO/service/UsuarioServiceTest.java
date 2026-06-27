package com.example.sgtpro.SGTPRO.service;

import com.example.sgtpro.SGTPRO.dto.RolDTO;
import com.example.sgtpro.SGTPRO.dto.UsuarioDTO;
import com.example.sgtpro.SGTPRO.entity.Rol;
import com.example.sgtpro.SGTPRO.entity.Usuario;
import com.example.sgtpro.SGTPRO.exception.BadRequestException;
import com.example.sgtpro.SGTPRO.exception.ResourceNotFoundException;
import com.example.sgtpro.SGTPRO.mapper.UsuarioMapper;
import com.example.sgtpro.SGTPRO.repository.RolRepository;
import com.example.sgtpro.SGTPRO.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {
    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private PasswordEncoder passwordEncoder; // Simulamos la encriptación BCrypt

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioPrueba;
    private UsuarioDTO usuarioDTOPrueba;
    private Rol rolPrueba;

    @BeforeEach
    void setUp() {
        
        rolPrueba = new Rol();
        rolPrueba.setIdRol(2);
        rolPrueba.setNombre("ROLE_LOGISTICA");

        usuarioPrueba = new Usuario();
        usuarioPrueba.setIdUsuario(1);
        usuarioPrueba.setCorreo("logistica@forli.com.pe");
        usuarioPrueba.setNombreCompleto("Juan Perez");
        usuarioPrueba.setPassword("hash_simulado");
        usuarioPrueba.setRol(rolPrueba);

        RolDTO rolDTO = new RolDTO(rolPrueba.getIdRol(), rolPrueba.getNombre(), rolPrueba.getDescripcion());
        usuarioDTOPrueba = UsuarioDTO.builder()
                .idUsuario(1)
                .correo("logistica@forli.com.pe")
                .nombreCompleto("Juan Perez")
                .password("123456") 
                .rol(rolDTO)
                .build();
    }

    @Test
    void listarUsuarios_DebeRetornarListaDTO() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuarioPrueba));
        when(usuarioMapper.toDTO(any(Usuario.class))).thenReturn(usuarioDTOPrueba);

        List<UsuarioDTO> resultado = usuarioService.listarUsuarios();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void listarUsuariosPaginado_DebeRetornarPaginaDTO() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Usuario> paginaUsuarios = new PageImpl<>(List.of(usuarioPrueba));

        when(usuarioRepository.findAll(pageable)).thenReturn(paginaUsuarios);
        when(usuarioMapper.toDTO(any(Usuario.class))).thenReturn(usuarioDTOPrueba);

        Page<UsuarioDTO> resultado = usuarioService.listarUsuariosPaginado(pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        verify(usuarioRepository, times(1)).findAll(pageable);
    }

    @Test
    void guardarUsuario_DebeRetornarUsuarioDTO_CuandoDatosSonValidos() {
        
        when(usuarioRepository.findByCorreo(usuarioDTOPrueba.getCorreo())).thenReturn(Optional.empty());
        when(rolRepository.findById(2)).thenReturn(Optional.of(rolPrueba));
        
        when(passwordEncoder.encode("123456")).thenReturn("hash_simulado");
        
        when(usuarioMapper.toEntity(any(UsuarioDTO.class))).thenReturn(usuarioPrueba);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioPrueba);
        when(usuarioMapper.toDTO(usuarioPrueba)).thenReturn(usuarioDTOPrueba);

        UsuarioDTO resultado = usuarioService.guardarUsuario(usuarioDTOPrueba);

        assertNotNull(resultado);
        assertEquals("logistica@forli.com.pe", resultado.getCorreo());
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void guardarUsuario_DebeLanzarBadRequest_CuandoCorreoYaExiste() {
       
        when(usuarioRepository.findByCorreo("logistica@forli.com.pe")).thenReturn(Optional.of(usuarioPrueba));

        BadRequestException excepcion = assertThrows(BadRequestException.class, () -> {
            usuarioService.guardarUsuario(usuarioDTOPrueba);
        });

        assertEquals("El correo: logistica@forli.com.pe ya se encuentra registrado en la base de datos pruebe con otro correo.", excepcion.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void guardarUsuario_DebeLanzarBadRequest_CuandoRolNoExiste() {
        
        when(usuarioRepository.findByCorreo(anyString())).thenReturn(Optional.empty());
        when(rolRepository.findById(2)).thenReturn(Optional.empty());

        BadRequestException excepcion = assertThrows(BadRequestException.class, () -> {
            usuarioService.guardarUsuario(usuarioDTOPrueba);
        });

        assertEquals("El rol especificado no existe.", excepcion.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void buscarPorId_DebeRetornarUsuarioDTO_CuandoExiste() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuarioPrueba));
        when(usuarioMapper.toDTO(usuarioPrueba)).thenReturn(usuarioDTOPrueba);

        UsuarioDTO resultado = usuarioService.buscarPorId(1);

        assertNotNull(resultado);
        assertEquals("Juan Perez", resultado.getNombreCompleto());
    }

    @Test
    void buscarPorId_DebeLanzarResourceNotFound_CuandoNoExiste() {
        when(usuarioRepository.findById(99)).thenReturn(Optional.empty());

        ResourceNotFoundException excepcion = assertThrows(ResourceNotFoundException.class, () -> {
            usuarioService.buscarPorId(99);
        });

        assertEquals("Usuario no encontrado con id: 99", excepcion.getMessage());
    }

    @Test
    void actualizarUsuario_DebeActualizarYRetornar_CuandoExiste() {
        UsuarioDTO datosEditados = UsuarioDTO.builder()
                .nombreCompleto("Juan Perez Editado")
                .build();
                
        UsuarioDTO dtoFinal = UsuarioDTO.builder()
                .idUsuario(1)
                .nombreCompleto("Juan Perez Editado")
                .correo("logistica@forli.com.pe")
                .build();

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuarioPrueba));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioPrueba);
        when(usuarioMapper.toDTO(any(Usuario.class))).thenReturn(dtoFinal);

        UsuarioDTO resultado = usuarioService.actualizarUsuario(1, datosEditados);

        assertNotNull(resultado);
        assertEquals("Juan Perez Editado", resultado.getNombreCompleto());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void eliminarPorID_DebeLlamarAlRepositorio_CuandoExiste() {
        when(usuarioRepository.existsById(1)).thenReturn(true);
        doNothing().when(usuarioRepository).deleteById(1);

        usuarioService.eliminarPorID(1);

        verify(usuarioRepository, times(1)).deleteById(1);
    }

    @Test
    void eliminarPorID_DebeLanzarResourceNotFound_CuandoNoExiste() {
        when(usuarioRepository.existsById(99)).thenReturn(false);

        ResourceNotFoundException excepcion = assertThrows(ResourceNotFoundException.class, () -> {
            usuarioService.eliminarPorID(99);
        });

        assertEquals("No se puede eliminar: El usuario con id 99 no existe en la base de datos.", excepcion.getMessage());
        verify(usuarioRepository, never()).deleteById(anyInt());
    }
}
