package br.com.arthur.madalena.cepmanager.mapper;

import br.com.arthur.madalena.cepmanager.dto.CepDTO;
import br.com.arthur.madalena.cepmanager.entity.Cep;
import org.springframework.stereotype.Component;

@Component
public class CepMapper {

    public CepDTO toDTO(Cep entity) {
        if (entity == null) {
            return null;
        }

        CepDTO dto = new CepDTO();
        dto.setId(entity.getId());
        dto.setCodigo(entity.getCodigo());
        dto.setLogradouro(entity.getLogradouro());
        dto.setComplemento(entity.getComplemento());
        dto.setBairro(entity.getBairro());
        dto.setCidade(entity.getCidade());
        dto.setUf(entity.getUf());
        dto.setIbge(entity.getIbge());

        return dto;
    }

    public Cep toEntity(CepDTO dto) {
        if (dto == null) {
            return null;
        }

        Cep entity = new Cep();
        entity.setId(dto.getId());
        entity.setCodigo(dto.getCodigo());
        entity.setLogradouro(dto.getLogradouro());
        entity.setComplemento(emptyToNull(dto.getComplemento()));
        entity.setBairro(dto.getBairro());
        entity.setCidade(dto.getCidade());
        entity.setUf(dto.getUf());
        entity.setIbge(emptyToNull(dto.getIbge()));

        return entity;
    }

    public void updateEntity(CepDTO dto, Cep entity) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setLogradouro(dto.getLogradouro());
        entity.setComplemento(emptyToNull(dto.getComplemento()));
        entity.setBairro(dto.getBairro());
        entity.setCidade(dto.getCidade());
        entity.setUf(dto.getUf());
        entity.setIbge(emptyToNull(dto.getIbge()));
    }

    private String emptyToNull(String value) {
        return (value == null || value.trim().isEmpty()) ? null : value.trim();
    }
}

