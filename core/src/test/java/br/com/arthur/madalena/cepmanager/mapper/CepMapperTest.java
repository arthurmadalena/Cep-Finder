package br.com.arthur.madalena.cepmanager.mapper;

import br.com.arthur.madalena.cepmanager.dto.CepDTO;
import br.com.arthur.madalena.cepmanager.entity.Cep;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CepMapperTest {

    private CepMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CepMapper();
    }

    @Test
    void deveConverterEntityParaDTO() {
        Cep entity = new Cep();
        entity.setId(1L);
        entity.setCodigo("01310100");
        entity.setLogradouro("Avenida Paulista");
        entity.setComplemento("lado ímpar");
        entity.setBairro("Bela Vista");
        entity.setCidade("São Paulo");
        entity.setUf("SP");
        entity.setIbge("3550308");
        entity.setDatHoraCadastro(LocalDateTime.now());

        CepDTO dto = mapper.toDTO(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getCodigo()).isEqualTo("01310100");
        assertThat(dto.getLogradouro()).isEqualTo("Avenida Paulista");
        assertThat(dto.getComplemento()).isEqualTo("lado ímpar");
        assertThat(dto.getBairro()).isEqualTo("Bela Vista");
        assertThat(dto.getCidade()).isEqualTo("São Paulo");
        assertThat(dto.getUf()).isEqualTo("SP");
        assertThat(dto.getIbge()).isEqualTo("3550308");
    }

    @Test
    void deveRetornarNullQuandoEntityForNull() {
        CepDTO dto = mapper.toDTO(null);

        assertThat(dto).isNull();
    }

    @Test
    void deveConverterDTOParaEntity() {
        CepDTO dto = new CepDTO();
        dto.setId(1L);
        dto.setCodigo("01310100");
        dto.setLogradouro("Avenida Paulista");
        dto.setComplemento("lado ímpar");
        dto.setBairro("Bela Vista");
        dto.setCidade("São Paulo");
        dto.setUf("SP");
        dto.setIbge("3550308");

        Cep entity = mapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getCodigo()).isEqualTo("01310100");
        assertThat(entity.getLogradouro()).isEqualTo("Avenida Paulista");
        assertThat(entity.getComplemento()).isEqualTo("lado ímpar");
        assertThat(entity.getBairro()).isEqualTo("Bela Vista");
        assertThat(entity.getCidade()).isEqualTo("São Paulo");
        assertThat(entity.getUf()).isEqualTo("SP");
        assertThat(entity.getIbge()).isEqualTo("3550308");
    }

    @Test
    void deveRetornarNullQuandoDTOForNull() {
        Cep entity = mapper.toEntity(null);

        assertThat(entity).isNull();
    }

    @Test
    void deveConverterComplementoVazioParaNull() {
        CepDTO dto = new CepDTO();
        dto.setCodigo("01310100");
        dto.setLogradouro("Avenida Paulista");
        dto.setComplemento("");
        dto.setBairro("Bela Vista");
        dto.setCidade("São Paulo");
        dto.setUf("SP");
        dto.setIbge("3550308");

        Cep entity = mapper.toEntity(dto);

        assertThat(entity.getComplemento()).isNull();
    }

    @Test
    void deveConverterComplementoComEspacosParaNull() {
        CepDTO dto = new CepDTO();
        dto.setCodigo("01310100");
        dto.setLogradouro("Avenida Paulista");
        dto.setComplemento("   ");
        dto.setBairro("Bela Vista");
        dto.setCidade("São Paulo");
        dto.setUf("SP");
        dto.setIbge("3550308");

        Cep entity = mapper.toEntity(dto);

        assertThat(entity.getComplemento()).isNull();
    }

    @Test
    void deveConverterIbgeVazioParaNull() {
        CepDTO dto = new CepDTO();
        dto.setCodigo("01310100");
        dto.setLogradouro("Avenida Paulista");
        dto.setBairro("Bela Vista");
        dto.setCidade("São Paulo");
        dto.setUf("SP");
        dto.setIbge("");

        Cep entity = mapper.toEntity(dto);

        assertThat(entity.getIbge()).isNull();
    }

    @Test
    void deveRemoverEspacosEmBrancoDosValores() {
        CepDTO dto = new CepDTO();
        dto.setCodigo("01310100");
        dto.setLogradouro("Avenida Paulista");
        dto.setComplemento("  lado ímpar  ");
        dto.setBairro("Bela Vista");
        dto.setCidade("São Paulo");
        dto.setUf("SP");
        dto.setIbge("  3550308  ");

        Cep entity = mapper.toEntity(dto);

        assertThat(entity.getComplemento()).isEqualTo("lado ímpar");
        assertThat(entity.getIbge()).isEqualTo("3550308");
    }

    @Test
    void deveAtualizarEntityComDadosDoDTO() {
        Cep entity = new Cep();
        entity.setId(1L);
        entity.setCodigo("01310100");
        entity.setLogradouro("Avenida Antiga");
        entity.setBairro("Bairro Antigo");
        entity.setCidade("Cidade Antiga");
        entity.setUf("SP");

        CepDTO dto = new CepDTO();
        dto.setLogradouro("Avenida Paulista");
        dto.setComplemento("lado ímpar");
        dto.setBairro("Bela Vista");
        dto.setCidade("São Paulo");
        dto.setUf("SP");
        dto.setIbge("3550308");

        mapper.updateEntity(dto, entity);

        assertThat(entity.getId()).isEqualTo(1L); // ID não deve mudar
        assertThat(entity.getCodigo()).isEqualTo("01310100"); // Código não deve mudar
        assertThat(entity.getLogradouro()).isEqualTo("Avenida Paulista");
        assertThat(entity.getComplemento()).isEqualTo("lado ímpar");
        assertThat(entity.getBairro()).isEqualTo("Bela Vista");
        assertThat(entity.getCidade()).isEqualTo("São Paulo");
        assertThat(entity.getUf()).isEqualTo("SP");
        assertThat(entity.getIbge()).isEqualTo("3550308");
    }

    @Test
    void naoDeveFazerNadaQuandoDTOForNullNoUpdate() {
        Cep entity = new Cep();
        entity.setCodigo("01310100");
        entity.setLogradouro("Avenida Paulista");

        mapper.updateEntity(null, entity);

        assertThat(entity.getLogradouro()).isEqualTo("Avenida Paulista");
    }

    @Test
    void naoDeveFazerNadaQuandoEntityForNullNoUpdate() {
        CepDTO dto = new CepDTO();
        dto.setLogradouro("Avenida Paulista");

        mapper.updateEntity(dto, null);

        // Não deve lançar exceção
        assertThat(dto.getLogradouro()).isEqualTo("Avenida Paulista");
    }

    @Test
    void naoDeveFazerNadaQuandoAmbosSaoNullNoUpdate() {
        mapper.updateEntity(null, null);

        // Não deve lançar exceção
    }
}

