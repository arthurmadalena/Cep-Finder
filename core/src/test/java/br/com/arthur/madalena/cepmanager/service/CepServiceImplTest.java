package br.com.arthur.madalena.cepmanager.service;

import br.com.arthur.madalena.cepmanager.dao.CepDAO;
import br.com.arthur.madalena.cepmanager.dto.CepDTO;
import br.com.arthur.madalena.cepmanager.entity.Cep;
import br.com.arthur.madalena.cepmanager.exception.BusinessException;
import br.com.arthur.madalena.cepmanager.exception.ResourceNotFoundException;
import br.com.arthur.madalena.cepmanager.mapper.CepMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CepServiceImplTest {

    @Mock
    private CepDAO cepDAO;

    @Mock
    private CepMapper cepMapper;

    @InjectMocks
    private CepServiceImpl cepService;

    private Cep cep;
    private CepDTO cepDTO;

    @BeforeEach
    void setUp() {
        cep = new Cep();
        cep.setId(1L);
        cep.setCodigo("01310100");
        cep.setLogradouro("Avenida Paulista");
        cep.setBairro("Bela Vista");
        cep.setCidade("São Paulo");
        cep.setUf("SP");

        cepDTO = new CepDTO();
        cepDTO.setId(1L);
        cepDTO.setCodigo("01310100");
        cepDTO.setLogradouro("Avenida Paulista");
        cepDTO.setBairro("Bela Vista");
        cepDTO.setCidade("São Paulo");
        cepDTO.setUf("SP");
    }

    @Test
    void testFindByCodigo_Success() {
        when(cepDAO.findByCodigo("01310100")).thenReturn(Optional.of(cep));
        when(cepMapper.toDTO(cep)).thenReturn(cepDTO);

        CepDTO result = cepService.findByCodigo("01310100");

        assertNotNull(result);
        assertEquals("01310100", result.getCodigo());
        verify(cepDAO).findByCodigo("01310100");
        verify(cepMapper).toDTO(cep);
    }

    @Test
    void testFindByCodigo_NotFound() {
        when(cepDAO.findByCodigo(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            cepService.findByCodigo("00000000");
        });
    }

    @Test
    void testFindByLogradouro() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Cep> page = new PageImpl<>(Arrays.asList(cep));
        
        when(cepDAO.findByLogradouroContaining("Paulista", pageable)).thenReturn(page);
        when(cepMapper.toDTO(any(Cep.class))).thenReturn(cepDTO);

        Page<CepDTO> result = cepService.findByLogradouro("Paulista", pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(cepDAO).findByLogradouroContaining("Paulista", pageable);
    }

    @Test
    void testFindByCidade() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Cep> page = new PageImpl<>(Arrays.asList(cep));
        
        when(cepDAO.findByCidade("São Paulo", pageable)).thenReturn(page);
        when(cepMapper.toDTO(any(Cep.class))).thenReturn(cepDTO);

        Page<CepDTO> result = cepService.findByCidade("São Paulo", pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(cepDAO).findByCidade("São Paulo", pageable);
    }

    @Test
    void testCreate_Success() {
        when(cepDAO.existsByCodigo("01310100")).thenReturn(false);
        when(cepMapper.toEntity(cepDTO)).thenReturn(cep);
        when(cepDAO.save(cep)).thenReturn(cep);
        when(cepMapper.toDTO(cep)).thenReturn(cepDTO);

        CepDTO result = cepService.create(cepDTO);

        assertNotNull(result);
        assertEquals("01310100", result.getCodigo());
        verify(cepDAO).existsByCodigo("01310100");
        verify(cepDAO).save(cep);
    }

    @Test
    void testCreate_AlreadyExists() {
        when(cepDAO.existsByCodigo("01310100")).thenReturn(true);

        assertThrows(BusinessException.class, () -> {
            cepService.create(cepDTO);
        });

        verify(cepDAO, never()).save(any());
    }

    @Test
    void testUpdate_Success() {
        when(cepDAO.findByCodigo("01310100")).thenReturn(Optional.of(cep));
        when(cepDAO.save(cep)).thenReturn(cep);
        when(cepMapper.toDTO(cep)).thenReturn(cepDTO);

        CepDTO result = cepService.update("01310100", cepDTO);

        assertNotNull(result);
        verify(cepMapper).updateEntity(cepDTO, cep);
        verify(cepDAO).save(cep);
    }

    @Test
    void testUpdate_NotFound() {
        when(cepDAO.findByCodigo(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            cepService.update("00000000", cepDTO);
        });
    }

    @Test
    void testDelete_Success() {
        when(cepDAO.findByCodigo("01310100")).thenReturn(Optional.of(cep));

        cepService.delete("01310100");

        verify(cepDAO).delete(cep);
    }

    @Test
    void testDelete_NotFound() {
        when(cepDAO.findByCodigo(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            cepService.delete("00000000");
        });
    }

    @Test
    void testExistsByCodigo() {
        when(cepDAO.existsByCodigo("01310100")).thenReturn(true);

        boolean result = cepService.existsByCodigo("01310100");

        assertTrue(result);
        verify(cepDAO).existsByCodigo("01310100");
    }
}

