package br.com.arthur.madalena.cepmanager.service;

import br.com.arthur.madalena.cepmanager.dto.CepDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CepService {

    CepDTO findById(Long id);

    CepDTO findByCodigo(String codigo);

    Page<CepDTO> findByLogradouro(String logradouro, Pageable pageable);

    Page<CepDTO> findByCidade(String cidade, Pageable pageable);

    Page<CepDTO> findByCidadeAndUf(String cidade, String uf, Pageable pageable);

    Page<CepDTO> findAll(Pageable pageable);

    Page<CepDTO> search(String termo, Pageable pageable);

    CepDTO create(CepDTO cepDTO);

    CepDTO update(String codigo, CepDTO cepDTO);

    void delete(String codigo);

    boolean existsByCodigo(String codigo);
}

