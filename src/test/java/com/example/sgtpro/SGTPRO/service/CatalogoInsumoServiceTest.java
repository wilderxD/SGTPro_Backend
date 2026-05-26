package com.example.sgtpro.SGTPRO.service;

import com.example.sgtpro.SGTPRO.dto.CatalogoInsumoDTO;
import com.example.sgtpro.SGTPRO.entity.CatalogoInsumo;
import com.example.sgtpro.SGTPRO.exception.BadRequestException;
import com.example.sgtpro.SGTPRO.exception.ResourceNotFoundException;
import com.example.sgtpro.SGTPRO.mapper.CatalogoInsumoMapper;
import com.example.sgtpro.SGTPRO.repository.CatalogoInsumoRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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

@ExtendWith(MockitoExtension.class)
public class CatalogoInsumoServiceTest {
    @Mock
    private CatalogoInsumoRepository insumoRepository;

    @Mock
    private CatalogoInsumoMapper insumoMapper;

    @InjectMocks
    private CatalogoInsumoService catalogoInsumoService;

    private CatalogoInsumo insumoPrueba;
    private CatalogoInsumoDTO insumoDTOPrueba;

    @BeforeEach
    void setUp() {
        
        insumoPrueba = new CatalogoInsumo();
        insumoPrueba.setIdInsumo(1);
        insumoPrueba.setCodigoInterno("FIL-001");
        insumoPrueba.setNombre("Filtro de Aceite");
        insumoPrueba.setUnidadMedida("Unidad");
        insumoPrueba.setCostoUnitario(new BigDecimal("45.50"));

        insumoDTOPrueba = CatalogoInsumoDTO.builder()
                .idInsumo(1)
                .codigoInterno("FIL-001")
                .nombre("Filtro de Aceite")
                .unidadMedida("Unidad")
                .costoUnitario(new BigDecimal("45.50"))
                .build();
    }

    @Test
    void listarCatalogoDeInsumos_DebeRetornarListaDTO() {
        when(insumoRepository.findAll()).thenReturn(List.of(insumoPrueba));
        when(insumoMapper.toDTO(any(CatalogoInsumo.class))).thenReturn(insumoDTOPrueba);

        List<CatalogoInsumoDTO> resultado = catalogoInsumoService.listarCatalogoDeInsumos();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(insumoRepository, times(1)).findAll();
    }

    @Test
    void listarCatalogoInsumosPaginado_DebeRetornarPaginaDTO() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CatalogoInsumo> paginaInsumos = new PageImpl<>(List.of(insumoPrueba));

        when(insumoRepository.findAll(pageable)).thenReturn(paginaInsumos);
        when(insumoMapper.toDTO(any(CatalogoInsumo.class))).thenReturn(insumoDTOPrueba);

        Page<CatalogoInsumoDTO> resultado = catalogoInsumoService.listarCatalogoInsumosPaginado(pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        verify(insumoRepository, times(1)).findAll(pageable);
    }

    @Test
    void crearInsumo_DebeRetornarInsumoDTO_CuandoCodigoNoExiste() {
        when(insumoRepository.findByCodigoInterno("FIL-001")).thenReturn(Optional.empty());
        when(insumoMapper.toEntity(any(CatalogoInsumoDTO.class))).thenReturn(insumoPrueba);
        when(insumoRepository.save(any(CatalogoInsumo.class))).thenReturn(insumoPrueba);
        when(insumoMapper.toDTO(any(CatalogoInsumo.class))).thenReturn(insumoDTOPrueba);

        CatalogoInsumoDTO resultado = catalogoInsumoService.crearInsumo(insumoDTOPrueba);

        assertNotNull(resultado);
        assertEquals("FIL-001", resultado.getCodigoInterno());
        verify(insumoRepository, times(1)).save(any(CatalogoInsumo.class));
    }

    @Test
    void crearInsumo_DebeLanzarBadRequest_CuandoCodigoYaExiste() {
        when(insumoRepository.findByCodigoInterno("FIL-001")).thenReturn(Optional.of(insumoPrueba));

        BadRequestException excepcion = assertThrows(BadRequestException.class, () -> {
            catalogoInsumoService.crearInsumo(insumoDTOPrueba);
        });

        assertEquals("El Insumo de codigo: FIL-001 ya se encuentra registrado en la base de datos.", excepcion.getMessage());
        verify(insumoRepository, never()).save(any(CatalogoInsumo.class));
    }

    @Test
    void buscarPorId_DebeRetornarInsumoDTO_CuandoExiste() {
        when(insumoRepository.findById(1)).thenReturn(Optional.of(insumoPrueba));
        when(insumoMapper.toDTO(insumoPrueba)).thenReturn(insumoDTOPrueba);

        CatalogoInsumoDTO resultado = catalogoInsumoService.buscarPorId(1);

        assertNotNull(resultado);
        assertEquals("Filtro de Aceite", resultado.getNombre());
    }

    @Test
    void buscarPorId_DebeLanzarResourceNotFound_CuandoNoExiste() {
        when(insumoRepository.findById(99)).thenReturn(Optional.empty());

        ResourceNotFoundException excepcion = assertThrows(ResourceNotFoundException.class, () -> {
            catalogoInsumoService.buscarPorId(99);
        });

        assertEquals("Usuario no encontrado con id: 99", excepcion.getMessage());
    }

    @Test
    void actualizarInsumo_DebeModificarYRetornar_CuandoExiste() {
        
        CatalogoInsumoDTO datosEditados = CatalogoInsumoDTO.builder()
                .costoUnitario(new BigDecimal("50.00")) // Simulamos una subida de precio
                .build();

        CatalogoInsumoDTO dtoFinalEsperado = CatalogoInsumoDTO.builder()
                .idInsumo(1)
                .codigoInterno("FIL-001")
                .nombre("Filtro de Aceite")
                .unidadMedida("Unidad")
                .costoUnitario(new BigDecimal("50.00"))
                .build();

        when(insumoRepository.findById(1)).thenReturn(Optional.of(insumoPrueba));

        when(insumoMapper.toDTO(any(CatalogoInsumo.class)))
                .thenReturn(insumoDTOPrueba)
                .thenReturn(dtoFinalEsperado);

        when(insumoMapper.toEntity(any(CatalogoInsumoDTO.class))).thenReturn(insumoPrueba);
        when(insumoRepository.save(any(CatalogoInsumo.class))).thenReturn(insumoPrueba);

        CatalogoInsumoDTO resultado = catalogoInsumoService.actualizarInsumo(1, datosEditados);

        assertNotNull(resultado);
        assertTrue(resultado.getCostoUnitario().compareTo(new BigDecimal("50.00")) == 0, "El costo debió actualizarse");
        verify(insumoRepository, times(1)).save(any(CatalogoInsumo.class));
    }

    @Test
    void eliminarInsumo_DebeLlamarAlRepositorio_CuandoExiste() {
        when(insumoRepository.existsById(1)).thenReturn(true);
        doNothing().when(insumoRepository).deleteById(1);

        catalogoInsumoService.eliminarInsumo(1);

        verify(insumoRepository, times(1)).deleteById(1);
    }

    @Test
    void eliminarInsumo_DebeLanzarRuntimeException_CuandoNoExiste() {
        when(insumoRepository.existsById(99)).thenReturn(false);

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            catalogoInsumoService.eliminarInsumo(99);
        });

        assertEquals("No se puede eliminar: el insumo con id 99", excepcion.getMessage());
        verify(insumoRepository, never()).deleteById(anyInt());
    }
}
