package br.com.arthur.madalena.cepmanager.converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CepConverterTest {

    private CepConverter converter;

    @BeforeEach
    void setUp() {
        converter = new CepConverter();
    }

    @Test
    void deveConverterCepComHifenParaNumeros() {
        String resultado = converter.getAsObject(null, null, "01310-100");

        assertThat(resultado).isEqualTo("01310100");
    }

    @Test
    void deveConverterCepSemHifenParaNumeros() {
        String resultado = converter.getAsObject(null, null, "01310100");

        assertThat(resultado).isEqualTo("01310100");
    }

    @Test
    void deveRemoverCaracteresNaoNumericos() {
        String resultado = converter.getAsObject(null, null, "01310-100abc");

        assertThat(resultado).isEqualTo("01310100");
    }

    @Test
    void deveRetornarNullQuandoValueForNull() {
        String resultado = converter.getAsObject(null, null, null);

        assertThat(resultado).isNull();
    }

    @Test
    void deveRetornarNullQuandoValueForVazio() {
        String resultado = converter.getAsObject(null, null, "");

        assertThat(resultado).isNull();
    }

    @Test
    void deveFormatarCepCom8DigitosComHifen() {
        String resultado = converter.getAsString(null, null, "01310100");

        assertThat(resultado).isEqualTo("01310-100");
    }

    @Test
    void deveRetornarValueQuandoNaoTiver8Digitos() {
        String resultado = converter.getAsString(null, null, "0131010");

        assertThat(resultado).isEqualTo("0131010");
    }

    @Test
    void deveRetornarNullQuandoValueForNullNoGetAsString() {
        String resultado = converter.getAsString(null, null, null);

        assertThat(resultado).isNull();
    }

    @Test
    void deveRetornarVazioQuandoValueForVazioNoGetAsString() {
        String resultado = converter.getAsString(null, null, "");

        assertThat(resultado).isEmpty();
    }

    @Test
    void deveManterCepJaFormatado() {
        String resultado = converter.getAsString(null, null, "01310-100");

        assertThat(resultado).isEqualTo("01310-100");
    }

    @Test
    void deveFormatarCepCorretamente() {
        String resultado = converter.getAsString(null, null, "94935912");

        assertThat(resultado).isEqualTo("94935-912");
    }

    @Test
    void deveRemoverTodosCaracteresEspeciais() {
        String resultado = converter.getAsObject(null, null, "013.10-100@#$");

        assertThat(resultado).isEqualTo("01310100");
    }
}

