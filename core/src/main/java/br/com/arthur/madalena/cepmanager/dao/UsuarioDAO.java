package br.com.arthur.madalena.cepmanager.dao;

import br.com.arthur.madalena.cepmanager.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioDAO extends JpaRepository<Usuario, Long> {

    @Query("SELECT u FROM Usuario u WHERE u.username = :username")
    Optional<Usuario> findByUsername(@Param("username") String username);

    @Query("SELECT u FROM Usuario u WHERE u.email = :email")
    Optional<Usuario> findByEmail(@Param("email") String email);

    @Query("SELECT u FROM Usuario u WHERE u.tokenVerificacao = :token")
    Optional<Usuario> findByTokenVerificacao(@Param("token") String token);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM usuario WHERE username = :username)", nativeQuery = true)
    boolean existsByUsername(@Param("username") String username);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM usuario WHERE email = :email)", nativeQuery = true)
    boolean existsByEmail(@Param("email") String email);

    @Query("SELECT u FROM Usuario u WHERE u.ativo = :ativo")
    Page<Usuario> findByAtivo(@Param("ativo") Boolean ativo, Pageable pageable);

    @Query("SELECT u FROM Usuario u WHERE LOWER(u.nomeCompleto) LIKE LOWER(CONCAT('%', :nome, '%'))")
    Page<Usuario> findByNomeCompletoContaining(@Param("nome") String nome, Pageable pageable);

    @Query(value = "SELECT u FROM Usuario u JOIN u.permissoes p WHERE p = :permissao")
    Page<Usuario> findByPermissao(@Param("permissao") String permissao, Pageable pageable);
}
