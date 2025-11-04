package br.com.arthur.madalena.cepmanager.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CepDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void deveValidarCepDTOCompleto() {
        CepDTO dto = new CepDTO();
        dto.setCodigo("01310100");
        dto.setLogradouro("Avenida Paulista");
        dto.setBairro("Bela Vista");
        dto.setCidade("São Paulo");
        dto.setUf("SP");

        Set<ConstraintViolation<CepDTO>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void deveRejeitarCepVazio() {
        CepDTO dto = new CepDTO();
        dto.setCodigo("");
        dto.setLogradouro("Avenida Paulista");
        dto.setBairro("Bela Vista");
        dto.setCidade("São Paulo");
        dto.setUf("SP");

        Set<ConstraintViolation<CepDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("codigo"));
    }

    @Test
    void deveRejeitarCepComFormatoInvalido() {
        CepDTO dto = new CepDTO();
        dto.setCodigo("123");
        dto.setLogradouro("Avenida Paulista");
        dto.setBairro("Bela Vista");
        dto.setCidade("São Paulo");
        dto.setUf("SP");

        Set<ConstraintViolation<CepDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void deveAceitarCepComHifen() {
        CepDTO dto = new CepDTO();
        dto.setCodigo("01310-100");
        dto.setLogradouro("Avenida Paulista");
        dto.setBairro("Bela Vista");
        dto.setCidade("São Paulo");
        dto.setUf("SP");

        Set<ConstraintViolation<CepDTO>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void deveAceitarCepSemHifen() {
        CepDTO dto = new CepDTO();
        dto.setCodigo("01310100");
        dto.setLogradouro("Avenida Paulista");
        dto.setBairro("Bela Vista");
        dto.setCidade("São Paulo");
        dto.setUf("SP");

        Set<ConstraintViolation<CepDTO>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void deveRejeitarLogradouroVazio() {
        CepDTO dto = new CepDTO();
        dto.setCodigo("01310100");
        dto.setLogradouro("");
        dto.setBairro("Bela Vista");
        dto.setCidade("São Paulo");
        dto.setUf("SP");

        Set<ConstraintViolation<CepDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("logradouro"));
    }

    @Test
    void deveRejeitarLogradouroMuitoLongo() {
        CepDTO dto = new CepDTO();
        dto.setCodigo("01310100");
        dto.setLogradouro("A".repeat(256));
        dto.setBairro("Bela Vista");
        dto.setCidade("São Paulo");
        dto.setUf("SP");

        Set<ConstraintViolation<CepDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().contains("255"));
    }

    @Test
    void deveRejeitarBairroVazio() {
        CepDTO dto = new CepDTO();
        dto.setCodigo("01310100");
        dto.setLogradouro("Avenida Paulista");
        dto.setBairro("");
        dto.setCidade("São Paulo");
        dto.setUf("SP");

        Set<ConstraintViolation<CepDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("bairro"));
    }

    @Test
    void deveRejeitarCidadeVazia() {
        CepDTO dto = new CepDTO();
        dto.setCodigo("01310100");
        dto.setLogradouro("Avenida Paulista");
        dto.setBairro("Bela Vista");
        dto.setCidade("");
        dto.setUf("SP");

        Set<ConstraintViolation<CepDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("cidade"));
    }

    @Test
    void deveRejeitarUfVazia() {
        CepDTO dto = new CepDTO();
        dto.setCodigo("01310100");
        dto.setLogradouro("Avenida Paulista");
        dto.setBairro("Bela Vista");
        dto.setCidade("São Paulo");
        dto.setUf("");

        Set<ConstraintViolation<CepDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("uf"));
    }

    @Test
    void deveRejeitarUfComTamanhoInvalido() {
        CepDTO dto = new CepDTO();
        dto.setCodigo("01310100");
        dto.setLogradouro("Avenida Paulista");
        dto.setBairro("Bela Vista");
        dto.setCidade("São Paulo");
        dto.setUf("SAO");

        Set<ConstraintViolation<CepDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("uf"));
    }

    @Test
    void deveAceitarComplementoVazio() {
        CepDTO dto = new CepDTO();
        dto.setCodigo("01310100");
        dto.setLogradouro("Avenida Paulista");
        dto.setBairro("Bela Vista");
        dto.setCidade("São Paulo");
        dto.setUf("SP");
        dto.setComplemento("");

        Set<ConstraintViolation<CepDTO>> violations = validator.validate(dto);

        // Complemento é opcional
        assertThat(violations).noneMatch(v -> v.getPropertyPath().toString().equals("complemento"));
    }

    @Test
    void deveAceitarIbgeVazio() {
        CepDTO dto = new CepDTO();
        dto.setCodigo("01310100");
        dto.setLogradouro("Avenida Paulista");
        dto.setBairro("Bela Vista");
        dto.setCidade("São Paulo");
        dto.setUf("SP");
        dto.setIbge("");

        Set<ConstraintViolation<CepDTO>> violations = validator.validate(dto);

        // IBGE é opcional
        assertThat(violations).noneMatch(v -> v.getPropertyPath().toString().equals("ibge"));
    }

    @Test
    void deveValidarIbgeCom7Digitos() {
        CepDTO dto = new CepDTO();
        dto.setCodigo("01310100");
        dto.setLogradouro("Avenida Paulista");
        dto.setBairro("Bela Vista");
        dto.setCidade("São Paulo");
        dto.setUf("SP");
        dto.setIbge("3550308");

        Set<ConstraintViolation<CepDTO>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void deveRejeitarIbgeComMaisDe7Caracteres() {
        CepDTO dto = new CepDTO();
        dto.setCodigo("01310100");
        dto.setLogradouro("Avenida Paulista");
        dto.setBairro("Bela Vista");
        dto.setCidade("São Paulo");
        dto.setUf("SP");
        dto.setIbge("12345678");

        Set<ConstraintViolation<CepDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("ibge"));
    }

    @Test
    void deveRejeitarIbgeComCaracteresNaoNumericos() {
        CepDTO dto = new CepDTO();
        dto.setCodigo("01310100");
        dto.setLogradouro("Avenida Paulista");
        dto.setBairro("Bela Vista");
        dto.setCidade("São Paulo");
        dto.setUf("SP");
        dto.setIbge("123456A");

        Set<ConstraintViolation<CepDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("ibge"));
    }

    @Test
    void deveRejeitarIbgeComMenosDe7Digitos() {
        CepDTO dto = new CepDTO();
        dto.setCodigo("01310100");
        dto.setLogradouro("Avenida Paulista");
        dto.setBairro("Bela Vista");
        dto.setCidade("São Paulo");
        dto.setUf("SP");
        dto.setIbge("12345");

        Set<ConstraintViolation<CepDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("ibge"));
    }
}

