package br.com.arthur.madalena.cepmanager.dao;

import br.com.arthur.madalena.cepmanager.entity.Cep;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CepDAO extends JpaRepository<Cep, Long> {

    @Query("SELECT c FROM Cep c WHERE c.codigo = :codigo")
    Optional<Cep> findByCodigo(@Param("codigo") String codigo);

    @Query("SELECT c FROM Cep c WHERE LOWER(c.logradouro) LIKE LOWER(CONCAT('%', :logradouro, '%'))")
    Page<Cep> findByLogradouroContaining(@Param("logradouro") String logradouro, Pageable pageable);

    @Query("SELECT c FROM Cep c WHERE LOWER(c.cidade) = LOWER(:cidade)")
    Page<Cep> findByCidade(@Param("cidade") String cidade, Pageable pageable);

    @Query("SELECT c FROM Cep c WHERE c.uf = UPPER(:uf)")
    Page<Cep> findByUf(@Param("uf") String uf, Pageable pageable);

    @Query("SELECT c FROM Cep c WHERE LOWER(c.cidade) = LOWER(:cidade) AND c.uf = UPPER(:uf)")
    Page<Cep> findByCidadeAndUf(@Param("cidade") String cidade, @Param("uf") String uf, Pageable pageable);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM cep WHERE codigo = :codigo)", nativeQuery = true)
    boolean existsByCodigo(@Param("codigo") String codigo);

    @Query(value = "SELECT * FROM cep WHERE " +
            "codigo LIKE CONCAT('%', :termo, '%') " +
            "OR LOWER(logradouro) LIKE LOWER(CONCAT('%', :termo, '%')) " +
            "OR LOWER(bairro) LIKE LOWER(CONCAT('%', :termo, '%')) " +
            "OR LOWER(cidade) LIKE LOWER(CONCAT('%', :termo, '%')) " +
            "OR LOWER(complemento) LIKE LOWER(CONCAT('%', :termo, '%')) " +
            "OR LOWER(uf) LIKE LOWER(CONCAT('%', :termo, '%')) " +
            "OR ibge LIKE CONCAT('%', :termo, '%')",
            nativeQuery = true)
    Page<Cep> searchByTerm(@Param("termo") String termo, Pageable pageable);
}

