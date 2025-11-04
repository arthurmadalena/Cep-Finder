package br.com.arthur.madalena.cepmanager.dao;

import br.com.arthur.madalena.cepmanager.entity.Cep;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CepDAOTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CepDAO cepDAO;

    private Cep cepTeste;

    @BeforeEach
    void setUp() {
        cepTeste = new Cep();
        cepTeste.setCodigo("01310100");
        cepTeste.setLogradouro("Avenida Paulista");
        cepTeste.setBairro("Bela Vista");
        cepTeste.setCidade("São Paulo");
        cepTeste.setUf("SP");
        cepTeste.setComplemento("lado ímpar");
        cepTeste.setIbge("3550308");
        
        entityManager.persist(cepTeste);
        entityManager.flush();
    }

    @Test
    void deveBuscarCepPorCodigo() {
        Optional<Cep> resultado = cepDAO.findByCodigo("01310100");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getLogradouro()).isEqualTo("Avenida Paulista");
        assertThat(resultado.get().getCidade()).isEqualTo("São Paulo");
    }

    @Test
    void deveRetornarVazioQuandoCepNaoExiste() {
        Optional<Cep> resultado = cepDAO.findByCodigo("99999999");

        assertThat(resultado).isEmpty();
    }

    @Test
    void deveBuscarPorLogradouro() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Cep> resultado = cepDAO.findByLogradouroContaining("Paulista", pageable);

        assertThat(resultado.getContent()).isNotEmpty();
        assertThat(resultado.getContent().get(0).getLogradouro()).contains("Paulista");
    }

    @Test
    void deveBuscarPorCidade() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Cep> resultado = cepDAO.findByCidade("São Paulo", pageable);

        assertThat(resultado.getContent()).isNotEmpty();
        assertThat(resultado.getContent().get(0).getCidade()).isEqualTo("São Paulo");
    }

    @Test
    void deveBuscarPorUf() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Cep> resultado = cepDAO.findByUf("SP", pageable);

        assertThat(resultado.getContent()).isNotEmpty();
        assertThat(resultado.getContent().get(0).getUf()).isEqualTo("SP");
    }

    @Test
    void deveBuscarPorCidadeEUf() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Cep> resultado = cepDAO.findByCidadeAndUf("São Paulo", "SP", pageable);

        assertThat(resultado.getContent()).isNotEmpty();
        assertThat(resultado.getContent().get(0).getCidade()).isEqualTo("São Paulo");
        assertThat(resultado.getContent().get(0).getUf()).isEqualTo("SP");
    }

    @Test
    void deveVerificarSeCodigoExiste() {
        boolean existe = cepDAO.existsByCodigo("01310100");

        assertThat(existe).isTrue();
    }

    @Test
    void deveRetornarFalseQuandoCodigoNaoExiste() {
        boolean existe = cepDAO.existsByCodigo("99999999");

        assertThat(existe).isFalse();
    }

    @Test
    void deveBuscarPorTermoGeral() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Cep> resultado = cepDAO.searchByTerm("Paulista", pageable);

        assertThat(resultado.getContent()).isNotEmpty();
        assertThat(resultado.getContent().get(0).getLogradouro()).contains("Paulista");
    }

    @Test
    void deveBuscarPorTermoGeralNoBairro() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Cep> resultado = cepDAO.searchByTerm("Bela Vista", pageable);

        assertThat(resultado.getContent()).isNotEmpty();
        assertThat(resultado.getContent().get(0).getBairro()).contains("Bela Vista");
    }

    @Test
    void deveBuscarPorTermoGeralNaCidade() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Cep> resultado = cepDAO.searchByTerm("São Paulo", pageable);

        assertThat(resultado.getContent()).isNotEmpty();
        assertThat(resultado.getContent().get(0).getCidade()).contains("São Paulo");
    }

    @Test
    void deveBuscarPorTermoGeralNoCodigo() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Cep> resultado = cepDAO.searchByTerm("01310", pageable);

        assertThat(resultado.getContent()).isNotEmpty();
        assertThat(resultado.getContent().get(0).getCodigo()).contains("01310");
    }

    @Test
    void deveBuscarPorTermoGeralNoIbge() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Cep> resultado = cepDAO.searchByTerm("3550308", pageable);

        assertThat(resultado.getContent()).isNotEmpty();
        assertThat(resultado.getContent().get(0).getIbge()).contains("3550308");
    }
}

