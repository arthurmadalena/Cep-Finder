package br.com.arthur.madalena.cepmanager.service;

import br.com.arthur.madalena.cepmanager.dao.CepDAO;
import br.com.arthur.madalena.cepmanager.dto.CepDTO;
import br.com.arthur.madalena.cepmanager.entity.Cep;
import br.com.arthur.madalena.cepmanager.exception.BusinessException;
import br.com.arthur.madalena.cepmanager.exception.ResourceNotFoundException;
import br.com.arthur.madalena.cepmanager.mapper.CepMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CepServiceImpl implements CepService {

    private final CepDAO cepDAO;
    private final CepMapper cepMapper;

    @Override
    public CepDTO findById(Long id) {
        log.debug("Buscando CEP por ID: {}", id);
        
        Cep cep = cepDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CEP não encontrado com ID: " + id));
        
        return cepMapper.toDTO(cep);
    }

    @Override
    public CepDTO findByCodigo(String codigo) {
        log.debug("Buscando CEP por código: {}", codigo);
        
        Cep cep = cepDAO.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("CEP não encontrado: " + codigo));
        
        return cepMapper.toDTO(cep);
    }

    @Override
    public Page<CepDTO> findByLogradouro(String logradouro, Pageable pageable) {
        log.debug("Buscando CEPs por logradouro: {}", logradouro);
        
        Page<Cep> ceps = cepDAO.findByLogradouroContaining(logradouro, pageable);
        return ceps.map(cepMapper::toDTO);
    }

    @Override
    public Page<CepDTO> findByCidade(String cidade, Pageable pageable) {
        log.debug("Buscando CEPs por cidade: {}", cidade);
        
        Page<Cep> ceps = cepDAO.findByCidade(cidade, pageable);
        return ceps.map(cepMapper::toDTO);
    }

    @Override
    public Page<CepDTO> findByCidadeAndUf(String cidade, String uf, Pageable pageable) {
        log.debug("Buscando CEPs por cidade: {} e UF: {}", cidade, uf);
        
        Page<Cep> ceps = cepDAO.findByCidadeAndUf(cidade, uf, pageable);
        return ceps.map(cepMapper::toDTO);
    }

    @Override
    public Page<CepDTO> findAll(Pageable pageable) {
        log.debug("Buscando todos os CEPs");
        
        Page<Cep> ceps = cepDAO.findAll(pageable);
        return ceps.map(cepMapper::toDTO);
    }

    @Override
    public Page<CepDTO> search(String termo, Pageable pageable) {
        log.debug("Pesquisando CEPs com termo: {}", termo);
        
        Page<Cep> ceps = cepDAO.searchByTerm(termo, pageable);
        return ceps.map(cepMapper::toDTO);
    }

    @Override
    @Transactional
    public CepDTO create(CepDTO cepDTO) {
        log.debug("Criando CEP: {}", cepDTO.getCodigo());
        
        if (cepDAO.existsByCodigo(cepDTO.getCodigo())) {
            throw new BusinessException("CEP já cadastrado: " + cepDTO.getCodigo());
        }
        
        Cep cep = cepMapper.toEntity(cepDTO);
        cep = cepDAO.save(cep);
        
        log.info("CEP criado com sucesso: {}", cep.getCodigo());
        return cepMapper.toDTO(cep);
    }

    @Override
    @Transactional
    public CepDTO update(String codigo, CepDTO cepDTO) {
        log.debug("Atualizando CEP: {}", codigo);
        
        Cep cep = cepDAO.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("CEP não encontrado: " + codigo));
        
        cepMapper.updateEntity(cepDTO, cep);
        cep = cepDAO.save(cep);
        
        log.info("CEP atualizado com sucesso: {}", cep.getCodigo());
        return cepMapper.toDTO(cep);
    }

    @Override
    @Transactional
    public void delete(String codigo) {
        log.debug("Deletando CEP: {}", codigo);
        
        Cep cep = cepDAO.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("CEP não encontrado: " + codigo));
        
        cepDAO.delete(cep);
        
        log.info("CEP deletado com sucesso: {}", codigo);
    }

    @Override
    public boolean existsByCodigo(String codigo) {
        return cepDAO.existsByCodigo(codigo);
    }
}

